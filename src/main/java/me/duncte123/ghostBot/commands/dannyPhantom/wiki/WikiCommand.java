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

import me.duncte123.fandomApi.FandomApi;
import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.fandomApi.models.FandomResult;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResult;
import me.duncte123.fandomApi.models.search.LocalWikiSearchResultSet;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

public class WikiCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        //
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }
        String searchQuery = StringUtils.join(args, " ");
        FandomResult result = SpoopyUtils.FANDOM_API.searchEndpoints.list(searchQuery);

        if (result instanceof FandomException) {
            //noinspection RedundantCast
            sendMsg(event, "Error: " + ((FandomException) result).toString());
            return;
        } else if (result == null) {
            sendMsg(event, "Something went wrong while looking up data.");
            return;
        }

        LocalWikiSearchResultSet wikiSearchResultSet = (LocalWikiSearchResultSet) result;

        EmbedBuilder eb = EmbedUtils.defaultEmbed()
                .setTitle("Query: " + searchQuery, FandomApi.getWikiUrl() + "/wiki/Special:Search?query=" + searchQuery)
                .setAuthor("Requester: " + String.format("%#s", event.getAuthor()), null, event.getAuthor().getEffectiveAvatarUrl())
                .setDescription("Total results: " + wikiSearchResultSet.getTotal() + "\n" +
                        "Current Listed: " + wikiSearchResultSet.getItems().size() + "\n\n");

        for (LocalWikiSearchResult localWikiSearchResult : wikiSearchResultSet.getItems()) {
            eb.appendDescription("[")
                    .appendDescription(localWikiSearchResult.getTitle())
                    .appendDescription(" - ")
                    .appendDescription(StringUtils.abbreviate(localWikiSearchResult.getSnippet()
                            .replaceAll("<span class=\"searchmatch\">", "**")
                            .replaceAll("</span>", "**"), 50))
                    .appendDescription("](")
                    .appendDescription(localWikiSearchResult.getUrl())
                    .appendDescription(")\n");
        }
        sendEmbed(event, eb.build());
    }

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public Category getCategory() {
        return Category.WIKI;
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
                "Usage `" + Variables.PREFIX + getName() + " <search term>`\n" +
                "Example: `" + Variables.PREFIX + getName() + " Danny`";
    }
}