package me.duncte123.ghostBot.objects;

import me.duncte123.ghostBot.utils.EmbedUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event);
    String getName();
    default String[] getAliases() {
        return new String[0];
    }
    String getHelp();

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
}
