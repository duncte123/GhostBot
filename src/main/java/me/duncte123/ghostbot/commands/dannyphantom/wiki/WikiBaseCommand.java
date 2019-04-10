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

package me.duncte123.ghostbot.commands.dannyphantom.wiki;

import com.google.gson.Gson;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.fandomapi.FandomException;
import me.duncte123.fandomapi.search.LocalWikiSearchResult;
import me.duncte123.fandomapi.search.LocalWikiSearchResultSet;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.utils.WikiHolder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public abstract class WikiBaseCommand extends Command {
    //shortcut to the wiki
    protected final WikiHolder wiki = new WikiHolder("https://dannyphantom.fandom.com");
    protected final Gson gson = new Gson();

    protected void handleWikiSearch(WikiHolder wiki, String searchQuery, GuildMessageReceivedEvent event) {
        WebUtils.ins.getJSONObject(String.format(
            "%s?query=%s",
            wiki.getSearchListEndpoint(),
            SpoopyUtils.encodeUrl(searchQuery))

        ).async(
            (json) -> {

                if (json.has("exception")) {
                    final FandomException ex = toEx(json);

                    if (ex.getType().equalsIgnoreCase("NotFoundApiException")) {
                        sendMsg(event, "Your search returned no results.");

                        return;
                    }

                    sendMsg(event, "An error occurred: " + ex);
                    return;
                }

                final LocalWikiSearchResultSet wikiSearchResultSet = gson.fromJson(json.toString(), LocalWikiSearchResultSet.class);

                final List<LocalWikiSearchResult> items = wikiSearchResultSet.getItems();

                if (items.size() > 10) {
                    final List<LocalWikiSearchResult> temp = new ArrayList<>();

                    for (int i = 0; i < 10; i++) {
                        temp.add(items.get(i));
                    }

                    items.clear();
                    items.addAll(temp);
                }

                final User author = event.getAuthor();
                final String authorName = author.getAsTag();
                final String authorIcon = author.getEffectiveAvatarUrl();


                final EmbedBuilder eb = EmbedUtils.defaultEmbed()
                    .setTitle("Query: " + searchQuery,
                        wiki.getDomain() + "/wiki/Special:Search?query=" + searchQuery.replaceAll(" ", "%20"))
                    .setAuthor("Requester: " + authorName,
                        "https://ghostbot.duncte123.me/", authorIcon)
                    .setDescription("Total results: " + wikiSearchResultSet.getTotal() +
                        "\nCurrent Listed: " + items.size() + "\n\n");


                for (LocalWikiSearchResult localWikiSearchResult : items) {
                    eb.appendDescription("[")
                        .appendDescription(localWikiSearchResult.getTitle())
                        .appendDescription(" - ")
                        .appendDescription(StringUtils.abbreviate(safeUrl(localWikiSearchResult.getSnippet()), 50))
                        .appendDescription("](")
                        .appendDescription(safeUrl(localWikiSearchResult.getUrl()))
                        .appendDescription(")\n");

                }

                sendEmbed(event, eb);
            },
            (it) -> sendMsg(event, "Something went wrong: " + it.getMessage())
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.WIKI;
    }

    public static FandomException toEx(JSONObject json) {
        final JSONObject ex = json.getJSONObject("exception");

        return new FandomException(
            ex.getString("type"),
            ex.getString("message"),
            ex.getInt("code"),
            ex.getString("details"),
            json.getString("trace_id")
        );
    }

    private static String safeUrl(String inp) {
        return inp
            .replaceAll("<span class=\"searchmatch\">", "**")
            .replaceAll("</span>", "**")
            .replaceAll("\\[", "\\]")
            .replaceAll("]", "\\]")
            .replaceAll("\\(", "\\(")
            .replaceAll("\\)", "\\)");
    }
}
