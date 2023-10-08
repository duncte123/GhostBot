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

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.LinkState;
import dev.arbjerg.lavalink.protocol.v4.Exception;
import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.messaging.MessageConfig;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.util.annotation.NonNull;

import java.net.URI;
import java.net.URISyntaxException;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

// TODO: remove lavaplayer
public class AudioUtils {

    private static final Logger logger = LoggerFactory.getLogger(AudioUtils.class);

    private static final int DEFAULT_VOLUME = 35; //(0-150, where 100 is the default max volume)
    private static final String EMBED_TITLE = "Spoopy-Luma-Player";
    private final GhostBotConfig config;
    private LavalinkClient lavalink = null;

    AudioUtils(final GhostBotConfig config) {
        this.config = config;if (isEnabled()) {
            lavalink = new LavalinkClient(Helpers.getUserIdFromToken(this.config.discord.token));

            for (final GhostBotConfig.Lavalink.Node it : this.config.lavalink.nodes) {
                try {
                    final URI uri = new URI(it.wsUrl);

                    lavalink.addNode(uri.getHost(), uri, it.pass);
                } catch (URISyntaxException e) {
                    logger.error("Adding lavalink node failed", e);
                }
            }
        }
    }

    public boolean isEnabled() {
        return this.config.lavalink.enable;
    }

    public boolean isConnected(long guildId) {
        if (!isEnabled()) {
            throw new RuntimeException("Lavalink is not enabled");
        }

        return lavalink.getLink(guildId).getState() == LinkState.CONNECTED;
    }

    public boolean isConnected(Guild g) {
        return isConnected(g.getIdLong());
    }

    public AudioChannel getConnectedChannel(@NonNull Guild guild) {
        // NOTE: never use the local audio manager, since the audio connection may be remote
        // there is also no reason to look the channel up remotely from lavalink, if we have access to a real guild
        // object here, since we can use the voice state of ourselves (and lavalink 1.x is buggy in keeping up with the
        // current voice channel if the bot is moved around in the client)
        // noinspection ConstantConditions
        return guild.getSelfMember().getVoiceState().getChannel();
    }

    public LavalinkClient getLavalink() {
        return lavalink;
    }

    public void loadAndPlay(final Guild guild, final MessageChannelUnion channel, final String trackUrl) {
        if (!isEnabled()) {
            throw new RuntimeException("Lavalink is not enabled");
        }

        final long guildId = guild.getIdLong();
        final Link link = this.lavalink.getLink(guildId);

        link.loadItem(trackUrl).subscribe((result) -> {
            if (result instanceof LoadResult.TrackLoaded trackLoaded) {
                link.updatePlayer(
                    (builder) -> builder.setVolume(DEFAULT_VOLUME).setEncodedTrack(trackLoaded.getData().getEncoded())
                ).subscribe((__) -> {
                    // TODO: send message?
                });
            } else if (result instanceof LoadResult.PlaylistLoaded) {
                logger.error("Playlist loaded somehow");
            } else if (result instanceof LoadResult.NoMatches) {
                sendMsg(
                    new MessageConfig.Builder()
                        .setChannel(channel)
                        .setEmbeds(true, EmbedUtils.embedField(EMBED_TITLE, "Nothing found by _" + trackUrl + '_'))
                        .build()
                );
            } else if (result instanceof LoadResult.LoadFailed loadFailed) {
                final Exception exception = loadFailed.getData();

                sendMsg(
                    new MessageConfig.Builder()
                        .setChannel(channel)
                        .setEmbeds(true, EmbedUtils.embedField(EMBED_TITLE, String.format(
                            "Could not play: %s\n" +
                                "Please contact a developer [here](%s) to inform them of this issue",
                            exception.getMessage(), Variables.GHOSTBOT_GUILD
                        )))
                        .build()
                );
            }
        });
    }

    /*public void loadAndPlay(GuildMusicManager  Object mng, final MessageChannelUnion channel, final Object trackUrl) {
        final AudioLoadResultHandler handler = new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
//                mng.getPlayer().playTrack(track);
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
                        .setEmbeds(true, EmbedUtils.embedField(EMBED_TITLE, "Nothing found by _" + trackUrl + '_'))
                        .build()
                );
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                sendMsg(
                    new MessageConfig.Builder()
                        .setChannel(channel)
                        .setEmbeds(true, EmbedUtils.embedField(EMBED_TITLE, String.format(
                            "Could not play: %s\n" +
                                "Please contact a developer [here](%s) to inform them of this issue",
                            exception.getMessage(), Variables.GHOSTBOT_GUILD
                        )))
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
    }*/
}
