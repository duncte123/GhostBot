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

package me.duncte123.ghostBot

import fredboat.audio.player.LavalinkManager
import me.duncte123.botCommons.web.WebUtils
import me.duncte123.ghostBot.audio.GuildMusicManager
import me.duncte123.ghostBot.utils.SpoopyUtils
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.slf4j.LoggerFactory

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class BotListener extends ListenerAdapter {

    private final def logger = LoggerFactory.getLogger(BotListener.class)
    private final String dbotsToken = SpoopyUtils.CONFIG.getString("api.dbots", "")
    private final def service = Executors.newSingleThreadScheduledExecutor()

    @Override
    void onReady(ReadyEvent event) {
        logger.info("Logged in as ${String.format("%#s", event.JDA.selfUser)}")
        postServerCount(event.JDA)
    }

    @Override
    void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.author.bot || event.author.fake) return
        if (!event.message.contentRaw.toLowerCase()
                .startsWith(Variables.PREFIX.toLowerCase())) return

        if (event.message.contentRaw == "${Variables.PREFIX}shutdown" && event.author.id == Variables.OWNER_ID) {
            logger.info("Shutting down!!")
            service.shutdown()
            event.message.addReaction("✅").queue(
                    //Shutdown on both success and failure
                    {success -> event.getJDA().shutdown()},
                    {failure -> event.getJDA().shutdown()}
            )
            try {
                //noinspection PointlessArithmeticExpression
                Thread.sleep(1 * 1000)
                System.exit(0)
            } catch (InterruptedException ignored) {
            }
            return
        }

        SpoopyUtils.COMMAND_MANAGER.handleCommand(event)
    }

    @Override
    void onGuildJoin(GuildJoinEvent event) {
        //if 70 of a guild is bots, we'll leave it
        double[] botToUserRatio = SpoopyUtils.getBotRatio(event.guild)
        if (botToUserRatio[1] > 80) {
            SpoopyUtils.getPublicChannel(event.guild)
                    .sendMessageFormat("Hey %s, %s%s of this server are bots (%s is the total btw). I'm outta here.",
                    event.guild.owner.asMention,
                    botToUserRatio[1],
                    "%",
                    event.guild.memberCache.size()).queue(
                    {message -> message.guild.leave().queue()}
            )
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
        if (LavalinkManager.ins.isConnected(event.guild)
                && event.voiceState.member != event.guild.selfMember) {
            VoiceChannel vc = LavalinkManager.ins.getConnectedChannel(event.guild)
            if (vc != null) {
                if (event.getChannelLeft() != vc) {
                    return
                }
                channelCheckThing(event.guild, event.channelLeft)
            }
        }
    }

    @Override
    void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            if (LavalinkManager.ins.isConnected(event.guild)) {
                if (event.channelJoined == LavalinkManager.ins.getConnectedChannel(event.guild)
                        && event.member != event.guild.selfMember) {
                    return
                } else {
                    channelCheckThing(event.guild, LavalinkManager.ins.getConnectedChannel(event.guild))
                }
                if (event.channelLeft == LavalinkManager.ins.getConnectedChannel(event.guild)) {
                    channelCheckThing(event.guild, event.channelLeft)
                    //return
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    private void postServerCount(JDA jda) {
        if (!dbotsToken.isEmpty()) {
            service.scheduleWithFixedDelay({
                WebUtils.ins.prepareRaw(
                        new Request.Builder()
                                .url("https://bots.discord.pw/api/bots/397297702150602752/stats")
                                .post(RequestBody.create(MediaType.parse("application/json"),
                                new JSONObject().put("server_count", jda.guilds.size()).toString()))
                                .addHeader("User-Agent", "DiscordBot $jda.selfUser.name")
                                .addHeader("Authorization", dbotsToken)
                                .build(), {r -> r.body().string()}).async(
                        {empty -> logger.info("Posted stats to dbots ($empty)")},
                        {nothing ->
                    logger.info("something borked")
                    logger.info(nothing.message)
                })
            }, 0L, 1L, TimeUnit.DAYS)
        }

    }

    private static void channelCheckThing(Guild g, VoiceChannel vc) {
        if (vc.members.stream().filter{ m -> !m.user.bot }.count() < 1) {
            GuildMusicManager manager = SpoopyUtils.AUDIO.getMusicManager(g)
            manager.player.stopTrack()
            manager.player.paused = false
            manager.scheduler.queue.clear()

            if (LavalinkManager.ins.isConnected(g)) {
                LavalinkManager.ins.closeConnection(g)
                g.audioManager.sendingHandler = null
            }
        }
    }
}
