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

package me.duncte123.ghostbot.utils;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.audio.GuildMusicManager;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;

public class AudioUtils {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AudioUtils.class);

    private static final int DEFAULT_VOLUME = 35; //(0-150, where 100 is the default max volume)
    private final String embedTitle = "Spoopy-Luma-Player";

    private final TLongObjectMap<GuildMusicManager> musicManagers;
    private final AudioPlayerManager playerManager;

    AudioUtils() {
        Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        playerManager = new DefaultAudioPlayerManager();
        
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(false));
        AudioSourceManagers.registerLocalSource(playerManager);

        musicManagers = new TLongObjectHashMap<>();
    }

    public void loadAndPlay(GuildMusicManager mng, final TextChannel channel, final String trackUrl, final boolean addPlayList) {
        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (mng.getPlayer().getPlayingTrack() != null) {
                    mng.getPlayer().stopTrack();
                }

                mng.getScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.error("Playlist loaded somehow");
            }

            @Override
            public void noMatches() {
                sendEmbed(channel, EmbedUtils.embedField(embedTitle, "Nothing found by _" + trackUrl + '_'));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                sendEmbed(channel, EmbedUtils.embedField(embedTitle, String.format(
                    "Could not play: %s\n" +
                        "If this happens often try another link or join our [support guild](%s) for more!",
                    exception.getMessage(), Variables.GHOSTBOT_GUILD
                )));
            }
        });
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        final long guildId = guild.getIdLong();
        GuildMusicManager mng = musicManagers.get(guildId);

        if (mng == null) {
            synchronized (musicManagers) {
                mng = musicManagers.get(guildId);
                if (mng == null) {
                    mng = new GuildMusicManager(guild);
                    mng.getPlayer().setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }

        guild.getAudioManager().setSendingHandler(mng.getSendHandler());

        return mng;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public TLongObjectMap<GuildMusicManager> getMusicManagers() {
        return musicManagers;
    }

    public String getBaseAudioDir() {
        return "../GhostBot/audioFiles/";
//        return "audioFiles/";
    }
}
