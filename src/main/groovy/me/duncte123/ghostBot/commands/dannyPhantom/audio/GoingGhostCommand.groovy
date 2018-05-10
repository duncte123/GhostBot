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

package me.duncte123.ghostBot.commands.dannyPhantom.audio

import me.duncte123.ghostBot.objects.Command
import me.duncte123.ghostBot.objects.CommandCategory
import me.duncte123.ghostBot.utils.SpoopyUtils
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg

class GoingGhostCommand extends Command {
    GoingGhostCommand() {
        this.audioPath = SpoopyUtils.AUDIO.BASE_AUDIO_DIR + "boxghost/"
        reloadAudioFiles()
    }

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (preAudioChecks(event)) {
            String selectedTrack = getRandomTrack()
            int p = SpoopyUtils.RANDOM.nextInt(100)
            if (p >= 50 && p <= 55) {
                selectedTrack = "extra/its going ghost.mp3"
            }
            sendMsg(event, "Selected track: _" + selectedTrack + "_")
            SpoopyUtils.AUDIO.loadAndPlay(getMusicManager(event.guild), event.channel,
                    audioPath + selectedTrack, false)
        }
    }

    @Override
    String getName() {
        return "goingghost"
    }

    @Override
    String getHelp() {
        return "Screams _\"going ghost\"_ in the voice channel that you are in (has a 5% chance of becoming ghostly)"
    }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.AUDIO
    }
}
