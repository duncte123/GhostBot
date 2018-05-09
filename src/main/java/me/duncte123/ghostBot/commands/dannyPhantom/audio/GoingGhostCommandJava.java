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

import me.duncte123.ghostBot.objects.Category_java;
import me.duncte123.ghostBot.objects.Command_java;
import me.duncte123.ghostBot.utils.SpoopyUtils_java;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils_java.sendMsg;

public class GoingGhostCommandJava extends Command_java {

    public GoingGhostCommandJava() {
        this.audioPath = SpoopyUtils_java.audio.BASE_AUDIO_DIR + "goingghost/";
        reloadAudioFiles();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (preAudioChecks(event)) {
            String selectedTrack = getRandomTrack();
            int p = SpoopyUtils_java.random.nextInt(100);
            if (p >= 50 && p <= 55) {
                selectedTrack = "extra/its going ghost.mp3";
            }
            sendMsg(event, "Selected track: _" + selectedTrack + "_");
            SpoopyUtils_java.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(),
                    audioPath + selectedTrack, false);
        }
    }

    @Override
    public String getName() {
        return "goingghost";
    }

    @Override
    public Category_java getCategory() {
        return Category_java.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Screams _\"going ghost\"_ in the voice channel that you are in (has a 5% chance of becoming ghostly)";
    }
}
