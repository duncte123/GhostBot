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

package me.duncte123.ghostbot;

import fredboat.audio.player.LavalinkManager;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

public class GhostBot {

    private static GhostBot instance;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    /*private final IntFunction<? extends Game> gameProvider = (it) -> Game.playing(
        String.format("GhostBot 3.0 | Now with popup blocker (shard %s)", it + 1)
    );*/
    private final IntFunction<? extends Game> gameProvider = (it) -> Game.watching(
        String.format("%shelp | #GoGhostAgain (shard %s)", Variables.PREFIX, it)
    );
    private final ShardManager shardManager;

    private GhostBot() throws LoginException {
        final Logger logger = LoggerFactory.getLogger(GhostBot.class);

        logger.info("Booting GhostBot");

        final String token = SpoopyUtils.getConfig().discord.token;
        final int totalShards = SpoopyUtils.getConfig().discord.totalShards;

        WebUtils.setUserAgent("Mozilla/5.0 (compatible; GhostBot/v" + Variables.VERSION + "; +https://github.com/duncte123/GhostBot)");
        EmbedUtils.setEmbedBuilder(
            () -> new EmbedBuilder()
                .setColor(Variables.EMBED_COLOR)
                .setFooter("GhostBot", Variables.FOOTER_ICON)
                .setTimestamp(Instant.now())
        );

        LavalinkManager.ins.start();

        final BotListener botListener = new BotListener();

        final DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder()
            .setShardsTotal(totalShards)
            .setToken(token)
            .setGameProvider(this.gameProvider)
            .setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE, CacheFlag.GAME))
            .addEventListeners(botListener);


        if (LavalinkManager.ins.isEnabled()) {
            builder.addEventListeners(LavalinkManager.ins.getLavalink());
        }

        shardManager = builder.build();
        initGameLoop();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public JDA getShard(int shardId) {
        return this.shardManager.getShardById(shardId);
    }

    private void initGameLoop() {
        service.scheduleAtFixedRate(
            () -> this.shardManager.setGameProvider(this.gameProvider)
            , 1, 1, TimeUnit.DAYS);
    }

    public static void main(String[] args) throws LoginException {
        instance = new GhostBot();
    }

    public static synchronized GhostBot getInstance() {
        return instance;
    }
}
