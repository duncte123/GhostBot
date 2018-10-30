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

package me.duncte123.ghostbot.commands.main

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed

class AboutCommand extends Command {

    AboutCommand() {}

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        def devName = getDevName(event)
        def guildCount = event.JDA.asBot().shardManager.guildCache.size()

        sendEmbed(event, EmbedUtils.embedMessage(
                """Hey there I'm GhostBot, I'm here for all your <:DPEmblemInvertStroke:422022982450282496> Danny Phantom needs.
I'm being developed by $devName and if you have any ideas of what to add to me you can contact him or join [this server](https://discord.gg/NKM9Xtk)

**Useful information:**
Invite link: [Click HERE](https://discordapp.com/oauth2/authorize?client_id=397297702150602752&scope=bot&permissions=8)
Prefix: `${Variables.PREFIX}`
Support server: [https://discord.gg/NKM9Xtk](https://discord.gg/NKM9Xtk)
Amount of servers that I'm in: $guildCount"""
        ))

    }

    @Override
    String getName() {
        "about"
    }

    @Override
    String getHelp() {
        "Get some info about the bot"
    }

    private static String getDevName(GuildMessageReceivedEvent event) {

        def devId = "191231307290771456"
        def defaultVal = "duncte123 (duncte123#1245)"

        def foundCount = event.guild.memberCache.stream()
                .map { it.user }.map { it.id }.filter { it == devId }.count()

        if (foundCount > 0) {
            return event.guild.getMemberById(devId).asMention
        }

        return defaultVal
    }
}
