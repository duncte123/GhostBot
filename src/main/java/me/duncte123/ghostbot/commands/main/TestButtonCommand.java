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
import me.duncte123.ghostbot.objects.command.JDASlashCommandEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.ActionRow;
import net.dv8tion.jda.api.interactions.Component;
import net.dv8tion.jda.api.interactions.button.Button;

import java.util.List;

import static me.duncte123.ghostbot.commands.ReactionCommand.LEFT_RIGHT_CANCEL_EMOTE_ONLY;

public class TestButtonCommand extends Command {
    @Override
    public void execute(ICommandEvent event) {
        if (!event.isSlash()) {
            event.reply("must be a slash command");
            return;
        }

        final SlashCommandEvent slashEvent = ((JDASlashCommandEvent) event).getSlashEvent();

        final List<Button> buttons = LEFT_RIGHT_CANCEL_EMOTE_ONLY.apply(event.getAuthor().getIdLong());

        slashEvent.deferReply(false)
            .setContent("Testing 123")
            .addActionRows(ActionRow.of(buttons))
            .queue();
    }

    @Override
    public String getName() {
        return "test-button";
    }

    @Override
    public String getHelp() {
        return "Testing a button";
    }
}
