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
import lavalink.client.player.IPlayer;
import me.duncte123.botcommons.BotCommons;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.audio.GuildMusicManager;
import me.duncte123.ghostbot.commands.main.UptimeCommand;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.utils.AudioUtils;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.sharding.ShardManager;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static me.duncte123.botcommons.web.ContentType.JSON;

public class BotListener implements EventListener {

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger(BotListener.class);
    /*private final TLongList botLists = new TLongArrayList(new long[]{
        110373943822540800L, // Dbots
        264445053596991498L, // Dbl
        374071874222686211L, // Bots for discord
        112319935652298752L, // Carbon
        439866052684283905L, // Discord Boats
        387812458661937152L, // Botlist.space
        483344253963993113L, // AutomaCord
        454933217666007052L, // Divine Discord Bot List
        446682534135201793L, // Discords best bots
        477792727577395210L, // discordbotlist.xyz
        475571221946171393L, // bots.discordlist.app
    });*/
    private final CommandManager commandManager;
    private final GhostBotConfig config;
    private final AudioUtils audio;
    private final Container container;

    BotListener(Container container) {
        this.commandManager = container.getCommandManager();
        this.config = container.getConfig();
        this.audio = container.getAudio();
        this.container = container;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        if (event instanceof ReadyEvent) {
            this.onReady((ReadyEvent) event);
        } else if (event instanceof GuildMessageReceivedEvent) {
            this.onGuildMessageReceived((GuildMessageReceivedEvent) event);
        } else if (event instanceof GuildJoinEvent) {
            this.onGuildJoin((GuildJoinEvent) event);
        } else if (event instanceof GuildLeaveEvent) {
            this.onGuildLeave((GuildLeaveEvent) event);
        } else if (event instanceof GuildVoiceLeaveEvent) {
            this.onGuildVoiceLeave((GuildVoiceLeaveEvent) event);
        } else if (event instanceof GuildVoiceMoveEvent) {
            this.onGuildVoiceMove((GuildVoiceMoveEvent) event);
        } else if (event instanceof MessageReactionAddEvent) {
            this.onMessageReactionAdd((MessageReactionAddEvent) event);
        } else if (event instanceof SlashCommandEvent) {
            this.onSlashCommand((SlashCommandEvent) event);
        }
    }

    private void onReady(@Nonnull ReadyEvent event) {
        final JDA jda = event.getJDA();

        logger.info("Logged in as {} ({})", jda.getSelfUser(), jda.getShardInfo());
        postServerCount();

        final var commands = this.commandManager.getCommands()
            .stream()
            .filter((it) -> it.getCategory() != CommandCategory.HIDDEN)
            .map(Command::getCommandData)
            .collect(Collectors.toList());

        jda.updateCommands()
            .addCommands(commands)
            .queue((__) -> logger.info("Slash commands updated"));
    }

    private void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage()) {
            return;
        }

        final String content = event.getMessage().getContentRaw().toLowerCase().trim();

        if (!content.startsWith(Variables.PREFIX.toLowerCase())
            && !content.startsWith(Variables.OTHER_PREFIX.toLowerCase())) {
            return;
        }

        if (content.equalsIgnoreCase(Variables.PREFIX + "shutdown") &&
            event.getAuthor().getIdLong() == Variables.OWNER_ID) {
            UptimeCommand.writeUptimeToFile();

            logger.info("Shutting down!!");
            service.shutdown();
            this.commandManager.getCommandService().shutdown();
            this.commandManager.getCommands().forEach(Command::shutdown);

            final ShardManager shardManager = Objects.requireNonNull(event.getJDA().getShardManager());

            this.audio.getMusicManagers().forEachEntry((gid, mngr) -> {
                try {
                    if (mngr.getPlayer().getPlayingTrack() != null) {
                        mngr.getPlayer().stopTrack();
                    }

                    final Guild guild = shardManager.getGuildById(gid);

                    if (guild == null) {
                        return true;
                    }

                    if (LavalinkManager.ins.isConnected(guild)) {
                        LavalinkManager.ins.closeConnection(guild);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            });

            new Thread(() -> {
                BotCommons.shutdown(shardManager);

                // TODO: make threads deamon
                System.exit(0);
            }).start();

            return;
        }

        this.commandManager.handleCommand(event, container);
    }

    private void onGuildJoin(@Nonnull GuildJoinEvent event) {
        logger.info("Joining guild: {}", event.getGuild());
    }

    private void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        logger.info("Leaving guild: {}", event.getGuild());
    }

    private void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        if (LavalinkManager.ins.isConnected(event.getGuild()) &&
            !event.getMember().equals(event.getGuild().getSelfMember())) {
            final VoiceChannel vc = LavalinkManager.ins.getConnectedChannel(event.getGuild());

            if (vc != null) {
                if (!event.getChannelLeft().equals(vc)) {
                    return;
                }

                channelCheckThing(event.getGuild(), event.getChannelLeft(), this.audio);
            }
        }
    }

    private void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        try {
            if (!LavalinkManager.ins.isConnected(event.getGuild())) {
                return;
            }

            final VoiceChannel connected = LavalinkManager.ins.getConnectedChannel(event.getGuild());

            if (event.getChannelJoined().equals(connected) &&
                !event.getMember().equals(event.getGuild().getSelfMember())) {
                return;
            } else {
                channelCheckThing(event.getGuild(), connected, this.audio);
            }

            channelCheckThing(event.getGuild(), event.getChannelLeft(), this.audio);

        } catch (NullPointerException ignored) {
        }
    }

    private void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        this.commandManager.reactListReg.handle(event);
    }

    private void onSlashCommand(@Nonnull SlashCommandEvent event) {
        this.commandManager.handleSlashCommand(event, this.container);
    }

    private void postServerCount() {
        if (this.config.shouldPostStats) {
            service.scheduleWithFixedDelay(() -> {
                final ShardManager manager = GhostBot.getInstance().getShardManager();
                final String ghostBot = "397297702150602752";

                final JSONObject theJson = new JSONObject(this.config.botLists)
                    .put("server_count", manager.getGuildCache().size())
                    .put("shard_count", manager.getShardsTotal())
                    .put("bot_id", ghostBot);

                final String dblKey = (String) theJson.remove("discordbots.org");

                final String jsonString = theJson.toString();

                WebUtils.ins.prepareRaw(
                    WebUtils.defaultRequest()
                        .url("https://botblock.org/api/count")
                        .post(RequestBody.create(null, jsonString.getBytes()))
                        .addHeader("Content-Type", JSON.getType())
                        .build(),
                    (it) -> Objects.requireNonNull(it.body()).string())
                    .async(
                        (it) -> logger.info("Posted stats to botblock api ({})", it)
                        ,
                        (it) -> {
                            logger.info("something borked");
                            logger.info(it.getMessage());
                        }
                    );

                WebUtils.ins.prepareRaw(
                    WebUtils.defaultRequest()
                        .url("https://top.gg/api/bots/" + ghostBot + "/stats")
                        .post(RequestBody.create(null, new JSONObject()
                            .put("server_count", manager.getGuildCache().size())
                            .put("shard_count", manager.getShardsTotal())
                            .toString()
                            .getBytes()
                        ))
                        .addHeader("Content-Type", JSON.getType())
                        .addHeader("Authorization", dblKey)
                        .build(),
                    (it) -> Objects.requireNonNull(it.body()).string())
                    .async(
                        (it) -> logger.info("Posted stats to discordbots.org ({})", it)
                        ,
                        (it) -> {
                            logger.info("Posting to dbl borked");
                            logger.info(it.getMessage());
                        }
                    );
            }, 0L, 1L, TimeUnit.DAYS);
        }

    }

    private static void channelCheckThing(Guild g, VoiceChannel vc, AudioUtils audio) {
        if (vc.getMembers().stream().filter((it) -> !it.getUser().isBot()).count() < 1) {
            final GuildMusicManager manager = audio.getMusicManager(g);
            final IPlayer player = manager.getPlayer();

            if (player.getPlayingTrack() != null) {
                player.stopTrack();
            }

            if (LavalinkManager.ins.isConnected(g)) {
                LavalinkManager.ins.closeConnection(g);
                g.getAudioManager().setSendingHandler(null);
            }
        }
    }

}
