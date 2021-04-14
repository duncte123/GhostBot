/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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

import me.duncte123.botcommons.messaging.MessageConfig;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class RestartCommand extends Command {
    @Override
    public void execute(ICommandEvent event) {
        final List<String> args = event.getArgs();

        if (event.getAuthor().getIdLong() != Variables.OWNER_ID || event.isSlash()) {
            sendMsg(event, "No permission");

            return;
        }

        final ShardManager manager = event.getJDA().getShardManager();

        if (manager == null) {
            sendMsg(event, "shard manager is null WTF");
            return;
        }

        if (args.isEmpty()) {
            sendMsg(MessageConfig.Builder
                .fromCtx(event)
                .setMessage("Restarting all shards")
                .setSuccessAction((it) -> manager.restart())
            );

            return;
        }

        final int toRestart = Integer.parseInt(args.get(0));

        if (toRestart > manager.getShardsTotal() - 1) {
            sendMsg(event, "Invalid shard");

            return;
        }

        sendMsg(MessageConfig.Builder
            .fromCtx(event)
            .setMessage("Restarting shard " + toRestart)
            .setSuccessAction((it) -> manager.restart(toRestart))
        );
    }

    @Override
    public String getName() {
        return "restart";
    }

    @Override
    public String getHelp() {
        return "restarts a shard or the bot\nUsage: `" + Variables.PREFIX + getName() + " [shard id]`";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }
}
