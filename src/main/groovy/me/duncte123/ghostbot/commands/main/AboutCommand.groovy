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
import me.duncte123.ghostbot.GhostBot
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.variables.Variables

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed

class AboutCommand extends Command {

    @Override
    void execute(CommandEvent event) {

        def devName = '<@191231307290771456> (duncte123#1245)'
        def guildCountDiscord = GhostBot.instance.shardManager.guildCache.size()

        sendEmbed(event.event, EmbedUtils.embedMessage(
            """\
Hey there, my name is GhostBot, I am the must have bot for your spooky server.
I am manly themed around Danny Phantom but other spooky stuff that you have for me can be suggested to $devName.
If you want to stay in contact with my developer you can join [this server]($Variables.GHOSTBOT_GUILD).

**Extra information:**
My twitter: [Click here](https://twitter.com/GhostBotDiscord)
My invite link: [Click here]($Variables.GHOSTBOT_INVITE)
My prefixes: `$Variables.PREFIX` and `$Variables.OTHER_PREFIX`
My home: [$Variables.GHOSTBOT_GUILD]($Variables.GHOSTBOT_GUILD)
My version: `$Variables.VERSION`
The amount of Discord servers that I am in: $guildCountDiscord"""
        ))

    }

    @Override
    String getName() { 'about' }

    @Override
    String getHelp() { 'Get some info about the bot' }
}
