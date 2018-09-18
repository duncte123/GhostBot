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

import com.github.natanbc.reliqua.request.RequestException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TumblrUtils {

    private static final String API_URL = "https://api.tumblr.com/v2/blog/%s/posts%s?limit=20&api_key=" +
            SpoopyUtils.config.api.tumblr;
    private static final Gson gson = new Gson();

    /*public static void fetcheAllFromAccount(String domain, @NotNull Consumer<List<TumblrPost>> cb) {
        fetchAllFromAccount(domain, null, cb);
    }*/

    public static void fetchAllFromAccount(String domain, String type, @NotNull Consumer<List<TumblrPost>> cb) {
        List<TumblrPost> response = new ArrayList<>();
        String url = String.format(API_URL,
                domain,
                (type != null && !type.isEmpty() ? "/" + type : "")
        );
        WebUtils.ins.getJSONObject(url).async(json -> {
            JSONObject res = json.getJSONObject("response");
            int total = res.getInt("total_posts");
            JSONArray postsJson = res.getJSONArray("posts");
            List<TumblrPost> firstPosts = gson.fromJson(postsJson.toString(), new TypeToken<List<TumblrPost>>(){}.getType());
            response.addAll(firstPosts);
            for (int i = 20; i <= total; i += 20) {
                JSONObject j = WebUtils.ins.getJSONObject(url + "&offset=" + i).execute();
                JSONArray fetched = j.getJSONObject("response").getJSONArray("posts");
                List<TumblrPost> posts = gson.fromJson(fetched.toString(), new TypeToken<List<TumblrPost>>(){}.getType());
                response.addAll(posts);
            }
            cb.accept(response);
        });
    }

    /*public static void fetchSinglePost(String domain, long id, @NotNull Consumer<TumblrPost> cb) {
        fetchSinglePost(domain, id, cb, null);
    }*/

    public static void fetchSinglePost(String domain, long id, @NotNull Consumer<TumblrPost> cb, Consumer<RequestException> error) {
        String url = String.format(API_URL, domain, "") + "&id=" + id;
        WebUtils.ins.getJSONObject(url).async(json ->
                        cb.accept(
                                gson.fromJson(json.getJSONObject("response")
                                        .getJSONArray("posts").getJSONObject(0).toString(), TumblrPost.class)
                        ),
                error
        );
    }

    public static void fetchLatestPost(String domain, @NotNull Consumer<TumblrPost> cb) {
        String url = String.format(API_URL, domain, "") + "&limit=1";
        WebUtils.ins.getJSONObject(url).async(json ->
                cb.accept(
                        gson.fromJson(json.getJSONObject("response")
                                .getJSONArray("posts").getJSONObject(0).toString(), TumblrPost.class)
                )
        );
    }
}
