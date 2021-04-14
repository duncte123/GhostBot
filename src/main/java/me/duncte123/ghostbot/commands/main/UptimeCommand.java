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

package me.duncte123.ghostbot.commands.main;

import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.ICommandEvent;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UptimeCommand extends Command {

    private final long oldUptime;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor((r) -> {
        final Thread t = new Thread(r, "Uptime-Write-Thread");
        t.setDaemon(true);
        return t;
    });

    public UptimeCommand() {
        long time = -1;

        try {
            time = Long.parseLong(Files.readString(new File("./data/uptime.txt").toPath()).trim());
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.oldUptime = time;

        service.scheduleAtFixedRate(UptimeCommand::writeUptimeToFile, 1L, 1L, TimeUnit.DAYS);
    }

    @Override
    public void execute(ICommandEvent event) {
        event.reply("My current uptime: " + getUptime(ManagementFactory.getRuntimeMXBean().getUptime()) +
            "\nPrevious uptime was: " + getUptime(this.oldUptime));
    }

    @Override
    public String getName() {
        return "uptime";
    }

    @Override
    public String getHelp() {
        return "Shows the bots uptime";
    }

    @Override
    public void shutdown() {
        this.service.shutdown();
    }

    private String getUptime(long time) {
        /*
        This code has been inspired from JDA-Butler <https://github.com/Almighty-Alpaca/JDA-Butler/>
         */
        //Like it's ever gonna be up for more then a week
        final long years = time / 31104000000L;
        final long months = time / 2592000000L % 12;
        final long days = time / 86400000L % 30;
        final long hours = time / 3600000L % 24;
        final long minutes = time / 60000L % 60;
        final long seconds = time / 1000L % 60;

        final String uptimeString = formatTimeWord("Year", years, true) +
            formatTimeWord("Month", months, true) +
            formatTimeWord("Day", days, true) +
            formatTimeWord("Hour", hours, true) +
            formatTimeWord("Minute", minutes, true) +
            formatTimeWord("Second", seconds, false);

        return uptimeString.startsWith(", ") ? uptimeString.replaceFirst(", ", "") : uptimeString;
    }

    private String formatTimeWord(String word, long amount, boolean withComma) {
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

    public static void writeUptimeToFile() {
        final long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        final String uptimeString = Long.toString(uptime);

        try {
            Files.write(
                new File("./data/uptime.txt").toPath(),
                uptimeString.getBytes(),
                StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
