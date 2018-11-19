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

package me.duncte123.ghostbot.objects.entities.impl.slack

import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import me.duncte123.ghostbot.objects.entities.GhostBotApi
import me.duncte123.ghostbot.objects.entities.GhostBotChannel
import me.duncte123.ghostbot.objects.entities.GhostBotGuild
import me.duncte123.ghostbot.objects.entities.GhostBotMessage
import me.duncte123.ghostbot.objects.entities.GhostBotMessageEvent
import me.duncte123.ghostbot.objects.entities.GhostBotUser

class SlackMessageEvent implements GhostBotMessageEvent<SlackMessagePosted> {

    private final SlackMessagePosted event
    private final GhostBotApi api
    private final GhostBotChannel channel
    private final GhostBotGuild guild
    private final GhostBotUser user
    private final GhostBotMessage message

    SlackMessageEvent(SlackMessagePosted event, SlackSession session) {
        this.event = event
        this.api = new SlackApi(session: session)
        this.channel = new GbSlackChannel(channel: event.channel)
        this.guild = new SlackGuild(channel: event.channel)
        this.user = new GbSlackUser(user: event.user)
        this.message = new GbSlackMessage(event: event)
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
    GhostBotGuild getGuild() {
        return guild
    }

    @Override
    SlackMessagePosted getOriginalEvent() {
        return event
    }

    @Override
    boolean isFromSlack() {
        return true
    }
}
