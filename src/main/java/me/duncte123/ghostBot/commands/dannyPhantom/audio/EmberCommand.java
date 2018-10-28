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

package me.duncte123.ghostBot.commands.dannyPhantom.audio;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.utils.AudioUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class EmberCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (preAudioChecks(event)) {
            sendMsg(event, "Selected track: _Ember McLain - Remember_");
            AudioUtils.getInstance().loadAndPlay(getMusicManager(event.getGuild()), event.getChannel(), "wBMOc24_aIw", false);
        }
    }

    @Override
    public String getName() {
        return "remember";
    }

    @Override
    public String getHelp() {
        return "Plays the song \"Remember\" in the voice channel that you are in";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.AUDIO;
    }
}
