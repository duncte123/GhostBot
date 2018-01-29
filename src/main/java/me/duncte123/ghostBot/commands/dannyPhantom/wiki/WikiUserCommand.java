package me.duncte123.ghostBot.commands.dannyPhantom.wiki;

import me.duncte123.fandomApi.models.FandomException;
import me.duncte123.fandomApi.models.FandomResult;
import me.duncte123.fandomApi.models.user.UserElement;
import me.duncte123.fandomApi.models.user.UserResultSet;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

public class WikiUserCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }
        String searchQuery = StringUtils.join(args, " ");
        FandomResult result = SpoopyUtils.FANDOM_API.userEndpoints.details(searchQuery);

        if (result instanceof FandomException) {
            FandomException ex = (FandomException) result;
            if (ex.getCode() == 404)
                sendMsg(event, "No users with this username found");
            else
                sendMsg(event, "Error: " + result);
            return;
        } else if (result == null) {
            sendMsg(event, "Something went wrong while looking up data.");
            return;
        }

        UserResultSet userResultSet = (UserResultSet) result;

        if (userResultSet.getItems().size() == 1) {
            UserElement user = userResultSet.getItems().get(0);
            sendEmbed(event, EmbedUtils.defaultEmbed()
                    .setThumbnail(user.getAvatar())
                    .setTitle("Profile link", user.getAbsoluteUrl())
                    .setAuthor(user.getName(), user.getAbsoluteUrl(), user.getAvatar())
                    .addField("User Info:", String.format("**Name:** %s\n" +
                                    "**Id:** %s\n" +
                                    "**Title:** %s\n" +
                                    "**Number of edits:** %s",
                            user.getName(),
                            user.getUserId(),
                            user.getTitle(),
                            user.getNumberofedits()), false)
                    .build());
        } else {
            EmbedBuilder eb = EmbedUtils.defaultEmbed().setTitle("I found the following users:");
            for (UserElement user : userResultSet.getItems()) {
                eb.appendDescription("[")
                        .appendDescription(user.getName())
                        .appendDescription("](")
                        .appendDescription(user.getAbsoluteUrl())
                        .appendDescription(")\n");
            }
            sendEmbed(event, eb.build());
        }
    }

    @Override
    public String getName() {
        return "wikiuser";
    }

    @Override
    public Category getCategory() {
        return Category.WIKI;
    }

    @Override
    public String[] getAliases() {
        return new String[]{
                "wikiusersearch"
        };
    }

    @Override
    public String getHelp() {
        return "Search wikia for users.\n" +
                "Usage: `" + Variables.PREFIX + getName() + " <username/user id>`\n" +
                "Examples: `" + Variables.PREFIX + getName() + " duncte123`\n" +
                "`" + Variables.PREFIX + getName() + " 34322457`\n";
    }
}
