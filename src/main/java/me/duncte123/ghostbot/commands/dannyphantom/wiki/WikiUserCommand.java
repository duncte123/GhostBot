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

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.fandomapi.user.UserElement;
import me.duncte123.fandomapi.user.UserResultSet;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.EmbedBuilder;

import java.io.IOException;
import java.util.List;

public class WikiUserCommand extends WikiBaseCommand {
    @Override
    public void execute(ICommandEvent event) {
        final List<String> args = event.getArgs();

        if (args.isEmpty()) {
            event.reply("Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }

        final String searchQuery = String.join(" ", args);

        WebUtils.ins.getJSONObject(String.format(
            "%s?ids=%s",
            wiki.getUserDetailsEndpoint(),
            SpoopyUtils.encodeUrl(searchQuery)
        )).async((json) -> {
                if (json.has("exception")) {
                    event.reply("An error occurred: " + toEx(json));
                    return;
                }

                try {
                    final UserResultSet userResultSet = event.getContainer().getJackson()
                        .readValue(json.toString(), UserResultSet.class);

                    if (userResultSet.getItems().size() == 1) {
                        final UserElement user = userResultSet.getItems().get(0);

                        user.setBasePath(userResultSet.getBasepath());

                        final EmbedBuilder embed = EmbedUtils.getDefaultEmbed()
                            .setThumbnail(user.getAvatar())
                            .setTitle("Profile link", user.getAbsoluteUrl())
                            .setAuthor(user.getName(), user.getAbsoluteUrl(), user.getAvatar())
                            .addField("User Info:", "**Name:** " + user.getName() +
                                "\n**Id:** " + user.getUserId() +
                                "\n**Title:** " + user.getTitle() +
                                "**Number of edits:** " + user.getNumberofedits(), false);

                        event.reply(embed);

                        return;
                    }

                    final EmbedBuilder eb = EmbedUtils.getDefaultEmbed()
                        .setTitle("I found the following users:");

                    for (UserElement user : userResultSet.getItems()) {
                        eb.appendDescription("[")
                            .appendDescription(user.getName())
                            .appendDescription("](")
                            .appendDescription(userResultSet.getBasepath() + user.getUrl())
                            .appendDescription(")\n");

                    }

                    event.reply(eb);
                } catch (IOException e) {
                    event.reply("Something went wrong: " + e.getMessage());
                }

            },
            (it) -> event.reply("Something went wrong: " + it.getMessage())
        );
    }

    @Override
    public String getName() {
        return "wikiuser";
    }

    @Override
    public List<String> getAliases() {
        return List.of("wikiusersearch");
    }

    @Override
    public String getHelp() {
        return "Search wikia for users.\n" +
            "Usage: `" + Variables.PREFIX + getName() + " <username/user id>`\n" +
            "Examples: `" + Variables.PREFIX + getName() + " duncte123`\n" +
            "`" + Variables.PREFIX + getName() + " 34322457`\n";
    }
}
