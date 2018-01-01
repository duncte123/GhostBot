package me.duncte123.ghostBot;

import me.duncte123.ghostBot.audio.GuildMusicManager;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(BotListener.class);

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Logged in as " + String.format("%#s", event.getJDA().getSelfUser()));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if(!event.getMessage().getContentRaw().startsWith(Variables.PREFIX)) return;

        if(event.getMessage().getContentRaw().equals(Variables.PREFIX + "shutdown") && event.getAuthor().getId().equals(Variables.OWNER_ID)) {
            logger.info("Shutting down!!");
            event.getMessage().addReaction("✅").queue(
                    //Shutdown on both success and failure
                    success -> event.getJDA().shutdown(),
                    failure -> event.getJDA().shutdown()
            );
            System.exit(0);
            return;
        }

        SpoopyUtils.commandManager.handleCommand(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        //if 70 of a guild is bots, we'll leave it
        double[] botToUserRatio = SpoopyUtils.getBotRatio(event.getGuild());
        if (botToUserRatio[1] > 70) {
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
        if(event.getGuild().getAudioManager().isConnected()) {
            if (!event.getVoiceState().getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
                if (!event.getChannelLeft().getId().equals(event.getGuild().getAudioManager().getConnectedChannel().getId())) {
                    return;
                }
                channelCheckThing(event.getGuild(), event.getChannelLeft());
            }
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if(event.getGuild().getAudioManager().isConnected()) {
            if (!event.getVoiceState().getMember().getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
                if (event.getChannelLeft() != null) {
                    if (!event.getChannelLeft().getId().equals(event.getGuild().getAudioManager().getConnectedChannel().getId())) {
                        return;
                    }
                    channelCheckThing(event.getGuild(), event.getChannelLeft());

                }

                if (event.getChannelJoined() != null) {
                    if (event.getGuild().getAudioManager().getConnectedChannel() != null &&
                            !event.getChannelJoined().getId().equals(event.getGuild().getAudioManager().getConnectedChannel().getId())) {
                        return;
                        //System.out.println("Self (this might be buggy)");
                    }
                    channelCheckThing(event.getGuild(), event.getChannelJoined());
                }
            }
        }
    }

    private void channelCheckThing(Guild g, VoiceChannel vc) {

        if (vc.getMembers().stream().filter(m -> !m.getUser().isBot()).count() < 1) {
            GuildMusicManager manager = SpoopyUtils.audio.getMusicManager(g);
            manager.player.stopTrack();
            manager.player.setPaused(false);
            manager.scheduler.queue.clear();

            if (g.getAudioManager().isConnected()) {
                g.getAudioManager().closeAudioConnection();
                g.getAudioManager().setSendingHandler(null);
            }
        }
    }
}
