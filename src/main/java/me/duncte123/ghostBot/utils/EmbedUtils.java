package me.duncte123.ghostBot.utils;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class EmbedUtils {

    public static Message embedToMessage(MessageEmbed embed) {
        MessageBuilder msg = new MessageBuilder();

        if (embed.getAuthor() != null) {
            msg.append("***").append(embed.getAuthor().getName()).append("***\n\n");
        }
        if (embed.getDescription() != null) {
            msg.append("_").append(embed.getDescription()).append("_\n\n");
        }
        for (MessageEmbed.Field f : embed.getFields()) {
            msg.append("__").append(f.getName()).append("__\n").append(f.getValue()).append("\n\n");
        }
        if (embed.getImage() != null) {
            msg.append(embed.getImage().getUrl()).append("\n");
        }
        if (embed.getFooter() != null) {
            msg.append(embed.getFooter().getText());
        }
        if (embed.getTimestamp() != null) {
            msg.append(" | ").append(embed.getTimestamp());
        }

        return msg.build();
    }
}
