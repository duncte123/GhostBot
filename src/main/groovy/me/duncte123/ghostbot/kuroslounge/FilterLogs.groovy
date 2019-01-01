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

package me.duncte123.ghostbot.kuroslounge

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FilterLogs extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FilterLogs.class)

    private static final long logsBotspam = 377529193220800522L
    private static final long loggerBot = 327424261180620801L

    @Override
    void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.channel.idLong != logsBotspam || event.author.idLong != loggerBot) return

        logger.debug(event.author.toString())

        def embeds = event.message.embeds
        def shouldDelete = false

        embeds.forEach { embed ->
            def fields = embed.fields

            fields.forEach { field ->
                def matcher = Message.INVITE_PATTERN.matcher(field.value)
                logger.debug(field.value)

                if (matcher.find()) {
                    logger.debug('deleting')
                    shouldDelete = true
                }
            }

        }

        if (shouldDelete) {
            logger.debug('deleting for real')
            event.message.delete().reason('Log contains discord invite').queue()
        }
    }

}
