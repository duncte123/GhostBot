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
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults.SearchItem;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed;
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

abstract class ImageBase extends Command {

    /**
     * This holds the search queries that we have performed so we won't go over quota
     * <p>
     * The key is the query and the values is the response
     */
    static final Map<String, GoogleSearchResults> searchCache = new HashMap<>();

    static final Logger logger = LoggerFactory.getLogger(ImageBase.class);

    void requestSearch(String query, Consumer<GoogleSearchResults> success, Consumer<RequestException> error) {
        if (searchCache.containsKey(query)) {
            success.accept(searchCache.get(query));
        } else {
            logger.info("MAKING IMAGE REQUEST: " + query);
            WebUtils.ins.getAson(SpoopyUtils.getGoogleSearchUrl(query)).async(
                    ason -> {
                        GoogleSearchResults data = Ason.deserialize(ason, GoogleSearchResults.class, true);
                        success.accept(data);
                        searchCache.put(query, data);
                    }, error);
        }
    }

    String requestImage(String query) {
        logger.debug("Getting image for '" + query + "'");
        List<String> items = SpoopyUtils.IMAGES.getArray("images." + query);
        return items.get(SpoopyUtils.random.nextInt(items.size()));
    }

    void sendMessageFromName(GuildMessageReceivedEvent event, @NotNull ImageData i) {
        if (i.fileName == null || i.fileName.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `" + i.key + "`");
            return;
        }

        logger.debug("Image Link: 'https://i.duncte123.me/" + replaceSpaces(i.key) + "/" + i.fileName + "'");

        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(i.key)
                .setImage("https://i.duncte123.me/" + replaceSpaces(i.key) + "/" + i.fileName)
                .build());
    }

    void sendMessageFromData(GuildMessageReceivedEvent event, GoogleSearchResults data, String key) {
        List<SearchItem> arr = data.getItems();
        if (arr == null || arr.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `" + key + "`");
            return;
        }
        SearchItem randomItem = arr.get(SpoopyUtils.random.nextInt(arr.size()));

        assert randomItem != null;
        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(randomItem.getTitle(), randomItem.getImage().getContextLink())
                .setImage(randomItem.getLink()).build());
    }

    private String replaceSpaces(String in) {
        return in.replaceAll(" ", "%20");
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }

    static class ImageData {
        final String fileName;
        final String key;

        ImageData(String fileName, String key) {
            this.fileName = fileName;
            this.key = key;
        }
    }
}
