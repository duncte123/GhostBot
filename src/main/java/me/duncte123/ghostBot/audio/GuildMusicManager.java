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

import fredboat.audio.player.LavalinkManager;
import lavalink.client.player.IPlayer;
import net.dv8tion.jda.core.entities.Guild;

public class GuildMusicManager {

    /**
     * This is our player
     */
    public final IPlayer player;

    /**
     * This is the scheduler
     */
    public final TrackScheduler scheduler;

    /**
     * This is what actually sends the audio
     */
    private final AudioPlayerSenderHandler sendHandler;

    /**
     * Constructor
     *
     * @param g The guild that we wannt the manager for
     */
    public GuildMusicManager(Guild g) {
        player = LavalinkManager.ins.createPlayer(g.getId());
        scheduler = new TrackScheduler(player);
        sendHandler = new AudioPlayerSenderHandler(player);
        player.addListener(scheduler);
    }

    /**
     * This will get our sendings handler
     *
     * @return The {@link AudioPlayerSenderHandler thing} that sends our audio
     */
    public AudioPlayerSenderHandler getSendHandler() {
        return sendHandler;
    }
}