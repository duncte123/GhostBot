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

package me.duncte123.ghostbot.objects.entities.impl.jda

import me.duncte123.ghostbot.objects.entities.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class JDAMessageEvent implements GhostBotMessageEvent<GuildMessageReceivedEvent> {

    private final GuildMessageReceivedEvent event
    private final GhostBotApi api
    private final GhostBotChannel channel
    private final GhostBotUser user
    private final GhostBotMessage message
    private final GhostBotGuild guild

    JDAMessageEvent(GuildMessageReceivedEvent event) {
        this.event = event
        this.api = new JDAApi(jda: event.JDA)
        this.channel = new JDAChannel(channel: event.channel)
        this.user = new JDAUser(user: event.author)
        this.message = new JDAMessage(message: event.message)
        this.guild = new JDAGuild(guild: event.guild)
    }

    @Override
    GhostBotMessage getMessage() {
        return message
    }

    @Override
    GhostBotUser getAuthor() {
        return user
    }

    @Override
    GhostBotChannel getChannel() {
        return channel
    }

    @Override
    GhostBotApi getAPI() {
        return api
    }

    @Override
    GuildMessageReceivedEvent getOriginalEvent() {
        return event
    }

    @Override
    GhostBotGuild getGuild() {
        return guild
    }

    @Override
    boolean isFromSlack() {
        return false
    }
}
