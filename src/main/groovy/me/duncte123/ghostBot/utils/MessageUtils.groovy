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

import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.ChannelType
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.ErrorResponseException
import org.slf4j.LoggerFactory

import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@SuppressWarnings("GroovyUnusedDeclaration")
class MessageUtils {

    private static def logger = LoggerFactory.getLogger(MessageUtils.class)

    private static final Consumer<Throwable> CUSTOM_QUEUE_ERROR = {
        if (it instanceof ErrorResponseException) {
            if (((ErrorResponseException) it).errorCode != 10008) {
                logger.error("RestAction queue returned failure", it)
                it.printStackTrace()
            }
        }
    }

    static void sendError(Message message) {
        if (message.channelType == ChannelType.TEXT) {
            TextChannel channel = message.textChannel
            if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
                return
            }
        }
        message.addReaction("❌").queue(null, CUSTOM_QUEUE_ERROR)
    }

    static void sendErrorWithMessage(Message message, String text) {
        sendError(message)
        sendMsg(message.textChannel, text)
    }

    static void sendSuccess(Message message) {
        if (message.channelType == ChannelType.TEXT) {
            TextChannel channel = message.textChannel
            if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_ADD_REACTION)) {
                return
            }
        }
        message.addReaction("✅").queue(null, CUSTOM_QUEUE_ERROR)
    }

    static void sendSuccessWithMessage(Message message, String text) {
        sendSuccess(message)
        sendMsg(message.textChannel, text)
    }

    static void sendEmbed(GuildMessageReceivedEvent event, MessageEmbed embed) {
        sendEmbed(event.channel, embed)
    }

    static void sendEmbed(TextChannel channel, MessageEmbed embed) {
        if (channel != null) {
            if (!channel.guild.selfMember.hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) {
                sendMsg(channel, EmbedUtils.embedToMessage(embed))
                return
            }
            sendMsg(channel, embed)
        }
    }

    static void sendMsgAndDeleteAfter(GuildMessageReceivedEvent event, long delay, TimeUnit unit, String msg) {
        sendMsgFormatAndDeleteAfter(event.channel, delay, unit, msg, "")
    }

    static void sendMsgAndDeleteAfter(TextChannel tc, long delay, TimeUnit unit, String msg) {
        sendMsgFormatAndDeleteAfter(tc, delay, unit, msg, "")
    }

    static void sendMsgFormatAndDeleteAfter(GuildMessageReceivedEvent event, long delay, TimeUnit unit, String msg, Object... args) {
        sendMsgFormatAndDeleteAfter(event.channel, delay, unit, msg, args)
    }

    static void sendMsgFormatAndDeleteAfter(TextChannel channel, long delay, TimeUnit unit, String msg, Object... args) {

        sendMsg(channel, new MessageBuilder().append(String.format(msg, args)).build()) {
            it.delete().reason("automatic remove").queueAfter(delay, unit, null, CUSTOM_QUEUE_ERROR)
        }
    }
    static void sendMsgFormat(GuildMessageReceivedEvent event, String msg, Object... args) {
        sendMsg(event.channel, (new MessageBuilder().appendFormat(msg, args).build()), null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsgFormat(TextChannel channel, String msg, Object... args) {
        sendMsg(channel, (new MessageBuilder().appendFormat(msg, args).build()), null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(GuildMessageReceivedEvent event, String msg) {
        sendMsg(event.channel, (new MessageBuilder()).append(msg).build(), null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(GuildMessageReceivedEvent event, String msg, Consumer<Message> success) {
        sendMsg(event.channel, (new MessageBuilder()).append(msg).build(), success, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(GuildMessageReceivedEvent event, String msg, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMsg(event.channel, (new MessageBuilder()).append(msg).build(), success, failure)
    }

    static void sendMsg(TextChannel channel, String msg) {
        sendMsg(channel, (new MessageBuilder()).append(msg).build(), null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(TextChannel channel, String msg, Consumer<Message> success) {
        sendMsg(channel, (new MessageBuilder()).append(msg).build(), success, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(TextChannel channel, String msg, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMsg(channel, (new MessageBuilder()).append(msg).build(), success, failure)
    }

    static void sendMsg(GuildMessageReceivedEvent event, MessageEmbed msg) {
        sendMsg(event.channel, (new MessageBuilder()).setEmbed(msg).build(), null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(TextChannel channel, MessageEmbed msg) {
        sendMsg(channel, (new MessageBuilder()).setEmbed(msg).build(), null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(GuildMessageReceivedEvent event, Message msg) {
        sendMsg(event.channel, msg, null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(GuildMessageReceivedEvent event, Message msg, Consumer<Message> success) {
        sendMsg(event.channel, msg, success, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(GuildMessageReceivedEvent event, Message msg, Consumer<Message> success, Consumer<Throwable> failure) {
        sendMsg(event.channel, msg, success, failure)
    }

    static void sendMsg(TextChannel channel, Message msg) {
        sendMsg(channel, msg, null, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(TextChannel channel, Message msg, Consumer<Message> success) {
        sendMsg(channel, msg, success, CUSTOM_QUEUE_ERROR)
    }

    static void sendMsg(TextChannel channel, Message msg, Consumer<Message> success, Consumer<Throwable> failure) {
        //Only send a message if we can talk
        if (channel != null && channel.guild.selfMember.hasPermission(channel,
                Permission.MESSAGE_WRITE, Permission.MESSAGE_READ))
            channel.sendMessage(msg).queue(success, failure)
    }
}
