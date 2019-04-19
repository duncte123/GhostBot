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

package me.duncte123.ghostbot.commands.dannyphantom.image;

import com.github.natanbc.reliqua.request.RequestException;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.googlesearch.GoogleSearchResults;
import me.duncte123.ghostbot.objects.googlesearch.GoogleSearchResults.SearchItem;
import me.duncte123.ghostbot.utils.ConfigUtils;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public abstract class ImageBase extends Command {

    private final Map<String, GoogleSearchResults> searchCache = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ImageBase.class);
    private static final JSONObject IMAGES = new ConfigUtils().getImages();

    void requestSearch(String query, Consumer<GoogleSearchResults> success, Consumer<RequestException> error) {

        if (searchCache.containsKey(query)) {
            success.accept(searchCache.get(query));

            return;
        }

        logger.info("MAKING IMAGE REQUEST: " + query);

        WebUtils.ins.getText(SpoopyUtils.getGoogleSearchUrl(query)).async(
            (it) -> {
                try {
                    final GoogleSearchResults data = SpoopyUtils.getJackson().readValue(it, GoogleSearchResults.class);
                    success.accept(data);
                    searchCache.put(query, data);
                }
                catch (IOException e){
                    error.accept(new RequestException(e));
                }
            }, error
        );
    }

    ImageData requestImage(String query) {
        logger.debug("Getting image for '" + query + '\'');

        final JSONArray items = IMAGES.getJSONArray(query);
        final JSONObject item = items.getJSONObject(ThreadLocalRandom.current().nextInt(items.length()));

        try {
            return SpoopyUtils.getJackson().readValue(item.toString(), ImageData.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    void sendMessageFromName(CommandEvent event, @NotNull ImageData i) {
        if (i.title == null || i.title.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `" + i.title + '`');

            return;
        }

        logger.debug("Image Link: '" + i.url + '\'');

        sendEmbed(event, EmbedUtils.defaultEmbed()
            .setTitle(i.title, i.website)
            .setImage(i.url));
    }

    void sendImage(CommandEvent event, byte[] data) {
        final String fileName = String.format("%s_%s.png", getName(), System.currentTimeMillis());
        final TextChannel channel = event.getChannel();

        if (event.getSelfMember().hasPermission(channel, Permission.MESSAGE_ATTACH_FILES)) {
            channel.sendFile(data, fileName).queue();
        } else {
            sendMsg(channel, "I need permission to upload files in order for this command to work.");
        }
    }

    static void sendMessageFromData(CommandEvent event, GoogleSearchResults data, String key) {
        final List<SearchItem> arr = data.items;

        if (arr == null || arr.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `" + key + '`');

            return;
        }

        final SearchItem randomItem = arr.get(ThreadLocalRandom.current().nextInt(arr.size()));

        sendEmbed(event, EmbedUtils.defaultEmbed()
            .setTitle(randomItem.title, randomItem.image.contextLink)
            .setImage(randomItem.link));
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGE;
    }

    public static class ImageData {
        public String title;
        public String url;
        public  String website;
    }
}
