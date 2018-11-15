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

package me.duncte123.ghostbot.commands.dannyphantom.wiki

import com.google.gson.Gson
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.fandomapi.FandomException
import me.duncte123.fandomapi.search.LocalWikiSearchResult
import me.duncte123.fandomapi.search.LocalWikiSearchResultSet
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.utils.WikiHolder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

abstract class WikiBaseCommand extends Command {

    Gson gson = new Gson()

    //shortcut to the wiki
    WikiHolder wiki = new WikiHolder('https://dannyphantom.fandom.com')


    protected void handleWikiSearch(WikiHolder wiki, String searchQuery, GuildMessageReceivedEvent event) {
        WebUtils.ins.getJSONObject(String.format(
                '%s?query=%s',
                wiki.searchListEndpoint,
                SpoopyUtils.encodeUrl(searchQuery))

        ).async(
                { json ->

                    if (json.has('exception')) {
                        def ex = toEx(json)

                        if (ex.type.equalsIgnoreCase('NotFoundApiException')) {
                            sendMsg(event, 'Your search returned no results.')
                            return
                        }

                        sendMsg(event, "An error occurred: $ex")

                        return
                    }

                    def wikiSearchResultSet = gson.fromJson(json.toString(), LocalWikiSearchResultSet.class)

                    def items = wikiSearchResultSet.getItems()

                    if (items.size() > 10) {
                        def temp = new ArrayList<LocalWikiSearchResult>()

                        for (int i = 0; i < 10; i++) {
                            temp.add(items.get(i))
                        }

                        items.clear()
                        items.addAll(temp)
                    }

                    def eb = EmbedUtils.defaultEmbed().with {
                        setTitle("Query: $searchQuery",
                                "$wiki.domain/wiki/Special:Search?query=${searchQuery.replaceAll(' ', '%20')}")
                        setAuthor("Requester: ${String.format('%#s', event.author)}",
                                'https://ghostbot.duncte123.me/', event.author.effectiveAvatarUrl)
                        setDescription("Total results: $wikiSearchResultSet.total\n" +
                                "Current Listed: ${items.size()}\n\n")
                    }


                    for (LocalWikiSearchResult localWikiSearchResult : items) {
                        eb.with {
                            appendDescription('[')
                            appendDescription(localWikiSearchResult.title)
                            appendDescription(' - ')
                            appendDescription(StringUtils.abbreviate(safeUrl(localWikiSearchResult.snippet), 50))
                            appendDescription('](')
                            appendDescription(safeUrl(localWikiSearchResult.url))
                            appendDescription(')\n')
                        }
                    }

                    sendEmbed(event, eb.build())
                },
                {
                    sendMsg(event, "Something went wrong: $it.message")
                }
        )
    }

    static FandomException toEx(JSONObject json) {
        JSONObject ex = json.getJSONObject('exception')
        return new FandomException(
                ex.getString('type'),
                ex.getString('message'),
                ex.getInt('code'),
                ex.getString('details'),
                json.getString('trace_id')
        )
    }

    private static String safeUrl(String inp) {
        return inp
                .replaceAll('<span class="searchmatch">', '**')
                .replaceAll('</span>', '**')
                .replaceAll('\\[', '\\]')
                .replaceAll(']', '\\]')
                .replaceAll('\\(', '\\(')
                .replaceAll('\\)', '\\)')
    }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.WIKI
    }
}
