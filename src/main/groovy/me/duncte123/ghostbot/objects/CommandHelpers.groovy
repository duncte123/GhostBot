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

import me.duncte123.ghostbot.objects.entities.GhostBotMessage
import me.duncte123.ghostbot.objects.entities.GhostBotMessageEvent
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

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
                    success.accept(new GhostBotMessage<Message>() {
                        @Override
                        Message get() {
                            return it
                        }
                    })
                }
            }

        }

    }

    static void editMessage(GhostBotMessage gmessage, String newContent, boolean isSlack) {
        editMessage(gmessage, newContent, isSlack, null)
    }

    static void editMessage(GhostBotMessage gmessage, GString newContent, boolean isSlack) {
        editMessage(gmessage, newContent.toString(), isSlack, null)
    }

    static void editMessage(GhostBotMessage gmessage, String newContent, boolean isSlack, Consumer<GhostBotMessage> success) {

        def message = !isSlack ? gmessage.get() as Message : null

        if (!isSlack) {

            message.editMessage(newContent).queue {
                if (success != null) {
                    success.accept(new GhostBotMessage<Message>() {
                        @Override
                        Message get() {
                            return it
                        }
                    })
                }
            }
        }

        // TODO edit slack message
    }

}
