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
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.variables.Variables;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateSlashCommand extends Command {
    @Override
    public void execute(ICommandEvent event) {
        if (event.getAuthor().getIdLong() != Variables.OWNER_ID) {
            return;
        }

        if (event.isSlash()) {
            event.reply("Cannot be used with slash commands");
            return;
        }

        final List<String> args = event.getArgs();

        if (args.isEmpty()) {
            event.reply("choose global, guild, clear-guild or clear-global");
            return;
        }

        final var commands = event.getContainer()
            .getCommandManager()
            .getCommands()
            .stream()
            .filter((it) -> it.getCategory() != CommandCategory.HIDDEN)
            .map(Command::getCommandData)
            .collect(Collectors.toList());

        switch (args.get(0)) {
            case "guild":
                event.getGuild().updateCommands()
                    .addCommands(commands)
                    .queue((__) -> event.reply("Commands updated"));
                break;
            case "global":
                event.getJDA().updateCommands()
                    .addCommands(commands)
                    .queue((__) -> event.reply("Commands updated"));
                break;
            case "clear-guild":
                event.getGuild().updateCommands()
                    .queue((__) -> event.reply("Cleared guild commands"));
                break;
            case "clear-global":
                event.getJDA().updateCommands()
                    .queue((__) -> event.reply("Cleared global commands"));
                break;
            default:
                event.reply("choose global, guild, clear-guild or clear-global");
                break;
        }
    }

    @Override
    public String getName() {
        return "updateslash";
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }
}
