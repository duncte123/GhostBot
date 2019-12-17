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

package me.duncte123.ghostbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fredboat.audio.player.LavalinkManager;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.AudioEventAdapterWrapped;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager extends AudioEventAdapterWrapped {

    private final IPlayer player;

    public GuildMusicManager(Guild g) {
        player = LavalinkManager.ins.createPlayer(g.getId());
        player.addListener(this);
    }

    public IPlayer getPlayer() {
        return player;
    }

    public AudioPlayerSenderHandler getSendHandler() {
        return new AudioPlayerSenderHandler(player);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        System.out.println("Started track " + track.getIdentifier());
    }
}
