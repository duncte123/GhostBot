/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.fandomapi.FandomException;
import me.duncte123.fandomapi.search.LocalWikiSearchResult;
import me.duncte123.fandomapi.search.LocalWikiSearchResultSet;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.utils.WikiHolder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction.OptionData;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.api.entities.Command.OptionType.STRING;

public abstract class WikiBaseCommand extends Command {
    //shortcut to the wiki
    final WikiHolder wiki = new WikiHolder("https://dannyphantom.fandom.com");

    protected void handleWikiSearch(WikiHolder wiki, String searchQuery, ObjectMapper jackson, ICommandEvent event) {
        WebUtils.ins.getJSONObject(String.format(
            "%s?query=%s",
            wiki.getSearchListEndpoint(),
            SpoopyUtils.encodeUrl(searchQuery))

        ).async(
            (json) -> {

                if (json.has("exception")) {
                    final FandomException ex = toEx(json);

                    if (ex.getType().equalsIgnoreCase("NotFoundApiException")) {
                        event.reply("Your search returned no results.");

                        return;
                    }

                    event.reply("An error occurred: " + ex);
                    return;
                }

                try {
                    final var wikiSearchResultSet = jackson.readValue(json.toString(), LocalWikiSearchResultSet.class);
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


                    final EmbedBuilder eb = EmbedUtils.getDefaultEmbed()
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

                    event.reply(eb);
                } catch (IOException e) {
                    event.reply("Something went wrong, please report the following to my developer: " + e.getMessage());
                }
            },
            (it) -> event.reply("Something went wrong: " + it.getMessage())
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.WIKI;
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(
            new OptionData(STRING, "search", "What to search the wiki for").setRequired(true)
        );
    }

    static FandomException toEx(JsonNode json) {
        final JsonNode ex = json.get("exception");

        return new FandomException(
            ex.get("type").asText(),
            ex.get("message").asText(),
            ex.get("code").asInt(),
            ex.get("details").asText(),
            json.get("trace_id").asText()
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
