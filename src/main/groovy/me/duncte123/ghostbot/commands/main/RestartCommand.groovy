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

import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class RestartCommand extends Command {
    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (event.author.idLong != Variables.OWNER_ID) {
            sendMsg(event, 'No permission')
            return
        }

        def manager = event.JDA.asBot().shardManager

        if (args.length < 1) {
            sendMsg(event, 'Restarting all shards') {
                manager.restart()
            }
            return
        }

        def toRestart = Integer.parseInt(args[0])

        if (toRestart > manager.shardsTotal - 1) {
            sendMsg(event, 'Invalid shard')
            return
        }

        sendMsg(event, "Restarting shard $toRestart") {
            manager.restart(toRestart)
        }
    }

    @Override
    String getName() { 'restart' }

    @Override
    String getHelp() { "restarts a shard or the bot\nUsage: `$Variables.PREFIX$name [shard id]`" }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.HIDDEN
    }
}
