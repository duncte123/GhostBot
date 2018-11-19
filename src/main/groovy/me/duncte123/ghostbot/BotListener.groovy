/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot

import fredboat.audio.player.LavalinkManager
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.audio.GuildMusicManager
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.variables.Variables
import me.duncte123.ghostbotslack.GhostBotSlack
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import static me.duncte123.botcommons.web.WebUtils.EncodingType.APPLICATION_JSON

class BotListener extends ListenerAdapter {
    public static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor()
    private final Logger logger = LoggerFactory.getLogger(BotListener.class)

    private final GhostBotSlack slack

    BotListener(GhostBotSlack slack) {
        this.slack = slack
    }

    @Override
    void onReady(ReadyEvent event) {
        def jda = event.JDA
        logger.info("Logged in as $jda.selfUser ($jda.shardInfo)")
        postServerCount(jda.asBot().shardManager)
    }

    @Override
    void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.author.bot || event.author.fake) return

        def content = event.message.contentRaw.toLowerCase()

        if (!content.startsWith(Variables.PREFIX.toLowerCase())
            && !content.startsWith(Variables.OTHER_PREFIX.toLowerCase())) return

        if (event.message.contentRaw == "${Variables.PREFIX}shutdown" && event.author.idLong == Variables.OWNER_ID) {
            logger.info('Shutting down!!')
            service.shutdownNow()
            SpoopyUtils.commandManager.commandService.shutdown()
            event.JDA.shutdown()

            slack.sessions.forEach {
                it.disconnect()
            }

            if (LavalinkManager.ins.lavalink != null) {
                LavalinkManager.ins.lavalink.shutdown()
            }

            System.exit(0)

            return
        }

        SpoopyUtils.commandManager.handleCommand(event)
    }

    @Override
    void onGuildJoin(GuildJoinEvent event) {
        //if 70 of a guild is bots, we'll leave it
        double[] botToUserRatio = SpoopyUtils.getBotRatio(event.guild)
        if (botToUserRatio[1] > 80) {
            SpoopyUtils.getPublicChannel(event.guild).sendMessage(String.format('Hey %s, %s%s of this server are bots (%s is the total btw). I\'m outta here.',
                event.guild.owner.asMention,
                botToUserRatio[1],
                '%',
                event.guild.memberCache.size())).queue {
                message -> message.guild.leave().queue()
            }
            logger.info("Joining guild: $event.guild.name, and leaving it after. BOT ALERT")
            return
        }
        logger.info("Joining guild: $event.guild")
    }

    @Override
    void onGuildLeave(GuildLeaveEvent event) {
        logger.info("Leaving guild: $event.guild")
    }

    @Override
    void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (LavalinkManager.ins.isConnected(event.getGuild())
            && event.voiceState.member != event.guild.selfMember) {
            VoiceChannel vc = LavalinkManager.ins.getConnectedChannel(event.guild)

            if (vc != null) {
                if (event.channelLeft != vc) {
                    return
                }

                channelCheckThing(event.guild, event.channelLeft)
            }
        }
    }

    @Override
    void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            if (!LavalinkManager.ins.isConnected(event.guild)) {
                return
            }

            if (event.channelJoined == LavalinkManager.ins.getConnectedChannel(event.guild)
                && event.member != event.guild.selfMember) {
                return
            } else {
                channelCheckThing(event.guild, LavalinkManager.ins.getConnectedChannel(event.guild))
            }
            if (event.channelLeft == LavalinkManager.ins.getConnectedChannel(event.guild)) {
                channelCheckThing(event.guild, event.channelLeft)
                //return;
            }

        } catch (NullPointerException ignored) {
        }
    }

    @Override
    void onMessageReactionAdd(MessageReactionAddEvent event) {
        SpoopyUtils.commandManager.reactListReg.handle(event)
    }

    private void postServerCount(ShardManager manager) {
        if (SpoopyUtils.config.shouldPostStats) {
            service.scheduleWithFixedDelay({

                def jsonString = new JSONObject(SpoopyUtils.config.botLists.toString())
                    .put('server_count', manager.guildCache.size())
                    .put('shard_count', manager.shardsTotal)
                    .put('bot_id', '397297702150602752')
                    .toString()

                WebUtils.ins.prepareRaw(
                    WebUtils.defaultRequest()
                        .url('https://botblock.org/api/count')
                        .post(RequestBody.create(APPLICATION_JSON.toMediaType(), jsonString))
                        .build(),
                    {
                        new JSONObject(new JSONTokener(it.body().byteStream()))
                    }
                )
                    .async(
                    {
                        logger.info("Posted stats to botblock api ($it)")
                    },
                    {
                        logger.info("something borked")
                        logger.info(it.message)
                    }
                )
            }, 0L, 1L, TimeUnit.DAYS)
        }

    }

    private static void channelCheckThing(Guild g, VoiceChannel vc) {
        if (vc.members.stream().filter { !it.user.bot }.count() < 1) {
            GuildMusicManager manager = SpoopyUtils.audio.getMusicManager(g)
            manager.player.stopTrack()
            manager.player.setPaused(false)
            manager.scheduler.queue.clear()

            if (LavalinkManager.ins.isConnected(g)) {
                LavalinkManager.ins.closeConnection(g)
                g.getAudioManager().setSendingHandler(null)
            }
        }
    }
}
