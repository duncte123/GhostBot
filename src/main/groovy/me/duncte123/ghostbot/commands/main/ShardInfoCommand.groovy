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
import me.duncte123.ghostbot.objects.CommandEvent
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.WordUtils

import java.text.DecimalFormat

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class ShardInfoCommand extends Command {

    @Override
    void execute(CommandEvent commandEvent) {

        def event = commandEvent.event

        def headers = new ArrayList<String>()
        headers.add('ID')
        headers.add('Status')
        headers.add('Ping')
        headers.add('Guilds')
        headers.add('VCs')

        def table = new ArrayList<List<String>>()
        def shardManager = event.JDA.asBot().getShardManager()
        def shards = new ArrayList<JDA>(shardManager.getShards()).reverse()

        for (JDA shard : shards) {
            def row = new ArrayList<String>()

            row.add(shard.shardInfo.shardId +
                (event.JDA.shardInfo.shardId == shard.shardInfo.shardId ? ' (current)' : ''))

            row.add(WordUtils.capitalizeFully(shard.status.toString().replace('_', ' ')))

            row.add(String.valueOf(shard.ping))
            row.add(String.valueOf(shard.guildCache.size()))

            def listeningVC = shard.voiceChannelCache.stream().filter { vc ->
                vc.members.contains(vc.guild.selfMember)
            }
            .mapToLong { it ->
                it.members.stream().filter { itt ->
                    !itt.user.bot && !itt.voiceState.deafened
                }.count()
            }.sum().toString()

            row.add(listeningVC)
            table.add(row)

            if (table.size() == 20) {
                sendMsg(event, makeAsciiTable(headers, table, shardManager))
                table = new ArrayList<String>()
            }
        }
        if (table.size() > 0) {
            sendMsg(event, makeAsciiTable(headers, table, shardManager))
        }

    }

    @Override
    String getName() { 'shardinfo' }

    @Override
    String getHelp() { 'Shows some shardinfo' }

    @Override
    String[] getAliases() { ['shards'] }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.HIDDEN
    }

    /*
     * These 2 functions have been inspired from FlareBot
     * https://github.com/FlareBot/FlareBot/blob/master/src/main/java/stream/flarebot/flarebot/util/ShardUtils.java
     */

    private static String makeAsciiTable(List<String> headers, List<List<String>> table, ShardManager shardManager) {
        def sb = new StringBuilder()
        def padding = 1
        def widths = new int[headers.size()]

        for (int i = 0; i < widths.length; i++) {
            widths[i] = 0
        }
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).length() > widths[i]) {
                widths[i] = headers.get(i).length()
            }
        }
        for (List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                def cell = row.get(i)
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length()
                }
            }
        }

        sb.append('```').append('PROLOG').append('\n')
        def formatLine = new StringBuilder('║')

        for (int width : widths) {
            formatLine.append(' %-').append(width).append('s ║')
        }

        formatLine.append('\n')
        sb.append(appendSeparatorLine('╔', '╦', '╗', padding, widths))
        sb.append(String.format(formatLine.toString(), headers.toArray()))
        sb.append(appendSeparatorLine('╠', '╬', '╣', padding, widths))

        for (List<String> row : table) {
            sb.append(String.format(formatLine.toString(), row.toArray()))
        }

        sb.append(appendSeparatorLine('╠', '╬', '╣', padding, widths))

        def connectedShards = String.valueOf(shardManager.shardCache.stream().filter {
            shard -> shard.status == JDA.Status.CONNECTED
        }.count())
        def avgPing = new DecimalFormat('###').format(shardManager.averagePing)
        def guilds = String.valueOf(shardManager.guildCache.size())

        def connectedVC = shardManager.shardCache.stream().mapToLong { shard ->
            shard.voiceChannelCache.stream().filter {
                vc -> vc.members.contains(vc.guild.selfMember)
            }.count()
        }.sum()

        def listeningVC = shardManager.shardCache.stream().mapToLong { shard ->
            shard.voiceChannelCache.stream().filter { vc ->
                vc.members.contains(vc.guild.selfMember)
            }
            .mapToLong { it ->
                it.members.stream().filter { itt ->
                    !itt.user.bot && !itt.voiceState.deafened
                }.count()
            }.sum()
        }.sum()

        sb.append(String.format(formatLine.toString(), 'Sum/Avg', connectedShards, avgPing, guilds, "$connectedVC / $listeningVC"))
        sb.append(appendSeparatorLine('╚', '╩', '╝', padding, widths))
        sb.append('```')

        return sb.toString()
    }

    private static String appendSeparatorLine(String left, String middle, String right, int padding, int ... sizes) {
        def first = true
        def ret = new StringBuilder()

        for (int size : sizes) {
            if (first) {
                first = false
                ret.append(left).append(StringUtils.repeat('═', size + padding * 2))
            } else {
                ret.append(middle).append(StringUtils.repeat('═', size + padding * 2))
            }
        }

        return ret.append(right).append('\n').toString()
    }
}
