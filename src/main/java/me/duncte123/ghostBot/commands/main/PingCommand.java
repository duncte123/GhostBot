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

package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostbot.objects.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.temporal.ChronoUnit;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class PingCommand implements Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        sendMsg(event, "PONG!", (message) -> {

            String format = String.format(
                    "PONG!\nRest Ping: %s\nWebsocket Ping: %s",
                    event.getMessage().getCreationTime().until(message.getCreationTime(), ChronoUnit.MILLIS),
                    event.getJDA().getPing()
            );

            message.editMessage(format).queue();

        });

    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getHelp() {
        return "PONG";
    }
}
