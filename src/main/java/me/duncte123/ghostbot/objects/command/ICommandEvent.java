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

package me.duncte123.ghostbot.objects.command;

import me.duncte123.botcommons.commands.ICommandContext;
import me.duncte123.ghostbot.utils.Container;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.List;

public interface ICommandEvent extends ICommandContext {
    Container getContainer();
    String getInvoke();
    List<String> getArgs();

    default SlashCommandEvent.OptionData getOption(String name) {
        throw new IllegalArgumentException("Cannot get options for this type of command");
    }

    default boolean isSlash() {
        return this.getClass().equals(JDASlashCommandEvent.class);
    }

    void reply(String content);
    void reply(EmbedBuilder embed);
}
