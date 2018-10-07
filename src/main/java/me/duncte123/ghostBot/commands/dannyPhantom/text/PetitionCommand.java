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

package me.duncte123.ghostBot.commands.dannyPhantom.text;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.botcommons.messaging.MessageUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

public class PetitionCommand extends Command {

    private final String[] messages = {
            // %1$s = "http://bit.ly/dp-petition"
            // %2$s = "#GoGhostAgain"
            "What you gonna sign? %1$s %2$s",
            "I would appreciate it if you could sign this petition %1$s %2$s !",
            "SIGN THE PETITION %1$s %2$s",
            "%2$s! %1$s",
            "https://ghostbot.duncte123.me/img/GoGhostAgainBanner.png%n" +
                    "Click the link to sign the petition <%1$s>",
            //Lol a haiku by Lady Phantom
            "Let's go ghost again!%n" +
                    "Come on; sign the petition!%n" +
                    "Team Phantom needs you!%n%1$s"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        MessageUtils.sendMsg(event,
                String.format(messages[ThreadLocalRandom.current().nextInt(messages.length)], "http://bit.ly/dp-petition", "#GoGhostAgain")
        );
    }

    @Override
    public String getName() {
        return "petition";
    }

    @Override
    public String getHelp() {
        return "Sends you a link to the Danny Phantom petition #GoGhostAgain";
    }
}
