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
import me.duncte123.ghostBot.objects.Command_java;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults_java;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults_java.SearchItem;
import me.duncte123.ghostBot.utils.EmbedUtils_java;
import me.duncte123.ghostBot.utils.SpoopyUtils_java;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

abstract class ImageBase_java extends Command_java {

    /**
     * This holds the search queries that we have performed so we won't go over quota
     * <p>
     * The key is the query and the values is the response
     */
    static final Map<String, GoogleSearchResults_java> searchCache = new HashMap<>();

    static final Logger logger = LoggerFactory.getLogger(ImageBase_java.class);

    void requestSearch(String query, Consumer<GoogleSearchResults_java> success, Consumer<RequestException> error) {
        if (searchCache.containsKey(query)) {
            success.accept(searchCache.get(query));
        } else {
            logger.info("MAKING IMAGE REQUEST: " + query);
            WebUtils.ins.getAson(SpoopyUtils_java.getGoogleSearchUrl(query)).async(
                    ason -> {
                        GoogleSearchResults_java data = Ason.deserialize(ason, GoogleSearchResults_java.class, true);
                        success.accept(data);
                        searchCache.put(query, data);
                    }, error);
        }
    }

    String requestImage(String query) {
        logger.debug("Getting image for '" + query + "'");
        List<String> items = SpoopyUtils_java.IMAGES.getArray("images." + query);
        return items.get(SpoopyUtils_java.random.nextInt(items.size()));
    }

    void sendMessageFromName(Message msg, String fileName, String key) {
        if (fileName == null || fileName.isEmpty()) {
            msg.editMessage("Nothing was found for the search query: `" + key + "`").queue();
            return;
        }

        logger.debug("Image Link: 'https://i.duncte123.me/" + replaceSpaces(key) + "/" + fileName + "'");

        msg.editMessage(
                EmbedUtils_java.defaultEmbed()
                        .setTitle(key)
                        .setImage("https://i.duncte123.me/" + replaceSpaces(key) + "/" + fileName)
                        .build())
                .override(true)
                .queue();
    }

    void sendMessageFromData(Message msg, GoogleSearchResults_java data, String key) {
        List<SearchItem> arr = data.getItems();
        if (arr == null || arr.isEmpty()) {
            msg.editMessage("Nothing was found for the search query: `" + key + "`").queue();
            return;
        }
        SearchItem randomItem = arr.get(SpoopyUtils_java.random.nextInt(arr.size()));

        assert randomItem != null;
        msg.editMessage(
                EmbedUtils_java.defaultEmbed()
                        .setTitle(randomItem.getTitle(), randomItem.getImage().getContextLink())
                        .setImage(randomItem.getLink()).build())
                .override(true)
                .queue();
    }

    private String replaceSpaces(String in) {
        return in.replaceAll(" ", "%20");
    }

}
