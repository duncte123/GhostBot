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

package me.duncte123.ghostBot.commands.dannyPhantom.wiki;

import com.google.gson.Gson;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResult;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.utils.WikiHolder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

/**
 * This class stores objects that are useful to the wiki commands
 */
public abstract class WikiBaseCommand extends Command {

    Gson gson = new Gson();

    //shortcut to the wiki
    WikiHolder wiki = new WikiHolder("https://dannyphantom.fandom.com");

    protected void handleWikiSearch(WikiHolder wiki, String searchQuery, GuildMessageReceivedEvent event) {
        WebUtils.ins.getJSONObject(String.format(
                "%s?query=%s",
                wiki.getSearchListEndpoint(),
                SpoopyUtils.encodeUrl(searchQuery))
        ).async(json -> {
                    if (json.has("exception")) {
                        FandomException ex = toEx(json);
                        if (ex.getType().equalsIgnoreCase("NotFoundApiException")) {
                            sendMsg(event, "Your search returned no results.");
                        } else {
                            sendMsg(event, "An error occurred: " + ex);
                        }
                        return;
                    }

                    LocalWikiSearchResultSet wikiSearchResultSet = gson.fromJson(json.toString(), LocalWikiSearchResultSet.class);

                    List<LocalWikiSearchResult> items = wikiSearchResultSet.getItems();
                    if (items.size() > 10) {
                        List<LocalWikiSearchResult> temp = new ArrayList<>();
                        for (int i = 0; i < 10; i++) {
                            temp.add(items.get(i));
                        }
                        items.clear();
                        items.addAll(temp);
                    }

                    EmbedBuilder eb = EmbedUtils.defaultEmbed()
                            .setTitle("Query: " + searchQuery, wiki.getDomain() +
                                    "/wiki/Special:Search?query=" + searchQuery.replaceAll(" ", "%20"))
                            .setAuthor("Requester: " + String.format("%#s", event.getAuthor()),
                                    "https://ghostbot.duncte123.me/", event.getAuthor().getEffectiveAvatarUrl())
                            .setDescription("Total results: " + wikiSearchResultSet.getTotal() + "\n" +
                                    "Currently Listed: " + items.size() + "\n\n");


                    for (LocalWikiSearchResult localWikiSearchResult : items) {
                        eb.appendDescription("[")
                                .appendDescription(localWikiSearchResult.getTitle())
                                .appendDescription(" - ")
                                .appendDescription(StringUtils.abbreviate(
                                        safeUrl(localWikiSearchResult.getSnippet()), 50)
                                )
                                .appendDescription("](")
                                .appendDescription(safeUrl(localWikiSearchResult.getUrl()))
                                .appendDescription(")\n");
                    }
                    sendEmbed(event, eb.build());

                },
                error -> sendMsg(event, "Something went wrong: " + error.getMessage())
        );
    }

    FandomException toEx(JSONObject json) {
        JSONObject ex = json.getJSONObject("exception");
        return new FandomException(
                ex.getString("type"),
                ex.getString("message"),
                ex.getInt("code"),
                ex.getString("details"),
                json.getString("trace_id")
        );
    }

    private String safeUrl(String in) {
        return in
                .replaceAll("<span class=\"searchmatch\">", "**")
                .replaceAll("</span>", "**")
                .replaceAll("\\[", "\\]")
                .replaceAll("]", "\\]")
                .replaceAll("\\(", "\\(")
                .replaceAll("\\)", "\\)");
    }
}
