package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.CommandCategory;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class ShardInfoCommand extends Command {

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        List<String> headers = new ArrayList<>();
        headers.add("Shard ID");
        headers.add("Status");
        headers.add("Ping");
        headers.add("Guild Count");
        headers.add("Connected VCs");

        List<List<String>> table = new ArrayList<>();
        ShardManager shardManager = event.getJDA().asBot().getShardManager();
        List<JDA> shards = new ArrayList<>(shardManager.getShards());
        Collections.reverse(shards);
        for (JDA shard : shards) {
            List<String> row = new ArrayList<>();
            row.add(shard.getShardInfo().getShardId() +
                    (event.getJDA().getShardInfo().getShardId() == shard.getShardInfo().getShardId() ? " (current)" : ""));
            row.add(WordUtils.capitalizeFully(shard.getStatus().toString().replace("_", " ")));
            row.add(String.valueOf(shard.getPing()));
            row.add(String.valueOf(shard.getGuilds().size()));

            String listening = shard.getVoiceChannelCache().stream().filter(vc -> vc.getMembers().contains(vc.getGuild()
                    .getSelfMember())).count() + " / " +
                    shard.getVoiceChannelCache().stream().filter(vc ->
                            vc.getMembers().contains(vc.getGuild().getSelfMember()))
                            .mapToLong(it ->
                                    it.getMembers().stream().filter(itt ->
                                            !itt.getUser().isBot() && !itt.getVoiceState().isDeafened()).count()
                            ).sum();

            row.add(listening);
            table.add(row);
            if (table.size() == 20) {
                sendMsg(event, makeAsciiTable(headers, table, shardManager));
                table = new ArrayList<>();
            }
        }
        if (table.size() > 0) {
            sendMsg(event, makeAsciiTable(headers, table, shardManager));
        }
    }

    @Override
    public String getName() {
        return "shardinfo";
    }

    @Override
    public String getHelp() {
        return "Shows some shardinfo";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }

    /*
     * These 2 functions have been inspired from FlareBot
     * https://github.com/FlareBot/FlareBot/blob/master/src/main/java/stream/flarebot/flarebot/util/ShardUtils.java
     */
    private String makeAsciiTable(List<String> headers, List<List<String>> table, ShardManager shardManager) {
        StringBuilder sb = new StringBuilder();
        int padding = 1;
        int[] widths = new int[headers.size()];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = 0;
        }
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).length() > widths[i]) {
                widths[i] = headers.get(i).length();
            }
        }
        for (List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                if (cell.length() > widths[i]) {
                    widths[i] = cell.length();
                }
            }
        }
        sb.append("```").append("PROLOG").append("\n");
        StringBuilder formatLine = new StringBuilder("║");
        for (int width : widths) {
            formatLine.append(" %-").append(width).append("s ║");
        }
        formatLine.append("\n");
        sb.append(appendSeparatorLine("╔", "╦", "╗", padding, widths));
        sb.append(String.format(formatLine.toString(), headers.toArray()));
        sb.append(appendSeparatorLine("╠", "╬", "╣", padding, widths));
        for (List<String> row : table) {
            sb.append(String.format(formatLine.toString(), row.toArray()));
        }
        sb.append(appendSeparatorLine("╠", "╬", "╣", padding, widths));
        String connectedShards = String.valueOf(shardManager.getShards().stream().filter(shard -> shard.getStatus() == JDA.Status.CONNECTED).count());
        String avgPing = new DecimalFormat("###").format(shardManager.getAveragePing());
        String guilds = String.valueOf(shardManager.getGuildCache().size());
        long connectedVC = shardManager.getShardCache().stream().mapToLong(shard ->
                shard.getVoiceChannelCache().stream().filter(vc -> vc.getMembers().contains(vc.getGuild().getSelfMember())).count()
        ).sum();
        long listeningVC = shardManager.getShardCache().stream().mapToLong(shard ->
                shard.getVoiceChannelCache().stream().filter(vc ->
                        vc.getMembers().contains(vc.getGuild().getSelfMember()))
                        .mapToLong(it ->
                                it.getMembers().stream().filter(itt ->
                                        !itt.getUser().isBot() && !itt.getVoiceState().isDeafened()).count()
                        ).sum()
        ).sum();
        sb.append(String.format(formatLine.toString(), "Sum/Avg", connectedShards, avgPing, guilds, connectedVC + " / " + listeningVC));
        sb.append(appendSeparatorLine("╚", "╩", "╝", padding, widths));
        sb.append("```");
        return sb.toString();
    }

    private String appendSeparatorLine(String left, String middle, String right, int padding, int... sizes) {
        boolean first = true;
        StringBuilder ret = new StringBuilder();
        for (int size : sizes) {
            if (first) {
                first = false;
                ret.append(left).append(StringUtils.repeat("═", size + padding * 2));
            } else {
                ret.append(middle).append(StringUtils.repeat("═", size + padding * 2));
            }
        }
        return ret.append(right).append("\n").toString();
    }
}
