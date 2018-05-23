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

package me.duncte123.ghostBot.utils;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class EmbedUtils {

    public static final String FOOTER_ICON = "https://cdn.discordapp.com/emojis/394148311835344896.png";

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
                .setFooter("GhostBot", FOOTER_ICON)
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
