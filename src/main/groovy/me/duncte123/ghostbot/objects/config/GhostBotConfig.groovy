/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.objects.config

import com.google.gson.JsonElement

class GhostBotConfig {

    public Discord discord
    public Slack slack
    public Lavalink lavalink
    public Api api
    public boolean running_local
    public boolean shouldPostStats
    public JsonElement botLists

    static class Discord {
        public String prefix
        public int totalShards
        public String token
    }

    static class Slack {
        public String token
    }

    static class Lavalink {
        public Node[] nodes
        public boolean enable

        static class Node {
            public String wsUrl
            public String pass
        }
    }

    static class Api {
        public String google
        public String tumblr
    }

}
