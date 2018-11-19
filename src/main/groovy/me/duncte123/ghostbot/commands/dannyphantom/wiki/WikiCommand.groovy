/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.commands.dannyphantom.wiki

import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.variables.Variables

class WikiCommand extends WikiBaseCommand {
    @Override
    void execute(CommandEvent event) {

        if (event.args.length == 0) {
            sendMessage(event.event, "Insufficient arguments, Correct usage: `$Variables.PREFIX$name <search term>`")
            return
        }

        handleWikiSearch(wiki, event.args.join(' '), event.event)
    }

    @Override
    String getName() { 'wiki' }

    @Override
    String[] getAliases() {
        [
            'wikia',
            'wikisearch',
            'dannyphantomwiki'
        ]
    }

    @Override
    String getHelp() {
        "Search the Danny Phantom wiki\n" +
            "Usage `$Variables.PREFIX$name <search term>`\nExample: `$Variables.PREFIX$name Danny`"
    }

    @Override
    boolean isSlackCompatible() { true }
}
