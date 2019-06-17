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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.natanbc.reliqua.request.RequestException;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TumblrUtils {

    private static final TumblrUtils instance = new TumblrUtils();
    private final String API_URL = "https://api.tumblr.com/v2/blog/%s/posts%s?limit=20" +
        "&api_key=%s";

    public void fetchAllFromAccount(String domain, String type, GhostBotConfig config,
                                    ObjectMapper jackson, @NotNull Consumer<List<TumblrPost>> cb) {
        final List<TumblrPost> response = new ArrayList<>();
        final String url = String.format(API_URL,
            domain,
            (type != null && !type.isEmpty() ? "/" + type : ""),
            config.api.tumblr
        );

        WebUtils.ins.getJSONObject(url).async((it) -> {
            try {
                final JSONObject res = it.getJSONObject("response");
                final int total = res.getInt("total_posts");
                final JSONArray postsJson = res.getJSONArray("posts");
                final List<TumblrPost> firstPosts = jackson.readValue(postsJson.toString(), new TypeReference<List<TumblrPost>>() {});

                response.addAll(firstPosts);

                for (int i = 20; i <= total; i += 20) {
                    final String nextPageUrl = String.format("%s&offset=%s", url, i);
                    final JSONObject j = WebUtils.ins.getJSONObject(nextPageUrl).execute();
                    final JSONArray fetched = j.getJSONObject("response").getJSONArray("posts");
                    final List<TumblrPost> posts = jackson.readValue(fetched.toString(), new TypeReference<List<TumblrPost>>() {});

                    response.addAll(posts);
                }

                cb.accept(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void fetchSinglePost(String domain, long id, GhostBotConfig config,
                                ObjectMapper jackson, @NotNull Consumer<TumblrPost> cb, Consumer<RequestException> error) {
        final String url = String.format(API_URL + "&id=%s", domain, "", config.api.tumblr, id);

        WebUtils.ins.getJSONObject(url).async((it) -> {
            try {
                cb.accept(
                    jackson.readValue(
                        it.getJSONObject("response").getJSONArray("posts").getJSONObject(0).toString()
                        , TumblrPost.class)
                );
            } catch (IOException e) {
                error.accept(new RequestException(e));
            }
        }, error);
    }

    public static TumblrUtils getInstance() {
        return instance;
    }
}
