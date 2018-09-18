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

package me.duncte123.ghostBot.commands.dannyPhantom.image;

import me.duncte123.ghostBot.objects.CommandCategory;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class GifCommand extends ImageBase {

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        String keyword = "Danny Phantom gif";

        requestSearch(keyword,
                data -> sendMessageFromData(event, data, keyword),
                er -> sendMsg(event, "Error while looking up image: " + er));
    }

    @Override
    public String getName() {
        return "gif";
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom gif";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGE;
    }
}
