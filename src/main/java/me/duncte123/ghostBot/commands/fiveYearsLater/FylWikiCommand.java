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

package me.duncte123.ghostBot.commands.fiveYearsLater;

import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiBaseCommand;
import me.duncte123.ghostbot.utils.WikiHolder;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class FylWikiCommand extends WikiBaseCommand {

    private final WikiHolder FYL_WIKI_HOLDER = new WikiHolder("https://5yl.wikia.com");

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (args.length == 0) {
            sendMsg(event, "Insufficient arguments, Correct usage: `" + Variables.PREFIX + getName() + " <search term>`");
            return;
        }

        handleWikiSearch(FYL_WIKI_HOLDER, StringUtils.join(args, " "), event);
    }

    @Override
    public String getName() {
        return "5ylwiki";
    }

    @Override
    public String getHelp() {
        return "Search the 5YL wiki\n" +
                "Usage `" + Variables.PREFIX + getName() + " <search term>`\n" +
                "Example: `" + Variables.PREFIX + getName() + " Danny`";
    }
}
