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

import com.github.natanbc.reliqua.request.RequestException;
import com.google.gson.Gson;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults;
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults.SearchItem;
import me.duncte123.ghostBot.utils.ConfigUtils;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

abstract class ImageBase extends Command {

    private static final Map<String, GoogleSearchResults> searchCache = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ImageBase.class);
    private static final ConfigUtils CU = new ConfigUtils();
    private static final JSONObject IMAGES = CU.loadImages();
    final Gson gson = new Gson();


    void requestSearch(String query, Consumer<GoogleSearchResults> success, Consumer<RequestException> error) {
        if (searchCache.containsKey(query)) {
            success.accept(searchCache.get(query));
        } else {
            logger.info("MAKING IMAGE REQUEST: " + query);
            WebUtils.ins.getJSONObject(SpoopyUtils.getGoogleSearchUrl(query)).async(
                    jsonObject -> {
                        GoogleSearchResults data = gson.fromJson(jsonObject.toString(), GoogleSearchResults.class);
                        success.accept(data);
                        searchCache.put(query, data);
                    }, error);
        }
    }

    ImageData requestImage(String query) {
        logger.debug("Getting image for '" + query + "'");
        JSONArray items = IMAGES.getJSONArray(query);
        JSONObject item = items.getJSONObject(ThreadLocalRandom.current().nextInt(items.length()));
        return gson.fromJson(item.toString(), ImageData.class);
    }

    void sendMessageFromName(GuildMessageReceivedEvent event, @NotNull ImageData i) {
        if (i.title == null || i.title.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `" + i.title + "`");
            return;
        }

        logger.debug("Image Link: '" + i.url + "'");

        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(i.title, i.website)
                .setImage(i.url)
                .build());
    }

    void sendMessageFromData(GuildMessageReceivedEvent event, GoogleSearchResults data, String key) {
        List<SearchItem> arr = data.getItems();
        if (arr == null || arr.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `" + key + "`");
            return;
        }
        SearchItem randomItem = arr.get(ThreadLocalRandom.current().nextInt(arr.size()));

        assert randomItem != null;
        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(randomItem.getTitle(), randomItem.getImage().getContextLink())
                .setImage(randomItem.getLink()).build());
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGE;
    }

    static class ImageData {
        public String title;
        public String url;
        public String website;

    }
}
