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

package me.duncte123.ghostBot.utils

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed

import java.awt.*
import java.time.Instant

class EmbedUtils {

    static EmbedBuilder defaultEmbed() {
        return new EmbedBuilder()
            .setColor(Color.decode("#6ffe32"))
            .setFooter("GhostBot", "https://cdn.discordapp.com/emojis/394148311835344896.png")
            .setTimestamp(Instant.now())
    }

    static MessageEmbed embedField(String title, String message) {
        return defaultEmbed().addField(title, message, false).build()
    }

    static MessageEmbed embedMessage(String message) {
        return defaultEmbed().setDescription(message).build()
    }

    static MessageEmbed embedImage(String imageURL) {
        return defaultEmbed().setImage(imageURL).build()
    }

    static Message embedToMessage(MessageEmbed embed) {
        def msg = new MessageBuilder()

        if (embed.author != null) {
            msg.append("***").append(embed.author.name).append("***\n\n")
        }
        if (embed.description != null) {
            msg.append("_").append(embed.description).append("_\n\n")
        }
        embed.fields.forEach {
            msg.append("__").append(it.name).append("__\n").append(it.value).append("\n\n")
        }
        if (embed.image != null) {
            msg.append(embed.image.url).append("\n")
        }
        if (embed.footer != null) {
            msg.append(embed.footer.text)
        }
        if (embed.timestamp != null) {
            msg.append(" | ").append(embed.timestamp)
        }

        return msg.build()
    }
}
