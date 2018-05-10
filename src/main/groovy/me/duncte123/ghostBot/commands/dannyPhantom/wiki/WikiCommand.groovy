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
 *     but WITHOUT ANY WARRANTY without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostBot.commands.dannyPhantom.wiki

import com.afollestad.ason.Ason
import me.duncte123.botCommons.web.WebUtils
import me.duncte123.fandomApi.models.search.LocalWikiSearchResult
import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet
import me.duncte123.ghostBot.utils.EmbedUtils
import me.duncte123.ghostBot.utils.SpoopyUtils
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.lang3.StringUtils

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg

class WikiCommand extends WikiBaseCommand {
    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `$Variables.PREFIX$name <search term>`")
            return
        }
        String searchQuery = args.join(" ")

        WebUtils.ins.getAson(String.format(
                "%s?query=%s",
                wiki.searchListEndpoint,
                SpoopyUtils.encodeUrl(searchQuery))
        ).async( { ason ->
            if (ason.has("exception")) {
                sendMsg(event, "An error occurred: ${toEx(ason)}")
                return
            }

            LocalWikiSearchResultSet wikiSearchResultSet = Ason.deserialize(ason, LocalWikiSearchResultSet.class, true)

            List<LocalWikiSearchResult> items = wikiSearchResultSet.items
            if (items.size() > 10) {
                List<LocalWikiSearchResult> temp = new ArrayList<>()
                for (int i = 0; i < 10; i++) {
                    temp.add(items[i])
                }
                items.clear()
                items.addAll(temp)
            }

            EmbedBuilder eb = EmbedUtils.defaultEmbed()
                    .setTitle("Query: $searchQuery", "$wiki.domain/wiki/Special:Search?query=" +
                    "${searchQuery.replaceAll(" ", "%20")}")
                    .setAuthor("Requester: " + String.format("%#s", event.getAuthor()),
                    "https://ghostBot.duncte123.me/", event.author.effectiveAvatarUrl)
                    .setDescription("Total results: $wikiSearchResultSet.total\n" +
                    "Current Listed: ${items.size()}\n" +
                    "\n")

            items.each {
                eb.appendDescription("[")
                        .appendDescription(it.title)
                        .appendDescription(" - ")
                        .appendDescription(StringUtils.abbreviate(
                        safeUrl(it.snippet), 50)
                )
                        .appendDescription("](")
                        .appendDescription(safeUrl(it.url))
                        .appendDescription(")\n")
            }
            sendEmbed(event, eb.build())

        },
        {error -> sendMsg(event, "Something went wrong: $error.message")}
        )
    }

    @Override
    String getName() {
        return "wiki"
    }

    @Override
    String[] getAliases() {
        return [
            "wikia",
            "wikisearch",
            "dannyphantomwiki"
        ]
    }

    @Override
    String getHelp() {
        return """Search the Danny Phantom wiki
               Usage `$Variables.PREFIX$name <search term>`
               Example: `$Variables.PREFIX$name Danny`"""
    }

    private static String safeUrl(String inp) {
        return inp
                .replaceAll("<span class=\"searchmatch\">", "**")
                .replaceAll("</span>", "**")
                .replaceAll("\\[", "\\]")
                .replaceAll("]", "\\]")
                .replaceAll("\\(", "\\(")
                .replaceAll("\\)", "\\)")
    }
}
