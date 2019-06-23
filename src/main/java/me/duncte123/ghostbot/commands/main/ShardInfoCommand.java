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

package me.duncte123.ghostbot.commands.main;

import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.objects.CommandEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.ShardCacheView;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class ShardInfoCommand extends Command {
    @Override
    public void execute(CommandEvent ctx) {
        final List<String> headers = new ArrayList<>();
        headers.add("ID");
        headers.add("Status");
        headers.add("Ping");
        headers.add("Guilds");
        headers.add("VCs");

        final GuildMessageReceivedEvent event = ctx.getEvent();

        List<List<String>> table = new ArrayList<>();
        final ShardManager shardManager = ctx.getJDA().getShardManager();
        final List<JDA> shards = new ArrayList<>(shardManager.getShards());
        Collections.reverse(shards);

        for (final JDA shard : shards) {
            final List<String> row = new ArrayList<>();

            row.add(shard.getShardInfo().getShardId() +
                (ctx.getJDA().getShardInfo().getShardId() == shard.getShardInfo().getShardId() ? " (current)" : ""));

            row.add(WordUtils.capitalizeFully(shard.getStatus().toString().replace("_", " ")));
            row.add(String.valueOf(shard.getGatewayPing()));
            row.add(String.valueOf(shard.getGuilds().size()));

            final Pair<Long, Long> channelStats = getConnectedVoiceChannels(shard);

            row.add(channelStats.getLeft() + " / " + channelStats.getRight());
            table.add(row);

            if (table.size() == 20) {
                sendMsg(event, makeAsciiTable(headers, table, shardManager));
                table = new ArrayList<>();
            }
        }

        if (!table.isEmpty()) {
            sendMsg(event, makeAsciiTable(headers, table, shardManager));
        }
    }

    @Override
    public String getHelp() {
        return "Get information about all things shards";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }

    @Override
    public String getName() {
        return "shardinfo";
    }

    @Override
    public List<String> getAliases() {
        return List.of("shards");
    }

    /*
     * These 2 functions have been inspired from FlareBot
     * https://github.com/FlareBot/FlareBot/blob/master/src/main/java/stream/flarebot/flarebot/util/ShardUtils.java
     */
    private String makeAsciiTable(List<String> headers, List<List<String>> table, ShardManager shardManager) {
        final StringBuilder sb = new StringBuilder();
        final int padding = 1;
        final int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = 0;
        }
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).length() > widths[i]) {
                widths[i] = headers.get(i).length();
            }
        }
        for (final List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                final String cell = row.get(i);
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }

        final Pair<Long, Long> channelStats = getConnectedVoiceChannels(shardManager);
        final String statsString = channelStats.getLeft() + " / " + channelStats.getRight();

        if (statsString.length() > widths[widths.length - 1]) {
            widths[widths.length - 1] = statsString.length();
        }

        sb.append("```").append("prolog").append("\n");
        final StringBuilder formatLine = new StringBuilder("║");
        for (final int width : widths) {
            formatLine.append(" %-").append(width).append("s ║");
        }
        formatLine.append("\n");
        sb.append(appendSeparatorLine("╔", "╦", "╗", padding, widths));
        sb.append(String.format(formatLine.toString(), headers.toArray()));
        sb.append(appendSeparatorLine("╠", "╬", "╣", padding, widths));
        for (final List<String> row : table) {
            sb.append(String.format(formatLine.toString(), row.toArray()));
        }
        sb.append(appendSeparatorLine("╠", "╬", "╣", padding, widths));

        final ShardCacheView shardCache = shardManager.getShardCache();

        final String connectedShards = String.valueOf(shardCache.stream().filter(shard -> shard.getStatus() == JDA.Status.CONNECTED).count());
        final String avgPing = new DecimalFormat("###").format(shardManager.getAverageGatewayPing());
        final String guilds = String.valueOf(shardManager.getGuildCache().size());

        sb.append(String.format(
            formatLine.toString(),
            "Sum/Avg",
            connectedShards,
            avgPing,
            guilds,
            statsString
        ));
        sb.append(appendSeparatorLine("╚", "╩", "╝", padding, widths));
        sb.append("```");
        return sb.toString();
    }

    private String appendSeparatorLine(String left, String middle, String right, int padding, int... sizes) {
        boolean first = true;
        final StringBuilder ret = new StringBuilder();
        for (final int size : sizes) {
            if (first) {
                first = false;
                ret.append(left).append(StringUtils.repeat("═", size + padding * 2));
            } else {
                ret.append(middle).append(StringUtils.repeat("═", size + padding * 2));
            }
        }
        return ret.append(right).append("\n").toString();
    }

    private Pair<Long, Long> getConnectedVoiceChannels(ShardManager shardManager) {
        final AtomicLong connectedVC = new AtomicLong();
        final AtomicLong listeningVC = new AtomicLong();

        shardManager.getShardCache().forEach(
            (jda) -> {
                final Pair<Long, Long> shardStats = getConnectedVoiceChannels(jda);

                connectedVC.addAndGet(shardStats.getLeft());
                listeningVC.addAndGet(shardStats.getRight());
            }
        );

        return Pair.of(connectedVC.get(), listeningVC.get());
    }

    /**
     * @param shard
     *         the current shard
     *
     * @return a pair where
     * first  = connected channels
     * second = users listening in channel
     */
    private Pair<Long, Long> getConnectedVoiceChannels(JDA shard) {

        final long connectedVC = shard.getVoiceChannelCache().stream()
            .filter((vc) -> vc.getMembers().contains(vc.getGuild().getSelfMember())).count();

        final long listeningVC = shard.getVoiceChannelCache().stream().filter(
            (voiceChannel) -> voiceChannel.getMembers().contains(voiceChannel.getGuild().getSelfMember()))
            .mapToLong(
                (channel) -> channel.getMembers().stream().filter(
                    (member) -> !member.getUser().isBot() && !member.getVoiceState().isDeafened()
                ).count()
            ).sum();

        return Pair.of(connectedVC, listeningVC);
    }
}
