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

import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class WikiCommand extends WikiBaseCommand {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        //
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }

        handleWikiSearch(wiki, StringUtils.join(args, " "), event);
    }

    @Override
    public String getName() {
        return "wiki";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.WIKI;
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
