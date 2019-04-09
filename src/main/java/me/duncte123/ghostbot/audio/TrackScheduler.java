/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;

import java.util.LinkedList;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapterWrapped {
    /**
     * This stores our queue
     */
    private final Queue<AudioTrack> queue;

    /**
     * Hey look at that, it's our player
     */
    private final IPlayer player;

    /**
     * This instantiates our player
     *
     * @param player
     *         Our audio player
     */
    TrackScheduler(IPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

    /**
     * Queue a track
     *
     * @param track
     *         The {@link AudioTrack AudioTrack} to queue
     */
    public void queue(AudioTrack track) {
        if (player.getPlayingTrack() != null) {
            queue.offer(track);
        } else {
            player.playTrack(track);
        }
    }

    /**
     * Starts the next track
     */
    private void nextTrack() {
        if (queue.peek() != null) {
            player.playTrack(queue.poll());
        }
    }

    /**
     * Gets run when a track ends
     *
     * @param player
     *         The {@link com.sedmelluq.discord.lavaplayer.player.AudioPlayer AudioTrack} for that guild
     * @param track
     *         The {@link AudioTrack AudioTrack} that ended
     * @param endReason
     *         Why did this track end?
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

}
