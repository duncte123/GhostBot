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

import com.afollestad.ason.Ason;
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResult;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet;
import me.duncte123.ghostBot.objects.Category_java;
import me.duncte123.ghostBot.utils.EmbedUtils_java;
import me.duncte123.ghostBot.utils.SpoopyUtils_java;
import me.duncte123.ghostBot.variables.Variables_java;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static me.duncte123.ghostBot.utils.MessageUtils_java.sendEmbed;
import static me.duncte123.ghostBot.utils.MessageUtils_java.sendMsg;

public class WikiCommandJava extends WikiBaseCommandJava {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        //
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables_java.PREFIX + getName() + " <search term>`");
            return;
        }
        String searchQuery = StringUtils.join(args, " ");

        WebUtils.ins.getAson(String.format(
                "%s?query=%s",
                wiki.getSearchListEndpoint(),
                SpoopyUtils_java.encodeUrl(searchQuery))
        ).async(
                ason -> {
                    if (ason.has("exception")) {
                        sendMsg(event, "An error occurred: " + toEx(ason));
                        return;
                    }

                    LocalWikiSearchResultSet wikiSearchResultSet = Ason.deserialize(ason, LocalWikiSearchResultSet.class, true);

                    List<LocalWikiSearchResult> items = wikiSearchResultSet.getItems();
                    if (items.size() > 10) {
                        List<LocalWikiSearchResult> temp = new ArrayList<>();
                        for (int i = 0; i < 10; i++) {
                            temp.add(items.get(i));
                        }
                        items.clear();
                        items.addAll(temp);
                    }

                    EmbedBuilder eb = EmbedUtils_java.defaultEmbed()
                            .setTitle("Query: " + searchQuery, wiki.getDomain() +
                                    "/wiki/Special:Search?query=" + searchQuery.replaceAll(" ", "%20"))
                            .setAuthor("Requester: " + String.format("%#s", event.getAuthor()),
                                    "https://ghostBot.duncte123.me/", event.getAuthor().getEffectiveAvatarUrl())
                            .setDescription("Total results: " + wikiSearchResultSet.getTotal() + "\n" +
                                    "Current Listed: " + items.size() + "\n\n");


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

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public Category_java getCategory() {
        return Category_java.WIKI;
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "wikia",
                "wikisearch",
                "dannyphantomwiki"
        };
    }

    @Override
    public String getHelp() {
        return "Search the Danny Phantom wiki\n" +
                "Usage `" + Variables_java.PREFIX + getName() + " <search term>`\n" +
                "Example: `" + Variables_java.PREFIX + getName() + " Danny`";
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
