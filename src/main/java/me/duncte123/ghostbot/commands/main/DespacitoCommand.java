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
import me.duncte123.ghostbot.utils.AudioUtils;
import me.duncte123.ghostbot.utils.StoredTracks;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class DespacitoCommand extends Command {
    @Override
    public void execute(ICommandEvent event) {
        if (!preAudioChecks(event)) {
            return;
        }

        event.reply("Now playing: _Despacito_");

        final AudioUtils audio = event.getContainer().getAudio();

        audio.loadAndPlay(
            getMusicManager(audio, event.getGuild()),
            event.getChannel(),
            StoredTracks.DESPACITO
        );
    }

    @Override
    public String getName() {
        return "despacito";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Plays Despacito in your voice channel";
    }
}
