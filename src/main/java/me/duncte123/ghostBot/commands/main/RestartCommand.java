package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class RestartCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() != Variables.OWNER_ID) {
            sendMsg(event, "No permission");
            return;
        }

        ShardManager manager = event.getJDA().asBot().getShardManager();

        if (args.length < 1) {
            sendMsg(event, "Restarting all shards",
                    (message) -> manager.restart()
            );
            return;
        }

        int toRestart = Integer.parseInt(args[0]);

        if (toRestart > manager.getShardsTotal() - 1) {
            sendMsg(event, "Invalid shard");
            return;
        }

        sendMsg(event, "Restarting shard " + toRestart,
                (message) -> manager.restart(toRestart)
        );

    }

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String getHelp() {
        return "restarts a shard or the bot\n" +
                "Usage: `" + Variables.PREFIX + getName() + " [shard id]`";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }
}
