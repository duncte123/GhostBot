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

public class FruitloopCommand extends Command {

    //private final String audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "fruitloop/";
    /*private String[] audioFiles = {
            "fruitloop 1.mp3",
            "fruitloop 2.mp3"
    };*/

    public FruitloopCommand() {
        this.audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "fruitloop/";
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
        return "fruitloop";
    }

    @Override
    public Category getCategory() {
        return Category.AUDIO;
    }

    @Override
    public String getHelp() {
        return "You're one crazed up fruitloop";
    }
}
