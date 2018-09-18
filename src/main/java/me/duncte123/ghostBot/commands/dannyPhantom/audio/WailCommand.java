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
import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class WailCommand extends Command {

    public WailCommand() {
        this.audioPath = SpoopyUtils.audio.BASE_AUDIO_DIR + "wail/";
        reloadAudioFiles();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        doAudioStuff(event);
    }

    @Override
    public String getName() {
        return "wail";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.AUDIO;
    }

    @Override
    public String getHelp() {
        return "Gives you a nice ghostly wail";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ghostlywail"};
    }
}
