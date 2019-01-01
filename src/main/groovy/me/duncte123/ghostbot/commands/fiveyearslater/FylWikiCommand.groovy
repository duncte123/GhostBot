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

package me.duncte123.ghostbot.commands.fiveyearslater

import me.duncte123.ghostbot.commands.dannyphantom.wiki.WikiBaseCommand
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.utils.WikiHolder
import me.duncte123.ghostbot.variables.Variables

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class FylWikiCommand extends WikiBaseCommand {

    private final WikiHolder FYL_WIKI_HOLDER = new WikiHolder('https://5yl.wikia.com')

    @Override
    void execute(CommandEvent event) {

        if (event.args.length == 0) {
            sendMsg(event.event, "Insufficient arguments, Correct usage: `$Variables.PREFIX$name <search term>`")
            return
        }

        handleWikiSearch(FYL_WIKI_HOLDER, event.args.join(' '), event.event)

    }

    @Override
    String getName() { '5ylwiki' }

    @Override
    String getHelp() {
        'Search the 5YL wiki\n' +
            "Usage `$Variables.PREFIX$name <search term>`\n" +
            "Example: `$Variables.PREFIX$name Danny`"
    }
}
