package me.duncte123.ghostBot.commands.dannyPhantom.wiki;

import me.duncte123.fandomApi.FandomApi;
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
        if(args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }
        String searchQuery = StringUtils.join(args, " ");
        LocalWikiSearchResultSet wikiSearchResultSet = SpoopyUtils.FANDOM_API.searchEndpoints.list(searchQuery);

        if(wikiSearchResultSet == null) {
            sendMsg(event, "No results found on <" + FandomApi.getWikiUrl() + ">");
            return;
        }

        EmbedBuilder eb = EmbedUtils.defaultEmbed()
                .setTitle("Query: " + searchQuery, FandomApi.getWikiUrl() + "/wiki/Special:Search?query=" + searchQuery)
                .setAuthor("Requester: " + String.format("%#s", event.getAuthor()), null, event.getAuthor().getEffectiveAvatarUrl())
                .setDescription("Total results: " + wikiSearchResultSet.getTotal() + "\n" +
                        "Current Listed: " + wikiSearchResultSet.getItems().size() + "\n\n");

        for(LocalWikiSearchResult localWikiSearchResult : wikiSearchResultSet.getItems()) {
            eb.appendDescription("[")
            .appendDescription(localWikiSearchResult.getTitle())
            .appendDescription(" - ")
            .appendDescription(StringUtils.abbreviate(localWikiSearchResult.getSnippet()
                    .replaceAll("<span class=\"searchmatch\">", "**")
                    .replaceAll("</span>","**"), 50))
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
        return Category.TEXT;
    }

    @Override
    public String[] getAliases() {
        return new String[] {
                "wikia",
                "dannyphantomwiki"
        };
    }

    @Override
    public String getHelp() {
        return "Search the danny phantom wiki\n" +
                "Usage `" + Variables.PREFIX + getName() + " <search term>`\n" +
                "Example: `" + Variables.PREFIX + getName() + " Danny`";
    }
}
