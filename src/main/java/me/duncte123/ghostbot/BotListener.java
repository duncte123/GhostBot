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
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.audio.GuildMusicManager;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import okhttp3.RequestBody;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.duncte123.botcommons.web.WebUtils.EncodingType.APPLICATION_JSON;

public class BotListener extends ListenerAdapter {

    public static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger(BotListener.class);
    private final TLongList botLists = new TLongArrayList(new long[]{
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
    });

    @Override
    public void onReady(ReadyEvent event) {
        final JDA jda = event.getJDA();

        logger.info("Logged in as {} ({})", jda.getSelfUser(), jda.getShardInfo());
        postServerCount();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) {
            return;
        }

        final String content = event.getMessage().getContentRaw().toLowerCase();

        if (!content.startsWith(Variables.PREFIX.toLowerCase())
            && !content.startsWith(Variables.OTHER_PREFIX.toLowerCase())) {
            return;
        }

        if (content.equalsIgnoreCase(Variables.PREFIX + "shutdown") &&
            event.getAuthor().getIdLong() == Variables.OWNER_ID) {
            logger.info("Shutting down!!");
            service.shutdownNow();
            SpoopyUtils.getCommandManager().getCommandService().shutdown();
            event.getJDA().asBot().getShardManager().shutdown();
            System.exit(0);

            return;
        }

        SpoopyUtils.getCommandManager().handleCommand(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //if 70 of a guild is bots, we'll leave it
        final double[] botToUserRatio = SpoopyUtils.getBotRatio(event.getGuild());

        if (botToUserRatio[1] > 80 && !botLists.contains(event.getGuild().getIdLong())) {
            SpoopyUtils.getPublicChannel(event.getGuild()).sendMessage(
                String.format(
                    "Hey %s, %s%s of this server are bots (%s is the total btw). I'm outta here.",
                    event.getGuild().getOwner().getAsMention(),
                    botToUserRatio[1],
                    '%',
                    event.getGuild().getMemberCache().size()
                )).queue(
                (message) -> message.getGuild().leave().queue()
            );

            logger.info("Joining guild: {}, and leaving it after. BOT ALERT", event.getGuild());
            return;
        }

        logger.info("Joining guild: {}", event.getGuild());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        logger.info("Leaving guild: {}", event.getGuild());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (LavalinkManager.ins.isConnected(event.getGuild()) &&
            !event.getVoiceState().getMember().equals(event.getGuild().getSelfMember())) {
            final VoiceChannel vc = LavalinkManager.ins.getConnectedChannel(event.getGuild());

            if (vc != null) {
                if (event.getChannelLeft().equals(vc)) {
                    return;
                }

                channelCheckThing(event.getGuild(), event.getChannelLeft());
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            if (!LavalinkManager.ins.isConnected(event.getGuild())) {
                return;
            }

            final VoiceChannel connected = LavalinkManager.ins.getConnectedChannel(event.getGuild());

            if (event.getChannelJoined().equals(connected) &&
                !event.getMember().equals(event.getGuild().getSelfMember())) {
                return;
            } else {
                channelCheckThing(event.getGuild(), connected);
            }

            if (event.getChannelLeft().equals(connected)) {
                channelCheckThing(event.getGuild(), event.getChannelLeft());
                //return;
            }

        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        SpoopyUtils.getCommandManager().reactListReg.handle(event);
    }

    private void postServerCount() {
        if (SpoopyUtils.getConfig().shouldPostStats) {
            service.scheduleWithFixedDelay(() -> {
                final ShardManager manager = GhostBot.instance.getShardManager();

                final String jsonString = new JSONObject(SpoopyUtils.getConfig().botLists.toString())
                    .put("server_count", manager.getGuildCache().size())
                    .put("shard_count", manager.getShardsTotal())
                    .put("bot_id", 397297702150602752L)
                    .toString();

                WebUtils.ins.prepareRaw(
                    WebUtils.defaultRequest()
                        .url("https://botblock.org/api/count")
                        .post(RequestBody.create(null, jsonString))
                        .addHeader("Content-Type", APPLICATION_JSON.getType())
                        .build(),
                    (it) -> it.body().string())
                    .async(

                        (it) -> logger.info("Posted stats to botblock api (${})", it)
                        ,
                        (it) -> {
                            logger.info("something borked");
                            logger.info(it.getMessage());
                        }
                    );
            }, 0L, 1L, TimeUnit.DAYS);
        }

    }

    private static void channelCheckThing(Guild g, VoiceChannel vc) {
        if (vc.getMembers().stream().filter((it) -> !it.getUser().isBot()).count() < 1) {
            final GuildMusicManager manager = SpoopyUtils.getAudio().getMusicManager(g);

            manager.getPlayer().stopTrack();
            manager.getPlayer().setPaused(false);
            manager.getScheduler().getQueue().clear();

            if (LavalinkManager.ins.isConnected(g)) {
                LavalinkManager.ins.closeConnection(g);
                g.getAudioManager().setSendingHandler(null);
            }
        }
    }

}
