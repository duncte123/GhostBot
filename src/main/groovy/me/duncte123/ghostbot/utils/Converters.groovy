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

package me.duncte123.ghostbot.utils

import com.ullink.slack.simpleslackapi.SlackAttachment
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.entities.MessageEmbed

class Converters {

    static SlackAttachment embedToAttachment(MessageEmbed embed) {

        def attachment = new SlackAttachment()
        attachment.color = Variables.EMBED_COLOR_SLACK
        attachment.fallback = 'Embed with cool content ;)'

        if (embed.title != null) {
            attachment.title = embed.title
        }

        if (embed.url != null) {
            attachment.titleLink = embed.url
        }

        if (embed.description != null) {
            attachment.text = embed.description
        }

        if (embed.footer != null) {
            def footer = embed.footer
            attachment.footer = footer.text
            attachment.footerIcon = footer.iconUrl
        }

        if (!embed.fields.isEmpty()) {
            embed.fields.forEach {
                attachment.addField(it.name, it.value, it.inline)
            }
        }

        if (embed.image != null) {
            attachment.imageUrl = embed.image.url
        }

        if (embed.thumbnail != null) {
            attachment.thumbUrl = embed.thumbnail.url
        }

        if (embed.timestamp != null) {
            attachment.timestamp = embed.timestamp.toEpochSecond()
        }

        def author = embed.author

        if (author != null) {

            if (author.name != null) {
                attachment.authorName = author.name
            }

            if (author.iconUrl != null) {
                attachment.authorIcon = author.iconUrl
            }

            if (author.url != null) {
                attachment.authorLink = author.url
            }

        }


        return attachment
    }

}
