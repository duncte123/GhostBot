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

package me.duncte123.ghostBot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.duncte123.ghostBot.audio.GuildMusicManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class AudioUtils {

    private static final int DEFAULT_VOLUME = 35; //(0-150, where 100 is the default max volume)
    public final String BASE_AUDIO_DIR = "../GhostBot/audioFiles/";
    private final String embedTitle = "Spoopy-Luma-Player";
    private AudioPlayerManager playerManager;

    private final Map<String, GuildMusicManager> musicManagers;

    public static AudioUtils ins = new AudioUtils();

    private AudioUtils() {
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        initPlayerManager();

        musicManagers = new HashMap<>();
    }

    private void initPlayerManager() {
        if(playerManager == null) {
            playerManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerLocalSource(playerManager);
        }
    }

    public AudioPlayerManager getPlayerManager() {
        initPlayerManager();
        return playerManager;
    }

    /**
     * This will return the formatted timestamp for the current playing track
     *
     * @param milliseconds the milliseconds that the track is at
     * @return a formatted time
     */
    public static String getTimestamp(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * Loads a track and plays it if the bot isn't playing
     *
     * @param mng         The {@link GuildMusicManager MusicManager} for the guild
     * @param channel     The {@link net.dv8tion.jda.core.entities.MessageChannel channel} that the bot needs to send the messages to
     * @param trackUrlRaw The url from the track to play
     * @param addPlayList If the url is a playlist
     */
    public void loadAndPlay(GuildMusicManager mng, final TextChannel channel, final String trackUrlRaw, final boolean addPlayList) {
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link resources
        if (trackUrlRaw.startsWith("<") && trackUrlRaw.endsWith(">")) {
            trackUrl = trackUrlRaw.substring(1, trackUrlRaw.length() - 1);
        } else {
            trackUrl = trackUrlRaw;
        }

        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {


            @Override
            public void trackLoaded(AudioTrack track) {
                if (mng.player.getPlayingTrack() != null) {
                    mng.player.stopTrack();
                }

                mng.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();

                if (tracks.size() == 0) {
                    MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Error: This playlist is empty."));
                    return;

                } else if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }
                String msg;

                if (addPlayList) {
                    msg = "Adding **" + playlist.getTracks().size() + "** tracks to queue from playlist: " + playlist.getName();
                    if (mng.player.getPlayingTrack() == null) {
                        msg += "\nand the Player has started playing;";
                    }
                    tracks.forEach(mng.scheduler::queue);
                } else {
                    msg = "Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")";
                    if (mng.player.getPlayingTrack() == null) {
                        msg += "\nand the Player has started playing;";
                    }
                    mng.scheduler.queue(firstTrack);
                }
                MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, msg));
            }


            @Override
            public void noMatches() {
                MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Nothing found by _" + trackUrl + "_"));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Could not play: " + exception.getMessage()
                        + "\nIf this happens often try another link or join our [support guild](https://discord.gg/NKM9Xtk) for more!"));
            }
        });
    }

    /**
     * This will get the music manager for the guild or register it if we don't have it yet
     *
     * @param guild The guild that we need the manager for
     * @return The music manager for that guild
     */
    public GuildMusicManager getMusicManager(Guild guild) {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null) {
            synchronized (musicManagers) {
                mng = musicManagers.get(guildId);
                if (mng == null) {
                    mng = new GuildMusicManager(guild);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        guild.getAudioManager().setSendingHandler(mng.getSendHandler());
        return mng;
    }

    public Map<String, GuildMusicManager> getMusicManagers() {
        return musicManagers;
    }
}