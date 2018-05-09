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

package me.duncte123.ghostBot.utils;

import me.duncte123.botCommons.config.Config;
import me.duncte123.botCommons.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class ConfigUtils_java {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils_java.class);

    private Config config;
    private Config images;

    /**
     * This will try to load the bot config and kill the program if it fails
     */
    ConfigUtils_java() {
        try {
            logger.info("Loading config.json");
            this.config = ConfigLoader.getConfig(new File("config.json"));
            this.images = ConfigLoader.getConfig(new File("images.json"));
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
    Config loadConfig() {
        return config;
    }

    Config loadImages() {
        return images;
    }
}
