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

package me.duncte123.ghostbot.commands

import gnu.trove.map.TLongObjectMap
import gnu.trove.set.TLongSet
import me.duncte123.ghostbot.CommandManager
import me.duncte123.ghostbot.GhostBot
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.utils.SpoopyUtils
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Emote
import net.dv8tion.jda.core.entities.Message
import static net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.core.utils.MiscUtil

import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Taken from:
 * https://github.com/Almighty-Alpaca/JDA-Butler/blob/master/src/main/java/com/almightyalpaca/discord/jdabutler/commands/ReactionCommand.java
 */
abstract class ReactionCommand extends Command {

    /*public final static String[] NUMBERS = ["1\u20E3", "2\u20E3", "3\u20E3",
                                            "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3", "9\u20E3", "\uD83D\uDD1F"]
    public final static String[] LETTERS = ["\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8",
                                            "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED",
                                            "\uD83C\uDDEE", "\uD83C\uDDEF"]*/
    public final static String LEFT_ARROW = "\u2B05"
    public final static String RIGHT_ARROW = "\u27A1"
    public final static String CANCEL = "\u274C"

    private final CommandManager.ReactionListenerRegistry listenerRegistry

    ReactionCommand(CommandManager.ReactionListenerRegistry registry) {
        this.listenerRegistry = registry
    }

    final void addReactions(Message message, List<String> reactions, TLongSet allowedUsers,
                            int timeout, TimeUnit timeUnit, Consumer<Integer> callback) {

        if (!ReactionListener.instances.containsKey(message.idLong)) {
            new ReactionListener(message, reactions, allowedUsers, listenerRegistry, timeout, timeUnit, callback)
        }
    }

    protected final void stopReactions(Message message) {
        stopReactions(message, true)
    }

    protected final void stopReactions(Message message, boolean removeReactions) {
        ReactionListener reactionListener = ReactionListener.instances.get(message.idLong)

        if (reactionListener != null) {
            reactionListener.stop(removeReactions)
        }
    }


    static final class ReactionListener {
        private static final TLongObjectMap<ReactionListener> instances = MiscUtil.newLongMap()
        private final long messageId
        private final long channelId
        private final List<String> allowedReactions
        private final TLongSet allowedUsers
        private final CommandManager.ReactionListenerRegistry registry
        private final Consumer<Integer> callback
        private final Thread timeoutThread

        private boolean shouldDeleteReactions = true

        ReactionListener(Message message, List<String> allowedReactions, TLongSet allowedUsers,
                         CommandManager.ReactionListenerRegistry registry, int timeout, TimeUnit timeUnit,
                         Consumer<Integer> callback) {

            instances.put(message.getIdLong(), this)

            this.messageId = message.getIdLong()
            this.channelId = message.getTextChannel().getIdLong()
            this.allowedReactions = allowedReactions
            this.allowedUsers = allowedUsers
            this.registry = registry
            this.callback = callback
            this.timeoutThread = new Thread(new TimeoutHandler(timeout, timeUnit))
            this.timeoutThread.start()

            addReactions(message)
            registry.register(this)

        }

        void handle(MessageReactionAddEvent event) {
            if (event.messageIdLong != messageId) {
                return
            }

            if (event.user == event.JDA.selfUser) {
                return
            }

            event.reaction.removeReaction(event.user).queue()

            if (!allowedUsers.empty && !allowedUsers.contains(event.user.idLong)) {
                return
            }

            ReactionEmote reactionEmote = event.reactionEmote
            String reaction = reactionEmote.isEmote() ? reactionEmote.emote.id : reactionEmote.name

            if (allowedReactions.contains(reaction)) {
                callback.accept(allowedReactions.indexOf(reaction))
            }
        }

        private void stop(boolean removeReactions) {
            this.shouldDeleteReactions = removeReactions
            this.timeoutThread.interrupt()
        }

        private void addReactions(Message message) {
            if (!message.guild.selfMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) {
                return
            }

            for (String reaction : allowedReactions) {

                if (!SpoopyUtils.isLong(reaction)) {
                    message.addReaction(reaction).queue()
                    continue
                }

                Emote emote = message.JDA.getEmoteById(reaction)

                if (emote != null) {
                    message.addReaction(emote).queue()
                }
            }
        }

        private void cleanup() {
            registry.remove(this)

            if (shouldDeleteReactions) {
                TextChannel channel = GhostBot.getInstance().getShardManager().getTextChannelById(channelId)
                channel.getMessageById(messageId).queue {
                    it.clearReactions().queue()
                }
            }

            instances.remove(messageId)
        }

        private final class TimeoutHandler implements Runnable {
            private final int timeout
            private final TimeUnit timeUnit

            private TimeoutHandler(int timeout, TimeUnit timeUnit) {
                this.timeout = timeout
                this.timeUnit = timeUnit
            }

            @Override
            void run() {
                try {
                    timeUnit.sleep(timeout)
                } catch (InterruptedException ignored) {
                }
                cleanup()
            }
        }
    }

}
