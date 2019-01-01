/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.utils

import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ConfigUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class)

    JSONObject images

    /**
     * This will try to load the bot config and kill the program if it fails
     */
    ConfigUtils() {
        try {
            logger.info('Loading images.json')
            def file = new File('images.json').text
            this.images = new JSONObject(file)
            logger.info('Loaded images.json')
        } catch (Exception e) {
            logger.error('Could not load config, aborting', e)
            System.exit(-1)
        }
    }
}
