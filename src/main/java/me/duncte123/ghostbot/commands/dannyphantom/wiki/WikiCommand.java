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

import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.variables.Variables;

import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class WikiCommand extends WikiBaseCommand {
    @Override
    public void execute(ICommandEvent event) {
        final List<String> args = event.getArgs();

        if (args.isEmpty()) {
            event.reply("Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }

        handleWikiSearch(this.wiki, String.join(" ", args), event.getContainer().getJackson(), event);
    }

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "wikia",
            "wikisearch",
            "dannyphantomwiki"
        );
    }

    @Override
    public String getHelp() {
        return "Search the Danny Phantom wiki\n" +
            "Usage `" + Variables.PREFIX + getName() + " <search term>`\nExample: `" + Variables.PREFIX + getName() + " Danny`";
    }
}
