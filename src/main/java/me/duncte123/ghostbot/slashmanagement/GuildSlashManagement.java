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
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;

import static me.duncte123.ghostbot.slashmanagement.SlashHelper.collectCommands;

public class GuildSlashManagement implements EventListener {
    private static final long DEV_GUILD_ID = 191245668617158656L;

    private final boolean clear;
    private final Container container;

    public GuildSlashManagement(boolean clear) throws IOException, LoginException {
        this.clear = clear;
        final Container container = new Container();
        this.container = container;
        final GhostBotConfig config = container.getConfig();
        final String token = config.discord.token;

        JDABuilder.createDefault(token)
            .addEventListeners(this)
            .build();
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            final var action = event.getJDA()
                .getGuildById(DEV_GUILD_ID)
                .updateCommands();

            if (!this.clear) {
                action.addCommands(
                    collectCommands(this.container.getCommandManager())
                );
            }

            action.queue();
            event.getJDA().shutdown();
        }
    }
}
