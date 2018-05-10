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

package me.duncte123.ghostBot.commands.dannyPhantom.image

import com.afollestad.ason.Ason
import com.github.natanbc.reliqua.request.RequestException
import me.duncte123.botCommons.web.WebUtils
import me.duncte123.ghostBot.objects.Command
import me.duncte123.ghostBot.objects.CommandCategory
import me.duncte123.ghostBot.objects.googleSearch.GoogleSearchResults
import me.duncte123.ghostBot.utils.EmbedUtils
import me.duncte123.ghostBot.utils.SpoopyUtils
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.slf4j.LoggerFactory

import javax.validation.constraints.NotNull
import java.util.function.Consumer

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg

abstract class ImageBase extends Command {
    /**
     * This holds the search queries that we have performed so we won't go over quota
     * <p>
     * The key is the query and the values is the response
     */
    static final def searchCache = new HashMap<String, GoogleSearchResults>()

    static final def logger = LoggerFactory.getLogger(ImageBase.class)

    void requestSearch(String query, Consumer<GoogleSearchResults> success, Consumer<RequestException> error) {
        if (searchCache.containsKey(query)) {
            success.accept(searchCache[query])
        } else {
            logger.info("MAKING IMAGE REQUEST: " + query);
            WebUtils.ins.getAson(SpoopyUtils.getGoogleSearchUrl(query)).async({ ason ->
                GoogleSearchResults data = Ason.deserialize(ason, GoogleSearchResults.class, true)
                success.accept(data)
                searchCache[query] = data
            }, error)
        }
    }

    def requestImage(String query) {
        logger.debug("Getting image for '$query'")
        List<String> items = SpoopyUtils.IMAGES.getArray("images.$query")
        return items.get(SpoopyUtils.RANDOM.nextInt(items.size()))
    }

    static void sendMessageFromName(GuildMessageReceivedEvent event, @NotNull LocalImage i) {
        if (i.fileName == null || i.fileName.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `$i.key`")
            return
        }

        logger.debug("Image Link: 'https://i.duncte123.me/${replaceSpaces(i.key)}/$i.fileName'")

        sendEmbed(event,
                EmbedUtils.defaultEmbed()
                        .setTitle(i.key)
                        .setImage("https://i.duncte123.me/${replaceSpaces(i.key)}/$i.fileName")
                        .build()
        )
    }

    void sendMessageFromData(GuildMessageReceivedEvent event, GoogleSearchResults data, String key) {
        List<GoogleSearchResults.SearchItem> arr = data.items
        if (arr == null || arr.isEmpty()) {
            sendMsg(event, "Nothing was found for the search query: `$key`")
            return
        }
        GoogleSearchResults.SearchItem randomItem = arr.get(SpoopyUtils.RANDOM.nextInt(arr.size()))

        assert randomItem != null
        sendEmbed(event, EmbedUtils.defaultEmbed()
                .setTitle(randomItem.title, randomItem.image.contextLink)
                .setImage(randomItem.link).build())
    }

    private static String replaceSpaces(String inp) {
        return inp.replaceAll(" ", "%20")
    }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.IMAGE
    }

    static class LocalImage {
        String key
        String fileName

        LocalImage(String key, String fileName) {
            this.key = key
            this.fileName = fileName
        }
    }
}
