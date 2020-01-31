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
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

public class GhostBot {

    private static GhostBot instance;
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final IntFunction<? extends Activity> activityProvider = (it) -> {
        final Activity[] activities = {
            Activity.playing("Doomed"),
            Activity.watching(String.format("%shelp | #GoGhostAgain (shard %s)", Variables.PREFIX, it + 1)),
            Activity.playing(String.format("GhostBot 3.0 | Now with popup blocker (shard %s)", it + 1)),
        };


        return activities[(int) Math.floor(Math.random() * activities.length)];
    };
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
        EmbedUtils.setEmbedBuilder(
            () -> new EmbedBuilder().setColor(Variables.EMBED_COLOR)
        );

        final LavalinkManager llm = LavalinkManager.ins;

        llm.start(config, container.getAudio());

        final BotListener botListener = new BotListener(container);
        final DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder()
            .setShardsTotal(totalShards)
            .setToken(token)
            .setActivityProvider(this.activityProvider)
            .setChunkingFilter(ChunkingFilter.NONE) // Lazy loading :)
            .setEnabledCacheFlags(EnumSet.of(CacheFlag.VOICE_STATE))
//            .setGuildSubscriptionsEnabled(false)
//            .setEnabledCacheFlags(EnumSet.allOf(CacheFlag.class))
            .setMemberCachePolicy(MemberCachePolicy.VOICE)
            .setDisabledIntents(
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_EMOJIS,
                GatewayIntent.GUILD_INVITES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.DIRECT_MESSAGE_TYPING
            )
            .addEventListeners(botListener);

        if (llm.isEnabled()) {
            builder.addEventListeners(llm.getLavalink());
            builder.setVoiceDispatchInterceptor(llm.getLavalink().getVoiceInterceptor());
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
            () -> this.shardManager.setActivityProvider(this.activityProvider)
            , 1, 1, TimeUnit.DAYS);
    }

    public static void main(String[] args) throws LoginException, IOException {
        instance = new GhostBot();
//        genAudioJson();
    }

    // Helper method for when we add audio files
    /*private static void genAudioJson() throws IOException {
        final Map<String, List<String>> output = new HashMap<>();
        final File audioFileDir = new File("audioFiles");

        final File[] listOfFiles = audioFileDir.listFiles();

        if (listOfFiles == null || listOfFiles.length == 0) {
            return;
        }

        for (final File file : listOfFiles) {

            if (file.isDirectory()) {
                final List<String> filesFound = new ArrayList<>();

                for (final File audioFile: file.listFiles()) {
                    if (audioFile.isFile()) {
                        final String name = audioFile.getName();

                        filesFound.add(name);
                    }
                }

                output.put(file.getName(), filesFound);
            }
        }

        new ObjectMapper().writeValue(new File("audioList.json"), output);
    }*/

    public static synchronized GhostBot getInstance() {
        return instance;
    }
}
