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

package me.duncte123.ghostBot.kuroslounge;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageEmbed.Field;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterLogs extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(FilterLogs.class);

    private static final long logsBotspam = 377529193220800522L;
    private static final long loggerBot = 327424261180620801L;
    private static final Pattern DISCORD_INVITE_PATTERN = Pattern.compile("discord(?:app\\.com/invite|\\.gg)/([\\S\\w]*\\b)");

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != logsBotspam || event.getAuthor().getIdLong() != loggerBot) return;

        logger.debug(event.getAuthor().toString());

        List<MessageEmbed> embeds = event.getMessage().getEmbeds();
        boolean[] shouldDelete = {false};

        embeds.forEach((embed) -> {
            List<Field> fields = embed.getFields();

            fields.forEach((field) -> {

                Matcher matcher = DISCORD_INVITE_PATTERN.matcher(field.getValue());

                logger.debug(field.getValue());
                if (matcher.find()) {
                    logger.debug("deleting");
                    shouldDelete[0] = true;
                }

            });

        });

        if (shouldDelete[0]) {
            logger.debug("deleting for real");
            event.getMessage().delete().reason("Log contains discord invite").queue();
        }
    }
}
