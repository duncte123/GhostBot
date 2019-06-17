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

package me.duncte123.ghostbot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.config.ConfigUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;

import java.io.IOException;

public class Container {

    private final GhostBotConfig config;
    private final CommandManager commandManager;
    private final AudioUtils audio;
    private final ObjectMapper jackson;

    public Container() throws IOException {
        this.config = ConfigUtils.loadFromFile("config.json", GhostBotConfig.class);
        this.jackson = new ObjectMapper();
        this.audio = new AudioUtils();
        this.commandManager = new CommandManager(this);
    }

    public GhostBotConfig getConfig() {
        return config;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public AudioUtils getAudio() {
        return audio;
    }

    public ObjectMapper getJackson() {
        return jackson;
    }
}
