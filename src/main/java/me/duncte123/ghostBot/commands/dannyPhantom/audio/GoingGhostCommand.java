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

package me.duncte123.ghostBot.commands.dannyPhantom.audio;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.ThreadLocalRandom;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class GoingGhostCommand extends Command {

    public GoingGhostCommand() {
        this.audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "goingghost/";
        reloadAudioFiles();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (preAudioChecks(event)) {
            String selectedTrack = getRandomTrack();
            int p = ThreadLocalRandom.current().nextInt(100);
            if (p >= 50 && p <= 55) {
                selectedTrack = "extra/its going ghost.mp3";
            }
            sendMsg(event, "Selected track: _" + selectedTrack.replace("_", "\\_") + "_");
            SpoopyUtils.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(),
                    audioPath + selectedTrack, false);
        }
    }

    @Override
    public String getName() {
        return "goingghost";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Screams _\"going ghost\"_ in the voice channel that you are in (has a 5% chance of becoming ghostly)";
    }
}
