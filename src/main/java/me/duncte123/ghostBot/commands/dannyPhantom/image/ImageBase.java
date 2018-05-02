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

package me.duncte123.ghostBot.commands.dannyPhantom.image;

import com.afollestad.ason.Ason;
import com.github.natanbc.reliqua.request.RequestException;
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResutls;
import me.duncte123.ghostBot.objects.googleSearch.SearchItem;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

abstract class ImageBase extends Command {

    /**
     * This holds the search queries that we have performed so we won't go over quota
     *
     * The key is the query and the values is the response
     */
    static final Map<String, GoogleSearchResutls> searchCache = new HashMap<>();

    void requestSearch(String query, Consumer<GoogleSearchResutls> success, Consumer<RequestException> error) {
        if(searchCache.containsKey(query)) {
            success.accept(searchCache.get(query));
        } else {
            System.out.println("MAKING IMAGE REQUEST: " + query);
            WebUtils.ins.getAson(SpoopyUtils.getGoogleSearchUrl(query)).async(
                    ason -> {
                        GoogleSearchResutls data = Ason.deserialize(ason, GoogleSearchResutls.class, true);
                        success.accept(data);
                        searchCache.put(query, data);
                    }, error);
        }
    }

    void sendMessageFromData(Message msg, GoogleSearchResutls data, String key) {
        List<SearchItem> arr = data.getItems();
        if (arr == null || arr.isEmpty()) {
            msg.editMessage("Nothing was found for the search query: `" + key + "`").queue();
            return;
        }
        SearchItem randomItem = arr.get(SpoopyUtils.random.nextInt(arr.size()));

        assert randomItem != null;
        msg.editMessage(
                EmbedUtils.defaultEmbed()
                        .setTitle(randomItem.getTitle(), randomItem.getImage().getContextLink())
                        .setImage(randomItem.getLink()).build())
                .override(true)
                .queue();
    }

}
