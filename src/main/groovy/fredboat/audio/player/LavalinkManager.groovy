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

package fredboat.audio.player

import lavalink.client.io.Lavalink
import lavalink.client.io.Link
import lavalink.client.io.jda.JdaLavalink
import lavalink.client.player.IPlayer
import lavalink.client.player.LavaplayerPlayerWrapper
import me.duncte123.ghostbot.GhostBot
import me.duncte123.ghostbot.objects.config.GhostBotConfig
import me.duncte123.ghostbot.utils.AudioUtils
import me.duncte123.ghostbot.utils.SpoopyUtils
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel

import javax.annotation.Nonnull

/**
 * This class has been taken from
 * https://github.com/Frederikam/FredBoat/blob/master/FredBoat/src/main/java/fredboat/audio/player/LavalinkManager.java\
 * and has been modified to fit my needs
 */
class LavalinkManager {

    public static final LavalinkManager ins = new LavalinkManager()
    private JdaLavalink lavalink = null

    private LavalinkManager() {}

    void start() {
        if (enabled) {

            def userId = getIdFromToken(SpoopyUtils.config.discord.token)

            lavalink = new JdaLavalink(
                    userId,
                    SpoopyUtils.config.discord.totalShards,
                    {
                        return GhostBot.instance.getShard(it)
                    })

            for (GhostBotConfig.Lavalink.Node it : SpoopyUtils.config.lavalink.nodes) {
                try {
                    lavalink.addNode(new URI(it.wsUrl), it.pass)
                } catch (URISyntaxException e) {
                    e.printStackTrace()
                }
            }
        }
    }

    boolean isEnabled() {
        return SpoopyUtils.config.lavalink.enable
    }

    IPlayer createPlayer(String guildId) {
        return enabled ? lavalink.getLink(guildId).player
                : new LavaplayerPlayerWrapper(AudioUtils.instance.playerManager.createPlayer())
    }

    void openConnection(VoiceChannel channel) {
        if (enabled) {
            lavalink.getLink(channel.guild).connect(channel)
        } else {
            channel.guild.audioManager.openAudioConnection(channel)
        }
    }

    boolean isConnected(Guild g) {
        return enabled ?
                lavalink.getLink(g).state == Link.State.CONNECTED :
                g.audioManager.connected
    }

    void closeConnection(Guild guild) {
        if (enabled) {
            lavalink.getLink(guild).disconnect()
        } else {
            guild.audioManager.closeAudioConnection()
        }
    }

    VoiceChannel getConnectedChannel(@Nonnull Guild guild) {
        //NOTE: never use the local audio manager, since the audio connection may be remote
        // there is also no reason to look the channel up remotely from lavalink, if we have access to a real guild
        // object here, since we can use the voice state of ourselves (and lavalink 1.x is buggy in keeping up with the
        // current voice channel if the bot is moved around in the client)
        return guild.selfMember.voiceState.channel
    }

    Lavalink getLavalink() {
        return lavalink
    }

    /**
     * This is a simple util function that extracts the bot id from the token
     *
     * @param token
     *         the token of your bot
     *
     * @return the client id of the bot
     */
    private static String getIdFromToken(String token) {

        return new String(
                Base64.decoder.decode(
                        token.split('\\.')[0]
                )
        )
    }
}
