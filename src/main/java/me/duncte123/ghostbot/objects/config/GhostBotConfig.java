/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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

public class GhostBotConfig {

    public Discord discord;
    public String api_token;
    public Lavalink lavalink;
    public Api api;
    public boolean running_local;
    public boolean shouldPostStats;
    public String botLists;

    public static class Discord {
        public String prefix;
        public int totalShards;
        public String token;
    }

    public static class Lavalink {
        public Node[] nodes;
        public boolean enable;

        public static class Node {
            public String name;
            public String wsUrl;
            public String pass;
        }
    }

    public static class Api {
        public String google;
        public String tumblr;
    }

    public static GhostBotConfig fromEnv() {
        final GhostBotConfig config = new GhostBotConfig();
        final Discord discord = new Discord();

        discord.token = System.getenv("BOT_TOKEN");
        discord.totalShards = Integer.parseInt(System.getenv("BOT_TOTAL_SHARDS"));
        discord.prefix = System.getenv("BOT_PREFIX");
        config.discord = discord;

        config.api_token = System.getenv("API_TOKEN");

        final Lavalink lavalink = new Lavalink();

        lavalink.enable = Boolean.parseBoolean(System.getenv("LAVALINK_ENABLE"));
        final int count = Integer.parseInt(System.getenv("LAVALINK_NODE_COUNT"));
        lavalink.nodes = new Lavalink.Node[count];

        for (int i = 0; i < count; i++) {
            lavalink.nodes[i] = new Lavalink.Node();
            lavalink.nodes[i].name = System.getenv("LAVALINK_NODE_"+i+"_NAME");
            lavalink.nodes[i].wsUrl = System.getenv("LAVALINK_NODE_"+i+"_HOST");
            lavalink.nodes[i].pass = System.getenv("LAVALINK_NODE_"+i+"_PASS");
        }

        config.lavalink = lavalink;

        final Api api = new Api();

        api.google = System.getenv("API_GOOGLE");
        api.tumblr = System.getenv("API_TUMBLR");
        config.api = api;

        config.running_local = Boolean.parseBoolean(System.getenv("RUNNING_LOCAL"));
        config.shouldPostStats = Boolean.parseBoolean(System.getenv("SHOULD_POST_STATS"));
        config.botLists = System.getenv("BOT_LISTS_JSON");

        return config;
    }
}
