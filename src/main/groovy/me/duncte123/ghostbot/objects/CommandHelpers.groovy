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

package me.duncte123.ghostbot.objects

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackSession
import me.duncte123.ghostbot.objects.entities.GhostBotMessage
import me.duncte123.ghostbot.objects.entities.GhostBotMessageEvent
import me.duncte123.ghostbot.objects.entities.impl.jda.JDAMessage
import me.duncte123.ghostbot.objects.entities.impl.slack.GbSlackMessageReply
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import java.util.concurrent.TimeUnit
import java.util.function.Consumer

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class CommandHelpers {

    static void sendMessage(GhostBotMessageEvent event, GString content) {
        sendMessage(event, content.toString(), null)
    }

    static void sendMessage(GhostBotMessageEvent event, String content) {
        sendMessage(event, content, null)
    }

    static void sendMessage(GhostBotMessageEvent event, String content, Consumer<GhostBotMessage> success) {
        if (!event.fromSlack) {
            def jdaEvent = event.originalEvent as GuildMessageReceivedEvent

            sendMsg(jdaEvent, content) {
                if (success != null) {
                    success.accept(new JDAMessage(message: it))
                }
            }

            return
        }

        def session = event.API.get() as SlackSession
        def channel = event.channel.get() as SlackChannel

        def request = session.sendMessage(channel, content)

        request.waitForReply(30, TimeUnit.SECONDS)

        def result = request.reply

        if (result == null) {
            return
        }

        if (!result.ok) {
            return
        }

        if (success != null) {
            success.accept(new GbSlackMessageReply(reply: result, channel: channel, session: session))
        }
    }

    static void editMessage(GhostBotMessage gmessage, String newContent, boolean isSlack) {
        editMessage(gmessage, newContent, isSlack, null)
    }

    static void editMessage(GhostBotMessage gmessage, GString newContent, boolean isSlack) {
        editMessage(gmessage, newContent.toString(), isSlack, null)
    }

    static void editMessage(GhostBotMessage gmessage, String newContent, boolean isSlack, Consumer<GhostBotMessage> success) {

        if (!isSlack) {

            def message = gmessage.get() as Message

            message.editMessage(newContent).queue {
                if (success != null) {
                    success.accept(new JDAMessage(message: it))
                }
            }

            return
        }

        def message = gmessage as GbSlackMessageReply
        def session = message.session
        def channel = message.channel
        def timeStamp = message.get().timestamp

        def result = session.updateMessage(timeStamp, channel, newContent).reply

        if (!result.ok) {
            return
        }

        if (success != null) {
            success.accept(new GbSlackMessageReply(reply: result, channel: channel, session: session))
        }
    }

}
