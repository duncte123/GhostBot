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

package fredboat.audio.player

import com.afollestad.ason.Ason
import lavalink.client.io.Lavalink
import lavalink.client.io.Link
import lavalink.client.player.IPlayer
import lavalink.client.player.LavaplayerPlayerWrapper
import me.duncte123.ghostBot.GhostBot
import me.duncte123.ghostBot.audio.LavalinkNode
import me.duncte123.ghostBot.utils.AudioUtils
import me.duncte123.ghostBot.utils.SpoopyUtils
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel

import javax.annotation.Nonnull

class LavalinkManager {

    public static final LavalinkManager ins = new LavalinkManager()
    Lavalink lavalink = null

    private LavalinkManager() {}

    void start() {
        if (!isEnabled()) return

        String userId = getIdFromToken(SpoopyUtils.CONFIG.getString("discord.token"))

        lavalink = new Lavalink(
                userId,
                SpoopyUtils.CONFIG.getInt("discord.totalShards", 1),
                { GhostBot.instance.getFakeShard(it) }
        )
        List<LavalinkNode> defaultNodes = new ArrayList<>()
        defaultNodes.add(new LavalinkNode(new Ason("{\"wsUrl\": \"ws://localhost\",\"pass\": \"youshallnotpass\"}")))
        List<Ason> nodes = SpoopyUtils.CONFIG.getArray("lavalink.nodes", defaultNodes)
        List<LavalinkNode> nodeList = new ArrayList<>()
        nodes.forEach {
            nodeList.add(new LavalinkNode(it))
        }

        nodeList.forEach {
            try {
                lavalink.addNode(new URI(it.wsUrl), it.pass)
            } catch (URISyntaxException e) {
                e.printStackTrace()
            }
        }

    }

    boolean isEnabled() {
        return SpoopyUtils.CONFIG.getBoolean("lavalink.enable", false)
    }

    IPlayer createPlayer(String guildId) {
        return isEnabled() ? lavalink.getLink(guildId).player
                : new LavaplayerPlayerWrapper(AudioUtils.ins.playerManager.createPlayer())
    }

    void openConnection(VoiceChannel channel) {
        if (isEnabled()) {
            lavalink.getLink(channel.guild).connect(channel)
        } else {
            channel.guild.audioManager.openAudioConnection(channel)
        }
    }

    boolean isConnected(Guild g) {
        return isEnabled() ?
                lavalink.getLink(g).state == Link.State.CONNECTED :
                g.audioManager.connected
    }

    void closeConnection(Guild guild) {
        if (isEnabled()) {
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
/*
    public Lavalink getLavalink() {
        return lavalink;
    }*/

    /**
     * This is a simple util function that extracts the bot id from the token
     *
     * @param token the token of your bot
     * @return the client id of the bot
     */
    private String getIdFromToken(String token) {

        return new String(
                Base64.decoder.decode(
                        token.split("\\.")[0]
                )
        )
    }

}
