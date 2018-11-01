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

package me.duncte123.ghostbot.commands.dannyphantom.image

import com.github.natanbc.reliqua.request.RequestException
import com.google.gson.Gson
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.objects.googlesearch.GoogleSearchResults
import me.duncte123.ghostbot.utils.ConfigUtils
import me.duncte123.ghostbot.utils.SpoopyUtils
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.jetbrains.annotations.NotNull
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

abstract class ImageBase extends Command {

    final Map<String, GoogleSearchResults> searchCache = new HashMap<>()
    private static final Logger logger = LoggerFactory.getLogger(ImageBase.class)
    private static final JSONObject IMAGES = new ConfigUtils().images
    final Gson gson = new Gson()

    void requestSearch(String query, Consumer<GoogleSearchResults> success, Consumer<RequestException> error) {

        if (searchCache.containsKey(query)) {
            success.accept(searchCache.get(query))

            return
        }

        logger.info("MAKING IMAGE REQUEST: $query")

        WebUtils.ins.getJSONObject(SpoopyUtils.getGoogleSearchUrl(query)).async(
                {
                    def data = gson.fromJson(it.toString(), GoogleSearchResults.class)
                    success.accept(data)
                    searchCache.put(query, data)
                }, error
        )
    }

    ImageData requestImage(String query) {
        logger.debug("Getting image for '$query'")

        def items = IMAGES.getJSONArray(query)
        def item = items.getJSONObject(ThreadLocalRandom.current().nextInt(items.length()))

        return gson.fromJson(item.toString(), ImageData.class)
    }

    void sendMessageFromName(GuildMessageReceivedEvent event, @NotNull ImageData i) {
        if (i.title == null || i.title.empty) {
            sendMsg(event, "Nothing was found for the search query: `$i.title`")
            return
        }

        logger.debug("Image Link: '$i.url'")

        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(i.title, i.website)
                .setImage(i.url)
                .build())
    }

    void sendMessageFromData(GuildMessageReceivedEvent event, GoogleSearchResults data, String key) {
        def arr = data.items

        if (arr == null || arr.empty) {
            sendMsg(event, "Nothing was found for the search query: `$key`")
            return
        }

        def randomItem = arr.get(ThreadLocalRandom.current().nextInt(arr.size()))

        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(randomItem.title, randomItem.image.contextLink)
                .setImage(randomItem.link).build())
    }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.IMAGE
    }

    class ImageData {
        String title
        String url
        String website
    }
}
