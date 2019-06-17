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

package me.duncte123.ghostbot.commands.main;

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.variables.Variables;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;

public class AboutCommand extends Command {
    @Override
    public void execute(CommandEvent event) {
        final String devName = "<@191231307290771456> (duncte123#1245)";
        final long guildCountDiscord = event.getShardManager().getGuildCache().size();

        sendEmbed(event, EmbedUtils.embedMessage(String.format(
            "Hey there, my name is GhostBot, I am the must have bot for your spooky server.\n" +
                "I am mainly themed around Danny Phantom but other spooky stuff that you have for me can be suggested to %2$s.\n" +
                "If you want to stay in contact with my developer you can join [this server](%1$s).\n\n" +
                "**Extra information:**\n" +
                "My twitter: [Click here](https://twitter.com/GhostBotDiscord)\n" +
                "My invite link: [Click here](%3$s)\n" +
                "My prefixes: `%4$s` and `%5$s`\n" +
                "My home: [%1$s](%1$s)\n" +
                "My version: `%6$s`\n" +
                "The amount of Discord servers that I am in: %7$s",
            Variables.GHOSTBOT_GUILD,
            devName,
            Variables.GHOSTBOT_INVITE,
            Variables.PREFIX,
            Variables.OTHER_PREFIX,
            Variables.VERSION,
            guildCountDiscord
        )));
    }

    @Override
    public String getName() { return "about"; }

    @Override
    public String getHelp() { return "Get some info about the bot"; }
}
