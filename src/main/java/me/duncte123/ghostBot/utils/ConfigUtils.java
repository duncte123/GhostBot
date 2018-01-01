/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Maurice R S "Sanduhr32"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostBot.utils;

import me.duncte123.ghostBot.config.Config;
import me.duncte123.ghostBot.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConfigUtils {
    private Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

    private Config config;

    /**
     * This will try to load the bot config and kill the program if it fails
     */
    public ConfigUtils() {
        try {
            logger.info("Loading config.json");
            this.config = ConfigLoader.getConfig(new File("config.json"));
            logger.info("Loaded config.json");
        } catch (Exception e) {
            logger.error("Could not load config, aborting", e);
            System.exit(-1);
        }
    }

    /**
     * This will return the config that we have
     *
     * @return the config for the bot
     */
    public Config loadConfig() {
        return config;
    }
}
