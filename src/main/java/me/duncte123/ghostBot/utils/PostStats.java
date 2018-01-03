package me.duncte123.ghostBot.utils;

import net.dv8tion.jda.core.JDA;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;

/**
 * https://gist.github.com/duncte123/d3ebf090cadddd3d91eff1da112960b4
 */
public class PostStats {

    private static final OkHttpClient client = new OkHttpClient();

    public static void toDiscordBots(JDA jda, String apiKey) {
        try {
            client.newCall(new Request.Builder()
                    .url("https://discordbots.org/api/bots/"+jda.getSelfUser().getId()+"/stats")
                    .post(RequestBody.create(MediaType.parse("application/json"),
                            new JSONObject().put("server_count", jda.getGuilds().size()).toString()))
                    .addHeader("User-Agent", "DiscordBot " + jda.getSelfUser().getName())
                    .addHeader("Authorization", apiKey)
                    .build()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}