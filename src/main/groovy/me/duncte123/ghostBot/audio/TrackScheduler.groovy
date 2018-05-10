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

package me.duncte123.ghostBot.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import lavalink.client.player.IPlayer
import lavalink.client.player.event.AudioEventAdapterWrapped

class TrackScheduler extends AudioEventAdapterWrapped {

    final Queue<AudioTrack> queueList
    final IPlayer player
    private AudioTrack lastTrack
    private boolean repeating = false
    private boolean repeatPlayList = false

    TrackScheduler(IPlayer player) {
        this.player = player
        this.queueList = new LinkedList<>()
    }

    void queue(AudioTrack track) {
        if (player.getPlayingTrack() != null) {
            queueList.offer(track)
        } else {
            player.playTrack(track)
        }
    }

    void nextTrack() {
        if (queueList.peek() != null)
            player.playTrack(queueList.poll())
    }

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

    boolean isRepeating() {
        return repeating
    }

    void setRepeating(boolean repeating) {
        this.repeating = repeating
    }

    boolean isRepeatingPlaylists() {
        return repeatPlayList
    }

    void setRepeatingPlaylists(boolean repeatingPlaylists) {
        this.repeatPlayList = repeatingPlaylists
    }

    void shuffle() {
        Collections.shuffle((List<?>) queueList)
    }
}
