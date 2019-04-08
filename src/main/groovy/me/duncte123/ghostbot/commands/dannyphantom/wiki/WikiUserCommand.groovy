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

package me.duncte123.ghostbot.commands.dannyphantom.wiki

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.fandomapi.user.UserElement
import me.duncte123.fandomapi.user.UserResultSet
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.variables.Variables

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class WikiUserCommand extends WikiBaseCommand {
    @Override
    void execute(CommandEvent commandEvent) {

        def event = commandEvent.event

        if (commandEvent.args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `$Variables.PREFIX$name <search term>`")
            return
        }

        def searchQuery = commandEvent.args.join(' ')

        WebUtils.ins.getJSONObject(String.format(
            '%s?ids=%s',
            wiki.userDetailsEndpoint,
            SpoopyUtils.encodeUrl(searchQuery)
        )).async(
            { json ->

                if (json.has('exception')) {
                    sendMsg(event, "An error occurred: ${toEx(json)}")
                    return
                }

                UserResultSet userResultSet = gson.fromJson(json.toString(), UserResultSet.class)

                if (userResultSet.items.size() == 1) {
                    def user = userResultSet.items.get(0)
                    user.basePath = userResultSet.basepath

                    def embed = EmbedUtils.defaultEmbed().with {
                        setThumbnail(user.avatar)
                        setTitle('Profile link', user.absoluteUrl)
                        setAuthor(user.name, user.absoluteUrl, user.avatar)
                        addField('User Info:', "**Name:** $user.name\n" +
                            "**Id:** $user.userId\n" +
                            "**Title:** $user.title\n" +
                            "**Number of edits:** $user.numberofedits", false)
                    }

                    sendEmbed(event, embed)

                    return
                }

                def eb = EmbedUtils.defaultEmbed().with {
                    setTitle('I found the following users:')
                }

                for (UserElement user : (userResultSet.items)) {
                    eb.with {
                        appendDescription('[')
                        appendDescription(user.name)
                        appendDescription('](')
                        appendDescription(userResultSet.basepath + user.url)
                        appendDescription(')\n')
                    }

                }

                sendEmbed(event, eb)

            },
            {
                sendMsg(event, "Something went wrong: $it.message")
            })
    }

    @Override
    String getName() { 'wikiuser' }

    @Override
    String[] getAliases() { ['wikiusersearch'] }

    @Override
    String getHelp() {
        'Search wikia for users.\n' +
            "Usage: `$Variables.PREFIX$name <username/user id>`\n" +
            "Examples: `$Variables.PREFIX$name duncte123`\n" +
            "`$Variables.PREFIX$name 34322457`\n"
    }
}
