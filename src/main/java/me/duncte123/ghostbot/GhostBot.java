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

package me.duncte123.ghostbot;

import fredboat.audio.player.LavalinkManager;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

public class GhostBot {

    private static GhostBot instance;
    private final ShardManager shardManager;

    private GhostBot() throws LoginException, IOException {
        final Logger logger = LoggerFactory.getLogger(GhostBot.class);

        logger.info("Booting GhostBot");

        final Container container = new Container();
        final GhostBotConfig config = container.getConfig();
        final String token = config.discord.token;
        final int totalShards = config.discord.totalShards;

        Variables.PREFIX = config.discord.prefix;

        WebUtils.setUserAgent("Mozilla/5.0 (compatible; GhostBot/v" + Variables.VERSION + "; +https://github.com/duncte123/GhostBot)");
        EmbedUtils.setDefaultColor(Variables.EMBED_COLOR);

        final LavalinkManager llm = LavalinkManager.ins;

        llm.start(config, container.getAudio());

        final BotListener botListener = new BotListener(container);
        final DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token)
            .setShardsTotal(totalShards)
            .setActivityProvider(
                (id) -> Activity.watching(String.format("%shelp | shard %d", Variables.PREFIX, id + 1))
            )
            .setChunkingFilter(ChunkingFilter.NONE) // Lazy loading :)
            .enableCache(VOICE_STATE, MEMBER_OVERRIDES)
            .disableCache(ACTIVITY, EMOTE, CLIENT_STATUS)
            .setMemberCachePolicy(MemberCachePolicy.VOICE)
            .setGatewayEncoding(GatewayEncoding.ETF)
            .setEnabledIntents(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES
            )
            .addEventListeners(botListener);

        if (llm.isEnabled()) {
            builder.addEventListeners(llm.getLavalink());
            builder.setVoiceDispatchInterceptor(llm.getLavalink().getVoiceInterceptor());
        }

        shardManager = builder.build();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public JDA getShard(int shardId) {
        return this.shardManager.getShardById(shardId);
    }

    public static void main(String[] args) throws LoginException, IOException {
        instance = new GhostBot();
    }

    public static synchronized GhostBot getInstance() {
        return instance;
    }
}
