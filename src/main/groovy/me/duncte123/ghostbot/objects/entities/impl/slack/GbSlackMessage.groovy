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

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.SlackUser
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import me.duncte123.ghostbot.objects.entities.GhostBotMessage

class GbSlackMessage implements GhostBotMessage {

    private SlackMessagePosted event
    SlackSession session

    String getMessageContent() {
        event.messageContent
    }

    SlackUser getAuthor() {
        event.user
    }

    SlackChannel getChannel() {
        event.channel
    }

    String getTimeStamp() {
        event.timeStamp
    }

    @Override
    Object get() {
        return null
    }
}
