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

import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class WailCommand extends Command {

   /* private final String audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "wail/";
    private final String[] audioFiles = {
            "ghost wail 1.mp3",
            "ghost wail 2.mp3",
            "ghost wail 3.mp3",
            "ghost wail 4.mp3",
            "ghost wail 5.mp3",
            "ghost wail 6.mp3"
    };*/

    public WailCommand() {
        this.audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "wail/";
        reloadAudioFiles();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (preAudioChecks(event)) {
            String selectedTrack = audioFiles[SpoopyUtils.random.nextInt(audioFiles.length)];
            sendMsg(event, "Selected track: _" + selectedTrack + "_");
            SpoopyUtils.audio.loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(),
                    audioPath + selectedTrack, false);
        }
    }

    @Override
    public String getName() {
        return "wail";
    }

    @Override
    public Category getCategory() {
        return Category.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Gives you a nice ghostly wail";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ghostlywail"};
    }
}
