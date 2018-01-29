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
                    .url("https://discordbots.org/api/bots/"+jda.getSelfUser().getId()+"/stats")
                    .post(RequestBody.create(MediaType.parse("application/json"),
                            new JSONObject().put("server_count", jda.getGuilds().size()).toString()))
                    .addHeader("User-Agent", "DiscordBot " + jda.getSelfUser().getName())
                    .addHeader("Authorization", apiKey)
                    .build());
        } finally {
            if(r != null)
                r.close();
        }

    }
}