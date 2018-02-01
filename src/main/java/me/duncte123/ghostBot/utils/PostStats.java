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

import net.dv8tion.jda.core.JDA;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

/**
 * https://gist.github.com/duncte123/d3ebf090cadddd3d91eff1da112960b4
 */
public class PostStats {

    public static void toDiscordBots(JDA jda, String apiKey) {
        Response r = null;
        try {
            r = WebUtils.executeRequest(new Request.Builder()
                    .url("https://discordbots.org/api/bots/" + jda.getSelfUser().getId() + "/stats")
                    .post(RequestBody.create(MediaType.parse("application/json"),
                            new JSONObject().put("server_count", jda.getGuilds().size()).toString()))
                    .addHeader("User-Agent", "DiscordBot " + jda.getSelfUser().getName())
                    .addHeader("Authorization", apiKey)
                    .build());
        } finally {
            if (r != null)
                r.close();
        }

    }
}