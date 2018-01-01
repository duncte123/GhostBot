/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Maurice R S "Sanduhr32"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioPlayerSenderHandler implements AudioSendHandler {

    /**
     * This is our me.duncte123.ghostBot.audio player
     */
    private final AudioPlayer audioPlayer;

    /**
     * I don't know what this does but it seems important
     */
    private AudioFrame lastFrame;

    public AudioPlayerSenderHandler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    /**
     * Checks if the player can provide the song
     *
     * @return true if we can provide something
     */
    @Override
    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }
        return lastFrame != null;
    }

    /**
     * This <em>should</em> gives us our me.duncte123.ghostBot.audio
     *
     * @return The me.duncte123.ghostBot.audio in some nice bytes
     */
    @Override
    public byte[] provide20MsAudio() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.data : null;
        lastFrame = null;
        return data;
    }

    /**
     * "Checks" if this me.duncte123.ghostBot.audio is opus
     *
     * @return always true
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}
