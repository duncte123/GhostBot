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

package me.duncte123.ghostBot.commands.dannyPhantom.wiki

import com.afollestad.ason.Ason
import me.duncte123.botCommons.web.WebUtils
import me.duncte123.fandomApi.models.user.UserElement
import me.duncte123.fandomApi.models.user.UserResultSet
import me.duncte123.ghostBot.utils.EmbedUtils
import me.duncte123.ghostBot.utils.SpoopyUtils
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg

class WikiUserCommand extends WikiBaseCommand {
    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `$Variables.PREFIX$name <search term>`")
            return
        }
        String searchQuery = args.join(" ")

        WebUtils.ins.getAson(String.format(
                "%s?ids=%s",
                wiki.userDetailsEndpoint,
                SpoopyUtils.encodeUrl(searchQuery)
        )).async({ ason ->
                if (ason.has("exception")) {
                    sendMsg(event, "An error occurred: ${toEx(ason)}")
                    return
                }

                UserResultSet userResultSet = Ason.deserialize(ason, UserResultSet.class, true)

                if (userResultSet.items.size() == 1) {
                    UserElement user = userResultSet.items[0]
                    user.setBasePath(userResultSet.basepath)
                    sendEmbed(event, EmbedUtils.defaultEmbed()
                            .setThumbnail(user.avatar)
                            .setTitle("Profile link", user.absoluteUrl)
                            .setAuthor(user.name, user.absoluteUrl, user.avatar)
                            .addField("User Info:", String.format("**Name:** %s\n" +
                            "**Id:** %s\n" +
                            "**Title:** %s\n" +
                            "**Number of edits:** %s",
                            user.name,
                            user.userId,
                            user.title,
                            user.numberofedits), false)
                            .build())
                } else {
                    EmbedBuilder eb = EmbedUtils.defaultEmbed()
                            .setTitle("I found the following users:")
                    for (UserElement user : userResultSet.items) {
                        eb.appendDescription("[")
                                .appendDescription(user.name)
                                .appendDescription("](")
                                .appendDescription(userResultSet.basepath + user.url)
                                .appendDescription(")\n")
                    }
                    sendEmbed(event, eb.build())
                }
        },
    { error ->
                sendMsg(event, "Something went wrong: $error.message")/* error.printStackTrace();*/
            }
        )
    }

    @Override
    String getName() {
        return "wikiuser"
    }

    @Override
    String[] getAliases() {
        return [
                "wikiusersearch"
        ]
    }

    @Override
    String getHelp() {
        return """Search wikia for users.
            Usage: `$Variables.PREFIX$name <username/user id>`
            Examples: `$Variables.PREFIX$name duncte123`
            `$Variables.PREFIX$name 34322457`
            """
    }
}
