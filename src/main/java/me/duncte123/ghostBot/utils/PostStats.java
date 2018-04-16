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

import me.duncte123.botCommons.web.WebUtils;
import net.dv8tion.jda.core.JDA;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.json.JSONObject;

public class PostStats {

    public static void toDiscordBots(JDA jda, String apiKey) {

        WebUtils.ins.prepareRaw(
                new Request.Builder()
                        .url("https://discordbots.org/api/bots/" + jda.getSelfUser().getId() + "/stats")
                        .post(RequestBody.create(MediaType.parse("application/json"),
                                new JSONObject().put("server_count", jda.getGuilds().size()).toString()))
                        .addHeader("User-Agent", "DiscordBot " + jda.getSelfUser().getName())
                        .addHeader("Authorization", apiKey)
                        .build(), ResponseBody::string).async(null, Throwable::printStackTrace);
    }
}