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

package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed;

public class AboutCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        sendEmbed(event, EmbedUtils.embedMessage("Hey there I'm GhostBot, I'm here for all your <:DPEmblemInvertStroke:402746292788264960> Danny Phantom needs.\n" +
                "I'm being developed by duncte123 (duncte123#1245) and if you have any ideas of what to add to me you can contact him or join [this server](https://discord.gg/NKM9Xtk)\n\n" +

                "**Useful information:**\n" +
                "Invite link: [https://discordapp.com/oauth2/authorize?client_id=397297702150602752&scope=bot&permissions=8](https://discordapp.com/oauth2/authorize?client_id=397297702150602752&scope=bot&permissions=8)\n" +
                "Prefix: `" + Variables.PREFIX + "`\n" +
                "Support server: [https://discord.gg/NKM9Xtk](https://discord.gg/NKM9Xtk)\n" +
                "Amount of servers that I'm in: " + event.getJDA().getGuildCache().size()
        ));
    }

    @Override
    public String getName() {
        return "about";
    }

    @Override
    public String getHelp() {
        return "Get some info about the bot";
    }

}
