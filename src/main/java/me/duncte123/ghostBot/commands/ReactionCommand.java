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

package me.duncte123.ghostBot.commands;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TLongSet;
import me.duncte123.ghostBot.CommandManager;
import me.duncte123.ghostBot.objects.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.utils.MiscUtil;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Taken from:
 * https://github.com/Almighty-Alpaca/JDA-Butler/blob/master/src/main/java/com/almightyalpaca/discord/jdabutler/commands/ReactionCommand.java
 */
public abstract class ReactionCommand extends Command {

    public final static String[] NUMBERS = new String[]{"1\u20E3", "2\u20E3", "3\u20E3",
            "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3", "9\u20E3", "\uD83D\uDD1F"};
    public final static String[] LETTERS = new String[]{"\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8",
            "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED", "\uD83C\uDDEE", "\uD83C\uDDEF"};
    public final static String LEFT_ARROW = "\u2B05";
    public final static String RIGHT_ARROW = "\u27A1";
    public final static String CANCEL = "\u274C";

    private final CommandManager.ReactionListenerRegistry listenerRegistry;

    public ReactionCommand(CommandManager.ReactionListenerRegistry registry) {
        this.listenerRegistry = registry;
    }

    protected final void addReactions(Message message, List<String> reactions, TLongSet allowedUsers,
                                      int timeout, TimeUnit timeUnit, Consumer<Integer> callback) {
        if (!ReactionListener.instances.containsKey(message.getIdLong()))
            new ReactionListener(message, reactions, allowedUsers, listenerRegistry, timeout, timeUnit, callback);
    }

    protected final void stopReactions(Message message) {
        stopReactions(message, true);
    }

    protected final void stopReactions(Message message, boolean removeReactions) {
        ReactionListener reactionListener = ReactionListener.instances.get(message.getIdLong());
        if (reactionListener != null)
            reactionListener.stop(removeReactions);
    }

    public static final class ReactionListener {
        private static final TLongObjectMap<ReactionListener> instances = MiscUtil.newLongMap();
        private final Message message;
        private final List<String> allowedReactions;
        private final TLongSet allowedUsers;
        private final CommandManager.ReactionListenerRegistry registry;
        private final Consumer<Integer> callback;
        private final Thread timeoutThread;

        private boolean shouldDeleteReactions = true;

        public ReactionListener(Message message, List<String> allowedReactions, TLongSet allowedUsers,
                                CommandManager.ReactionListenerRegistry registry, int timeout, TimeUnit timeUnit,
                                Consumer<Integer> callback) {
            instances.put(message.getIdLong(), this);
            this.message = message;
            this.allowedReactions = allowedReactions;
            this.allowedUsers = allowedUsers;
            this.registry = registry;
            this.callback = callback;
            this.timeoutThread = new Thread(new TimeoutHandler(timeout, timeUnit));
            this.timeoutThread.start();
            addReactions();
            registry.register(this);
        }

        public void handle(MessageReactionAddEvent event) {
            if (event.getMessageIdLong() != message.getIdLong())
                return;
            if (event.getUser() == event.getJDA().getSelfUser())
                return;


            try {
                event.getReaction().removeReaction(event.getUser()).queue();
            } catch (PermissionException ignored) {
            }

            if (!allowedUsers.isEmpty() && !allowedUsers.contains(event.getUser().getIdLong()))
                return;

            MessageReaction.ReactionEmote reactionEmote = event.getReactionEmote();
            String reaction = reactionEmote.isEmote() ? reactionEmote.getEmote().getId() : reactionEmote.getName();

            if (allowedReactions.contains(reaction))
                callback.accept(allowedReactions.indexOf(reaction));
        }

        private void stop(boolean removeReactions) {
            this.shouldDeleteReactions = removeReactions;
            this.timeoutThread.interrupt();
        }

        private void addReactions() {
            if (message.getChannelType() == ChannelType.TEXT && !message.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_ADD_REACTION))
                return;
            for (String reaction : allowedReactions) {
                Emote emote = null;
                try {
                    emote = message.getJDA().getEmoteById(reaction);
                } catch (NumberFormatException ignored) {
                }
                if (emote == null) {
                    message.addReaction(reaction).queue();
                } else {
                    message.addReaction(emote).queue();
                }
            }
        }

        private void cleanup() {
            registry.remove(ReactionListener.this);
            if (shouldDeleteReactions) {
                try {
                    message.clearReactions().queue();
                } catch (PermissionException ignored) {
                }
            }
            instances.remove(message.getIdLong());
        }

        private final class TimeoutHandler implements Runnable {
            private final int timeout;
            private final TimeUnit timeUnit;

            private TimeoutHandler(int timeout, TimeUnit timeUnit) {
                this.timeout = timeout;
                this.timeUnit = timeUnit;
            }

            @Override
            public void run() {
                try {
                    timeUnit.sleep(timeout);
                } catch (InterruptedException ignored) {
                }
                cleanup();
            }
        }
    }
}