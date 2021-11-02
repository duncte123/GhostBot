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

package me.duncte123.ghostbot.slashmanagement;

import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;
import java.util.stream.Collectors;

public class SlashHelper {
    public static List<CommandData> collectCommands(CommandManager manager) {
        return manager.getCommands()
            .stream()
            .filter((it) -> it.getCategory() != CommandCategory.HIDDEN)
            .map(Command::getCommandData)
            .collect(Collectors.toList());
    }
}
