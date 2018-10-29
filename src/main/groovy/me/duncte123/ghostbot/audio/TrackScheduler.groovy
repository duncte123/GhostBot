/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import lavalink.client.player.IPlayer
import lavalink.client.player.event.AudioEventAdapterWrapped

class TrackScheduler extends AudioEventAdapterWrapped {

    /**
     * This stores our queue
     */
    final Queue<AudioTrack> queue

    /**
     * Hey look at that, it's our player
     */
    private final IPlayer player

    /**
     * This is the last playing track
     */
    private AudioTrack lastTrack

    /**
     * Are we repeating the track
     */
    private boolean repeating = false

    /**
     * Are we repeating playlists
     */
    private boolean repeatPlayList = false

    /**
     * This instantiates our player
     *
     * @param player
     *         Our audio player
     */
    TrackScheduler(IPlayer player) {
        this.player = player
        this.queue = new LinkedList<>()
    }

    /**
     * Queue a track
     *
     * @param track
     *         The {@link AudioTrack AudioTrack} to queue
     */
    void queue(AudioTrack track) {
        if (player.getPlayingTrack() != null) {
            queue.offer(track)
        } else {
            player.playTrack(track)
        }
    }

    /**
     * Starts the next track
     */
    void nextTrack() {
        if (queue.peek() != null) {
            player.playTrack(queue.poll())
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
    void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track

        if (endReason.mayStartNext) {
            if (repeating) {
                if (!repeatPlayList) {
                    player.playTrack(lastTrack.makeClone())
                } else {
                    queue(lastTrack.makeClone())
                }
            } else {
                nextTrack()
            }
        }
    }
}
