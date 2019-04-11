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

package me.duncte123.ghostbot.objects.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import me.duncte123.ghostbot.utils.RawJsonDeserializer;

public class GhostBotConfig {

    public Discord discord;
    public String api_token;
    public Lavalink lavalink;
    public Api api;
    public boolean running_local;
    public boolean shouldPostStats;
    @JsonDeserialize(using = RawJsonDeserializer.class)
    public String botLists;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Discord {
        public String prefix;
        public int totalShards;
        public String token;
    }

    public static class Lavalink {
        public Node[] nodes;
        public boolean enable;

        public static class Node {
            public String wsUrl;
            public String pass;
        }
    }

    public static class Api {
        public String google;
        public String tumblr;
    }

}
