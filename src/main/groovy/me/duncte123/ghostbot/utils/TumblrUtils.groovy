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

package me.duncte123.ghostbot.utils

import com.github.natanbc.reliqua.request.RequestException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.objects.tumblr.TumblrPost
import org.jetbrains.annotations.NotNull

import java.util.function.Consumer

class TumblrUtils {

    static final TumblrUtils instance = new TumblrUtils()

    private final String API_URL = "https://api.tumblr.com/v2/blog/%s/posts%s?limit=20" +
            "&api_key=$SpoopyUtils.config.api.tumblr"
    final Gson gson = new Gson()

    /*public void fetcheAllFromAccount(String domain, @NotNull Consumer<List<TumblrPost>> cb) {
        fetchAllFromAccount(domain, null, cb);
    }*/

    void fetchAllFromAccount(String domain, String type, @NotNull Consumer<List<TumblrPost>> cb) {
        def response = new ArrayList<>()
        def url = String.format(API_URL,
                domain,
                (type != null && !type.isEmpty() ? "/" + type : "")
        )

        WebUtils.ins.getJSONObject(url).async {
            def res = it.getJSONObject("response")
            def total = res.getInt("total_posts")
            def postsJson = res.getJSONArray("posts")
            def firstPosts = gson.fromJson(postsJson.toString(), new TypeToken<List<TumblrPost>>() {}.getType())
            response.addAll(firstPosts)

            for (int i = 20; i <= total; i += 20) {
                def j = WebUtils.ins.getJSONObject("$url&offset=$i").execute()
                def fetched = j.getJSONObject("response").getJSONArray("posts")
                def posts = gson.fromJson(fetched.toString(), new TypeToken<List<TumblrPost>>() {}.getType())
                response.addAll(posts)
            }

            cb.accept(response)
        }
    }

    /*public void fetchSinglePost(String domain, long id, @NotNull Consumer<TumblrPost> cb) {
        fetchSinglePost(domain, id, cb, null);
    }*/

    void fetchSinglePost(String domain, long id, @NotNull Consumer<TumblrPost> cb, Consumer<RequestException> error) {
        def url = "${String.format(API_URL, domain, "")}&id=$id"

        WebUtils.ins.getJSONObject(url).async({
            cb.accept(
                    gson.fromJson(it.getJSONObject("response")
                            .getJSONArray("posts").getJSONObject(0).toString(), TumblrPost.class)
            )
        }, error)
    }

    /*void fetchLatestPost(String domain, @NotNull Consumer<TumblrPost> cb) {
        def url = "${String.format(API_URL, domain, "")}&limit=1"

        WebUtils.ins.getJSONObject(url).async {
            cb.accept(
                    gson.fromJson(it.getJSONObject("response")
                            .getJSONArray("posts").getJSONObject(0).toString(), TumblrPost.class)
            )
        }
    }*/

}
