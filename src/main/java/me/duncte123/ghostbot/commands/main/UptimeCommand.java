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

import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandEvent;

import java.lang.management.ManagementFactory;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class UptimeCommand extends Command {
    @Override
    public void execute(CommandEvent event) {
        sendMsg(event, "My current uptime: " + getUptime(ManagementFactory.getRuntimeMXBean().getUptime()));
    }

    @Override
    public String getName() {
        return "uptime";
    }

    @Override
    public String getHelp() {
        return "Shows the bots uptime";
    }

    private String getUptime(long time) {
        /*
        This code has been inspired from JDA-Butler <https://github.com/Almighty-Alpaca/JDA-Butler/>
         */
        //Like it's ever gonna be up for more then a week
        final int years = (int) (time / 31104000000L);
        final int months = (int) (time / 2592000000L % 12);
        final int days = (int) (time / 86400000L % 30);
        final int hours = (int) (time / 3600000L % 24);
        final int minutes = (int) (time / 60000L % 60);
        final int seconds = (int) (time / 1000L % 60);

        final String uptimeString = formatTimeWord("Year", years, true) +
            formatTimeWord("Month", months, true) +
            formatTimeWord("Day", days, false) +
            ", " +
            formatTimeWord("Hour", hours, true) +
            formatTimeWord("Minute", minutes, true) +
            formatTimeWord("Second", seconds, false);

        return uptimeString.startsWith(", ") ? uptimeString.replaceFirst(", ", "") : uptimeString;
    }

    private String formatTimeWord(String word, int amount, boolean withComma) {
        if (amount == 0) {
            return "";
        }

        final StringBuilder builder = new StringBuilder()
            .append(amount).append(' ').append(word);

        if (amount > 1) {
            builder.append('s');
        }

        if (withComma) {
            builder.append(", ");
        }

        return builder.toString();
    }
}
