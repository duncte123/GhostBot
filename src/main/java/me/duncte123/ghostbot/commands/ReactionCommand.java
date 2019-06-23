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

package me.duncte123.ghostbot.commands;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.set.TLongSet;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.GhostBot;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class ReactionCommand extends Command {
    private static final String LEFT_ARROW = "\u2B05";
    private static final String RIGHT_ARROW = "\u27A1";
    private static final String CANCEL = "\u274C";
    public static final List<String> LEFT_RIGHT_CANCEL = List.of(LEFT_ARROW, RIGHT_ARROW, CANCEL);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(100);


    private final CommandManager.ReactionListenerRegistry listenerRegistry;

    public ReactionCommand(CommandManager.ReactionListenerRegistry registry) {
        this.listenerRegistry = registry;
    }

    public final void addReactions(Message message, List<String> reactions, TLongSet allowedUsers,
                                   int timeout, TimeUnit timeUnit, Consumer<Integer> callback) {

        if (!ReactionListener.instances.containsKey(message.getIdLong())) {
            new ReactionListener(message, reactions, allowedUsers, listenerRegistry, timeout, timeUnit, callback);
        }
    }

    protected final void stopReactions(Message message) {
        stopReactions(message, true);
    }

    protected final void stopReactions(Message message, boolean removeReactions) {
        ReactionListener reactionListener = ReactionListener.instances.get(message.getIdLong());

        if (reactionListener != null) {
            reactionListener.stop(removeReactions);
        }
    }


    public static final class ReactionListener {
        private static final TLongObjectMap<ReactionListener> instances = MiscUtil.newLongMap();
        private final long messageId;
        private final long channelId;
        private final List<String> allowedReactions;
        private final TLongSet allowedUsers;
        private final CommandManager.ReactionListenerRegistry registry;
        private final Consumer<Integer> callback;
        private final ScheduledFuture<?> timeoutFuture;

        private boolean shouldDeleteReactions = true;

        public ReactionListener(Message message, List<String> allowedReactions, TLongSet allowedUsers,
                                CommandManager.ReactionListenerRegistry registry, int timeout, TimeUnit timeUnit,
                                Consumer<Integer> callback) {

            instances.put(message.getIdLong(), this);

            this.messageId = message.getIdLong();
            this.channelId = message.getTextChannel().getIdLong();
            this.allowedReactions = allowedReactions;
            this.allowedUsers = allowedUsers;
            this.registry = registry;
            this.callback = callback;
            this.timeoutFuture = scheduler.schedule(this::cleanup, timeout, timeUnit);

            addReactions(message);
            registry.register(this);

        }

        public void handle(MessageReactionAddEvent event) {
            if (event.getMessageIdLong() != messageId) {
                return;
            }

            if (event.getUser().equals(event.getJDA().getSelfUser())) {
                return;
            }

            if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getReaction().removeReaction(event.getUser()).queue(null, (__) -> {});
            }

            if (!allowedUsers.isEmpty() && !allowedUsers.contains(event.getUser().getIdLong())) {
                return;
            }

            final ReactionEmote reactionEmote = event.getReactionEmote();
            final String reaction = reactionEmote.isEmote() ? reactionEmote.getEmote().getId() : reactionEmote.getName();

            if (allowedReactions.contains(reaction)) {
                callback.accept(allowedReactions.indexOf(reaction));
            }
        }

        private void stop(boolean removeReactions) {
            this.shouldDeleteReactions = removeReactions;
            this.timeoutFuture.cancel(true);
            this.cleanup();
        }

        private void addReactions(Message message) {
            if (!message.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_ADD_REACTION)) {
                return;
            }

            for (final String reaction : allowedReactions) {
                if (!SpoopyUtils.isLong(reaction)) {
                    message.addReaction(reaction).queue();
                }
            }
        }

        private void cleanup() {
            registry.remove(this);

            if (shouldDeleteReactions) {
                final TextChannel channel = GhostBot.getInstance().getShardManager().getTextChannelById(channelId);

                if (channel != null && channel.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    channel.retrieveMessageById(messageId).queue(
                        (it) -> it.clearReactions().queue(null, (t) -> {})
                    );
                }
            }

            instances.remove(messageId);
        }
    }
}
