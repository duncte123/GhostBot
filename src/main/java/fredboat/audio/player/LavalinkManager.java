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

package fredboat.audio.player;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkPlayer;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.utils.AudioUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class LavalinkManager {
    public static final LavalinkManager ins = new LavalinkManager();
    private LavalinkClient lavalink = null;
    private GhostBotConfig config;
    private AudioUtils audio;

    private LavalinkManager() {}

    public void start(GhostBotConfig config, AudioUtils audio) {
        this.config = config;
        this.audio = audio;

        if (isEnabled()) {
            lavalink = new LavalinkClient(Helpers.getUserIdFromToken(this.config.discord.token));

            for (final GhostBotConfig.Lavalink.Node it : this.config.lavalink.nodes) {
                try {
                    final URI uri = new URI(it.wsUrl);

                    lavalink.addNode(uri.getHost(), uri, it.pass);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isEnabled() {
        return this.config.lavalink.enable;
    }

    public Mono<LavalinkPlayer> createPlayer(long guildId) {
        if (!isEnabled()) {
            throw new RuntimeException("Lavalink is not enabled");
        }

        return lavalink.getLink(guildId).getPlayer();
    }

    public boolean isConnected(long guildId) {
        if (!isEnabled()) {
            throw new RuntimeException("Lavalink is not enabled");
        }

        return lavalink.getLink(guildId).getState() == Link.State.CONNECTED;
    }

    public boolean isConnected(Guild g) {
        return isEnabled() ?
            lavalink.getLink(g).getState() == Link.State.CONNECTED :
            g.getAudioManager().isConnected();
    }

    public void closeConnection(Guild guild) {
        guild.getJDA().getDirectAudioController().disconnect(guild);
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

    private static String getIdFromToken(String token) {

        return new String(
            Base64.getDecoder().decode(
                token.split("\\.")[0]
            )
        );
    }

}
