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
        attachment.fallback = 'You should get out of compact mode ;)'

        if (embed.title != null) {
            attachment.title = embed.title
        }

        if (embed.description != null) {
            attachment.text = embed.description
        }

        if (embed.footer != null) {
            def footer = embed.footer
            attachment.footer = footer.text
            attachment.footerIcon = footer.iconUrl
        }


        if (embed.timestamp != null) {
            attachment.timestamp = embed.timestamp.toEpochSecond()
        }


        return attachment
    }

}
