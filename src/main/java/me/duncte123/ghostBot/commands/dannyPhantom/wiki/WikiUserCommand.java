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

import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.fandomapi.user.UserElement;
import me.duncte123.fandomapi.user.UserResultSet;
import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class WikiUserCommand extends WikiBaseCommand {

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }
        String searchQuery = StringUtils.join(args, " ");

        WebUtils.ins.getJSONObject(String.format(
                "%s?ids=%s",
                wiki.getUserDetailsEndpoint(),
                SpoopyUtils.encodeUrl(searchQuery)
        )).async(json -> {
                    if (json.has("exception")) {
                        sendMsg(event, "An error occurred: " + toEx(json));
                        return;
                    }

                    UserResultSet userResultSet = gson.fromJson(json.toString(), UserResultSet.class);

                    if (userResultSet.getItems().size() == 1) {
                        UserElement user = userResultSet.getItems().get(0);
                        user.setBasePath(userResultSet.getBasepath());

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
                                    .appendDescription(userResultSet.getBasepath() + user.getUrl())
                                    .appendDescription(")\n");
                        }
                        sendEmbed(event, eb.build());
                    }
                },
                error -> {
                    sendMsg(event, "Something went wrong: " + error.getMessage());/* error.printStackTrace();*/
                }
        );
    }

    @Override
    public String getName() {
        return "wikiuser";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.WIKI;
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
