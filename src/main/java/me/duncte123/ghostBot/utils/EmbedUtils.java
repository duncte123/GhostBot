package me.duncte123.ghostBot.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class EmbedUtils {


    public static MessageEmbed embedField(String title, String message) {
        return defaultEmbed().addField(title, message, false).build();
    }

    public static MessageEmbed embedMessage(String message) {
        return defaultEmbed().setDescription(message).build();
    }

    public static MessageEmbed embedImage(String imageURL) {
        return defaultEmbed().setImage(imageURL).build();
    }

    public static EmbedBuilder defaultEmbed() {
        return new EmbedBuilder()
                .setColor(Color.decode("#6ffe32"))
                .setFooter("GhostBot", "https://cdn.discordapp.com/emojis/394148311835344896.png")
                .setTimestamp(Instant.now());
    }

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
