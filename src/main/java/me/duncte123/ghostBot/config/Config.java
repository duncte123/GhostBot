/*
 * GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018  Duncan "duncte123" Sterken
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostBot.config;

import com.afollestad.ason.Ason;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Config {

    protected final Ason config;
    private final Config parent;

    protected Config(Config parent, Ason config) {
        this.parent = parent;
        this.config = config;
    }

    /**
     * Replaces the last thing in a string
     *
     * @param text        the text to replace
     * @param regex       the regex or something
     * @param replacement what to replace it with
     * @return the replaced string
     */
    public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    /**
     * This will try to get data from the config file
     *
     * @param key the key where the setting is located
     * @return the value of the setting
     */
    public String getString(String key) {
        return config.getString(key);
    }

    /**
     * This will try to get data from the config file
     *
     * @param key          the key where the setting is located
     * @param defaultValue If this can't be found we will create the option in the config
     * @return the value of the setting
     */
    public final String getString(String key, String defaultValue) {
        if (!this.hasKey(key)) {
            this.put(key, defaultValue);
        }
        try {
            return this.getString(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * This will attempt to get an integer from the config file
     *
     * @param key The key to get the int from
     * @return the int
     * @throws NumberFormatException if the returned value isn't valis
     */
    public int getInt(String key) throws NumberFormatException {
        try {
            return config.getInt(key);
        } catch (final NumberFormatException e) {
            throw e;
        }
    }

    /**
     * This will attempt to get an integer from the config file
     *
     * @param key          The key to get the int from
     * @param defaultValue the value to put it on if the int can't be found
     * @return the int
     */
    public final int getInt(String key, int defaultValue) {
        if (!this.hasKey(key))
            this.put(key, defaultValue);
        return this.getInt(key);
    }

    /**
     * This will attempt to get a boolean from the config file
     *
     * @param key the key to get the boolean from
     * @return the boolean from the key
     */
    public boolean getBoolean(String key) {
        try {
            return config.getBool(key);
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This will attempt to get a boolean from the config file
     *
     * @param key          the key to get the boolean from
     * @param defaultValue the default value to put the boolean on when it can't be found
     * @return the boolean from the key
     */
    public final boolean getBoolean(String key, boolean defaultValue) {
        if (!this.hasKey(key))
            this.put(key, defaultValue);
        return this.getBoolean(key);
    }

    /**
     * This will check if the key that we are looking for
     *
     * @param key the key to find
     * @return true if the key is there
     */
    public boolean hasKey(String key) {
        try {
            return config.has(key);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * This will attempt to put a value is the config
     *
     * @param key   the key to add the value under
     * @param value the value that we need to add, in the form of json
     */
    public void put(String key, Object value) {
        config.put(key, value);
        final String finalKey = key.substring(key.lastIndexOf(".") + 1);
        key = replaceLast(key, finalKey, "");
        if (key.endsWith("."))
            key = replaceLast(key, ".", "");
        final String[] path = key.split("\\.");
        JSONObject current = this.config.toStockJson();

        try {
            for (String element : path) {
                if (element.trim().isEmpty())
                    continue;
                if (element.endsWith("]") && element.contains("[")) {
                    final int i = element.lastIndexOf("[");
                    int index;
                    try {
                        index = Integer.parseInt(element.substring(i).replace("[", "").replace("]", ""));
                    } catch (final Exception e) {
                        index = -1;
                    }
                    element = element.substring(0, i);

                    if (!current.has(element))
                        current.put(element, new JSONArray());
                    final JSONArray array = current.getJSONArray(element);
                    if (index == -1) {
                        final JSONObject object = new JSONObject();
                        array.put(object);
                        current = object;
                    } else {
                        if (index == array.length())
                            array.put(new JSONObject());
                        current = array.getJSONObject(index);
                    }

                } else {
                    if (!current.has(element))
                        current.put(element, new JSONObject());
                    current = current.getJSONObject(element);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
        current.put(finalKey, value);
        try {
            this.save();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * get the config as a file
     *
     * @return the config as a file
     */
    public File getConfigFile() {
        return this.parent.getConfigFile();
    }

    /**
     * save the config
     *
     * @throws Exception when things break
     */
    public void save() throws Exception {
        this.parent.save();
    }
}