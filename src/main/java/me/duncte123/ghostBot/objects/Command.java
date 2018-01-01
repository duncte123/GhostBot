package me.duncte123.ghostBot.objects;

import me.duncte123.ghostBot.audio.GuildMusicManager;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

public interface Command {
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event);
    String getName();
    default Show getShow() {
        return Show.NONE;
    }
    default String[] getAliases() {
        return new String[0];
    }
    String getHelp();

    default void sendSuccess(Message message) {
        if (message.getChannelType() == ChannelType.TEXT) {
            TextChannel channel = message.getTextChannel();
            if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
                return;
            }
        }
        message.addReaction("✅").queue();
    }

    default void sendEmbed(GuildMessageReceivedEvent event, MessageEmbed embed) {
        sendEmbed(event.getChannel(), embed);
    }

    default void sendEmbed(TextChannel channel, MessageEmbed embed) {
        if (!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
            sendMsg(channel, EmbedUtils.embedToMessage(embed));
            return;
        }
        sendMsg(channel, embed);
    }

    default void sendMsg(GuildMessageReceivedEvent event, String msg) {
        sendMsg(event.getChannel(), (new MessageBuilder()).append(msg).build());
    }

    default void sendMsg(TextChannel channel, String msg) {
        sendMsg(channel, (new MessageBuilder()).append(msg).build());
    }

    default void sendMsg(GuildMessageReceivedEvent event, MessageEmbed msg) {
        sendMsg(event.getChannel(), (new MessageBuilder()).setEmbed(msg).build());
    }

    default void sendMsg(TextChannel channel, MessageEmbed msg) {
        sendMsg(channel, (new MessageBuilder()).setEmbed(msg).build());
    }

    default void sendMsg(GuildMessageReceivedEvent event, Message msg) {
        sendMsg(event.getChannel(), msg);
    }

    default void sendMsg(TextChannel channel, Message msg) {
        //Only send a message if we can talk
        if(channel.canTalk())
            channel.sendMessage(msg).queue();
    }

    default GuildMusicManager getMusicManager(Guild guild) {
        return SpoopyUtils.audio.getMusicManager(guild);
    }

    default boolean preAudioChecks(GuildMessageReceivedEvent event) {
        if(!event.getMember().getVoiceState().inVoiceChannel()) {
            sendEmbed(event, EmbedUtils.embedMessage("Please join a voice channel first"));
            return false;
        }

        try {
            event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        }
        catch (PermissionException e) {
            if (e.getPermission() == Permission.VOICE_CONNECT) {
                sendMsg(event, EmbedUtils.embedMessage(String.format("I don't have permission to join `%s`", event.getMember().getVoiceState().getChannel().getName())));
            } else {
                sendMsg(event, EmbedUtils.embedMessage(String.format("Error while joining channel `%s`: %s"
                        , event.getMember().getVoiceState().getChannel().getName(), e.getMessage())));
            }
            return false;
        }
        return true;
    }
}
