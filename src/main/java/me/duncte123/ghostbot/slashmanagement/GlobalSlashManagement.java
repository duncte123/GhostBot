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

package me.duncte123.ghostbot.slashmanagement;

import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.utils.Container;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;

import static me.duncte123.ghostbot.slashmanagement.SlashHelper.collectCommands;

public class GlobalSlashManagement implements EventListener {
    private final boolean clear;
    private final Container container;
    private final ShardManager shardManager;

    public GlobalSlashManagement(boolean clear) throws IOException, LoginException {
        this.clear = clear;
        this.container = new Container();
        final GhostBotConfig config = this.container.getConfig();

        this.shardManager = DefaultShardManagerBuilder.createDefault(config.discord.token)
            .setShardsTotal(config.discord.totalShards)
            .addEventListeners(this)
            .build();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            final JDA jda = event.getJDA();

            if (jda.getShardInfo().getShardId() > 0) {
                return;
            }

            final var action = jda.updateCommands();

            if (!this.clear) {
                action.addCommands(
                    collectCommands(this.container.getCommandManager())
                );
            }

            action.queue((__) -> this.shardManager.shutdown());
        }
    }
}
