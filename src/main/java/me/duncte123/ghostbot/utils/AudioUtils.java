/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.player.LavalinkManager;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.messaging.MessageConfig;
import me.duncte123.ghostbot.audio.GuildMusicManager;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class AudioUtils {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AudioUtils.class);

    private static final int DEFAULT_VOLUME = 35; //(0-150, where 100 is the default max volume)
    private static final String EMBED_TITLE = "Spoopy-Luma-Player";

    private final TLongObjectMap<GuildMusicManager> musicManagers;
    private final AudioPlayerManager playerManager;

    AudioUtils() {
        Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        playerManager = new DefaultAudioPlayerManager();

        playerManager.registerSourceManager(new YoutubeAudioSourceManager(false));
        playerManager.registerSourceManager(new HttpAudioSourceManager());

        musicManagers = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), new Object());
    }

    public void loadAndPlay(GuildMusicManager mng, final TextChannel channel, final Object trackUrl) {
        final AudioLoadResultHandler handler = new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                mng.getPlayer().playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                logger.error("Playlist loaded somehow");
            }

            @Override
            public void noMatches() {
                sendMsg(
                    new MessageConfig.Builder()
                        .setChannel(channel)
                        .setEmbed(EmbedUtils.embedField(EMBED_TITLE, "Nothing found by _" + trackUrl + '_'), true)
                        .build()
                );
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                sendMsg(
                    new MessageConfig.Builder()
                        .setChannel(channel)
                        .setEmbed(EmbedUtils.embedField(EMBED_TITLE, String.format(
                            "Could not play: %s\n" +
                                "Please contact a developer [here](%s) to inform them of this issue",
                            exception.getMessage(), Variables.GHOSTBOT_GUILD
                        )), true)
                        .build()
                );
            }
        };

        if (trackUrl instanceof String) {
            playerManager.loadItemOrdered(mng, (String) trackUrl, handler);
            return;
        }

        if (!(trackUrl instanceof Function)) {
            throw new IllegalArgumentException("Track should be a function instead");
        }

        //noinspection unchecked
        final Function<YoutubeAudioSourceManager, YoutubeAudioTrack> fn = (Function<YoutubeAudioSourceManager, YoutubeAudioTrack>) trackUrl;

        handler.trackLoaded(
            fn.apply(playerManager.source(YoutubeAudioSourceManager.class))
        );
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        final long guildId = guild.getIdLong();
        GuildMusicManager mng = musicManagers.get(guildId);

        if (mng == null) {
            mng = new GuildMusicManager(guild);
            mng.getPlayer().setVolume(DEFAULT_VOLUME);
            musicManagers.put(guildId, mng);
        }

        if (!LavalinkManager.ins.isEnabled()) {
            guild.getAudioManager().setSendingHandler(mng.getSendHandler());
        }

        return mng;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public TLongObjectMap<GuildMusicManager> getMusicManagers() {
        return musicManagers;
    }
}
