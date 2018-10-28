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

package me.duncte123.ghostbot.utils

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.messaging.MessageUtils
import me.duncte123.ghostBot.audio.GuildMusicManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel

import java.util.logging.Level
import java.util.logging.Logger

class AudioUtils {

    static AudioUtils instance = new AudioUtils()

    public final String BASE_AUDIO_DIR = "../GhostBot/audioFiles/"
    private static final int DEFAULT_VOLUME = 35 //(0-150, where 100 is the default max volume)
    private final String embedTitle = "Spoopy-Luma-Player"

    private final TLongObjectMap<GuildMusicManager> musicManagers
    private AudioPlayerManager playerManager

    private AudioUtils() {
        Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF)

        initPlayerManager()

        musicManagers = new TLongObjectHashMap<>()
    }

    private void initPlayerManager() {
        if (playerManager == null) {
            playerManager = new DefaultAudioPlayerManager()
            playerManager.registerSourceManager(new YoutubeAudioSourceManager(false))
            AudioSourceManagers.registerLocalSource(playerManager)
        }
    }

    AudioPlayerManager getPlayerManager() {
        initPlayerManager()
        return playerManager
    }

    /**
     * Loads a track and plays it if the bot isn't playing
     *
     * @param mng
     *         The {@link GuildMusicManager MusicManager} for the guild
     * @param channel
     *         The {@link net.dv8tion.jda.core.entities.MessageChannel channel} that the bot needs to send the messages
     *         to
     * @param trackUrlRaw
     *         The url from the track to play
     * @param addPlayList
     *         If the url is a playlist
     */
    void loadAndPlay(GuildMusicManager mng, final TextChannel channel, final String trackUrl, final boolean addPlayList) {
        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {


            @Override
            void trackLoaded(AudioTrack track) {
                if (mng.player.playingTrack != null) {
                    mng.player.stopTrack()
                }

                mng.scheduler.queue(track)
            }

            @Override
            void playlistLoaded(AudioPlaylist playlist) {
                def firstTrack = playlist.selectedTrack
                def tracks = playlist.tracks

                if (tracks.size() == 0) {
                    MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Error: This playlist is empty."))
                    return

                } else if (firstTrack == null) {
                    firstTrack = playlist.tracks.get(0)
                }
                String msg

                if (addPlayList) {
                    msg = "Adding **${playlist.tracks.size()}** tracks to queue from playlist: $playlist.name"
                    if (mng.player.playingTrack == null) {
                        msg += "\nand the Player has started playing;"
                    }
                    tracks.forEach(mng.scheduler::queue)
                } else {
                    msg = "Adding to queue $firstTrack.info.title (first track of playlist $playlist.name)"
                    if (mng.player.playingTrack == null) {
                        msg += "\nand the Player has started playing;"
                    }
                    mng.scheduler.queue(firstTrack)
                }

                MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, msg))
            }


            @Override
            void noMatches() {
                MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Nothing found by _" + trackUrl + "_"))
            }

            @Override
            void loadFailed(FriendlyException exception) {
                MessageUtils.sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Could not play: $exception.message" +
                        "\nIf this happens often try another link or join our [support guild](https://discord.gg/NKM9Xtk) for more!"))
            }
        })
    }

    /**
     * This will get the music manager for the guild or register it if we don't have it yet
     *
     * @param guild
     *         The guild that we need the manager for
     *
     * @return The music manager for that guild
     */
    GuildMusicManager getMusicManager(Guild guild) {
        def guildId = guild.idLong
        def mng = musicManagers.get(guildId)

        if (mng == null) {
            synchronized (musicManagers) {
                mng = musicManagers.get(guildId)
                if (mng == null) {
                    mng = new GuildMusicManager(guild)
                    mng.player.volume = DEFAULT_VOLUME
                    musicManagers.put(guildId, mng)
                }
            }
        }

        guild.audioManager.setSendingHandler(mng.sendHandler)

        return mng
    }

}
