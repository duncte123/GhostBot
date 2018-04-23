/*
 * GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018  Duncan "duncte123" Sterken
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

package me.duncte123.ghostBot;

import fredboat.audio.player.LavalinkManager;
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.audio.GuildMusicManager;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(BotListener.class);
    private final String dbotsToken = SpoopyUtils.config.getString("api.dbots", "");
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Logged in as " + String.format("%#s", event.getJDA().getSelfUser()));
        postServerCount(event.getJDA());
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if (!event.getMessage().getContentRaw().startsWith(Variables.PREFIX)) return;

        if (event.getMessage().getContentRaw().equals(Variables.PREFIX + "shutdown") && event.getAuthor().getId().equals(Variables.OWNER_ID)) {
            logger.info("Shutting down!!");
            service.shutdown();
            event.getMessage().addReaction("✅").queue(
                    //Shutdown on both success and failure
                    success -> event.getJDA().shutdown(),
                    failure -> event.getJDA().shutdown()
            );
            try {
                //noinspection PointlessArithmeticExpression
                Thread.sleep(1 * 1000);
                System.exit(0);
            } catch (InterruptedException ignored) {
            }
            return;
        }

        SpoopyUtils.commandManager.handleCommand(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //if 70 of a guild is bots, we'll leave it
        double[] botToUserRatio = SpoopyUtils.getBotRatio(event.getGuild());
        if (botToUserRatio[1] > 80) {
            SpoopyUtils.getPublicChannel(event.getGuild()).sendMessage(String.format("Hey %s, %s%s of this server are bots (%s is the total btw). I'm outta here.",
                    event.getGuild().getOwner().getAsMention(),
                    botToUserRatio[1],
                    "%",
                    event.getGuild().getMemberCache().size())).queue(
                    message -> message.getGuild().leave().queue()
            );
            logger.info("Joining guild: " + event.getGuild().getName() + ", and leaving it after. BOT ALERT");
            return;
        }
        logger.info("Joining guild: " + event.getGuild().toString());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        logger.info("Leaving guild: " + event.getGuild().toString());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (LavalinkManager.ins.isConnected(event.getGuild())
                && !event.getVoiceState().getMember().equals(event.getGuild().getSelfMember())) {
            VoiceChannel vc = LavalinkManager.ins.getConnectedChannel(event.getGuild());
            if (vc != null) {
                if (!event.getChannelLeft().equals(vc)) {
                    return;
                }
                channelCheckThing(event.getGuild(), event.getChannelLeft());
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            if (LavalinkManager.ins.isConnected(event.getGuild())) {
                if (event.getChannelJoined().equals(LavalinkManager.ins.getConnectedChannel(event.getGuild()))
                        && !event.getMember().equals(event.getGuild().getSelfMember())) {
                    return;
                } else {
                    channelCheckThing(event.getGuild(), LavalinkManager.ins.getConnectedChannel(event.getGuild()));
                }
                if (event.getChannelLeft().equals(LavalinkManager.ins.getConnectedChannel(event.getGuild()))) {
                    channelCheckThing(event.getGuild(), event.getChannelLeft());
                    //return;
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    private void postServerCount(JDA jda) {
        service.scheduleWithFixedDelay(() ->
                        WebUtils.ins.prepareRaw(
                                new Request.Builder()
                                        .url("https://bots.discord.pw/api/bots/397297702150602752/stats")
                                        .post(RequestBody.create(MediaType.parse("application/json"),
                                                new JSONObject().put("server_count", jda.getGuilds().size()).toString()))
                                        .addHeader("User-Agent", "DiscordBot " + jda.getSelfUser().getName())
                                        .addHeader("Authorization", dbotsToken)
                                        .build(), r -> r.body().string()).async(
                                empty -> logger.info("Posted stats to dbots (" + empty + ")"),
                                nothing -> {
                                    logger.info("something borked");
                                    logger.info(nothing.getMessage());
                                })
                , 0L, 1L, TimeUnit.DAYS);

    }

    private void channelCheckThing(Guild g, VoiceChannel vc) {
        if (vc.getMembers().stream().filter(m -> !m.getUser().isBot()).count() < 1) {
            GuildMusicManager manager = SpoopyUtils.audio.getMusicManager(g);
            manager.player.stopTrack();
            manager.player.setPaused(false);
            manager.scheduler.queue.clear();

            if (LavalinkManager.ins.isConnected(g)) {
                LavalinkManager.ins.closeConnection(g);
                g.getAudioManager().setSendingHandler(null);
            }
        }
    }
}
