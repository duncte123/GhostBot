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

package me.duncte123.ghostBot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapterWrapped {

    /**
     * This stores our queue
     */
    public final Queue<AudioTrack> queue;

    /**
     * Hey look at that, it's our player
     */
    private final IPlayer player;

    /**
     * This is the last playing track
     */
    private AudioTrack lastTrack;

    //private final GuildMusicManager guildMusicManager;

    /**
     * Are we repeating the track
     */
    private boolean repeating = false;


    /**
     * Are we repeating playlists
     */
    private boolean repeatPlayList = false;

    //private LavaplayerPlayerWrapper lavaplayerPlayer;

    /**
     * This instantiates our player
     *
     * @param player Our audio player
     */
    TrackScheduler(IPlayer player) {
        this.player = player;
        this.queue = new LinkedList<>();
        //this.guildMusicManager = guildMusicManager;
    }

    /**
     * Queue a track
     *
     * @param track The {@link AudioTrack AudioTrack} to queue
     */
    public void queue(AudioTrack track) {
        if(player.getPlayingTrack() != null) {
            queue.offer(track);
        } else {
            player.playTrack(track);
        }
    }

    /**
     * Starts the next track
     */
    public void nextTrack() {
        if(queue.peek() != null)
            player.playTrack(queue.poll());
    }

    /**
     * Gets run when a track ends
     *
     * @param player    The {@link AudioPlayer AudioTrack} for that guild
     * @param track     The {@link AudioTrack AudioTrack} that ended
     * @param endReason Why did this track end?
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.lastTrack = track;

        if (endReason.mayStartNext) {
            if (repeating) {
                if (!repeatPlayList) {
                    player.playTrack(lastTrack.makeClone());
                } else {
                    queue(lastTrack.makeClone());
                }
            } else {
                nextTrack();
            }
        }
    }

    /**
     * This will tell you if the player is repeating
     *
     * @return true if the player is set to repeat
     */
    public boolean isRepeating() {
        return repeating;
    }

    /**
     * This will tell you if the player is repeating playlists
     *
     * @return true if the player is set to repeat playlists
     */
    public boolean isRepeatingPlaylists() {
        return repeatPlayList;
    }

    /**
     * tell the player if needs to repeat
     *
     * @param repeating if the player needs to repeat
     */
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    /**
     * tell the player if needs to repeat playlists
     *
     * @param repeatingPlaylists if the player needs to repeat playlists
     */
    public void setRepeatingPlaylists(boolean repeatingPlaylists) {
        this.repeatPlayList = repeatingPlaylists;
    }

    /**
     * Shuffles the player
     */
    public void shuffle() {
        Collections.shuffle((List<?>) queue);
    }

}
