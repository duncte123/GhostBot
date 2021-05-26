/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.GhostBot;
import me.duncte123.ghostbot.objects.command.Command;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.ActionRow;
import net.dv8tion.jda.api.interactions.button.Button;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.stream.Collectors;

public abstract class ReactionCommand extends Command {
    private static final String LEFT_ARROW = "\u2B05";
    private static final String RIGHT_ARROW = "\u27A1";
    private static final String CANCEL = "\u274C";
    // Other way of doing it?
//    private static final String CANCEL = "\uD83C\uDDFD";
    public static final LongFunction<List<Button>> LEFT_RIGHT_CANCEL_EMOTE_ONLY = (userId) -> List.of(
        Button.primary("previous:" + userId, Emoji.ofUnicode(LEFT_ARROW)),
        Button.primary("next:" + userId, Emoji.ofUnicode(RIGHT_ARROW)),
        Button.secondary("cancel:" + userId, Emoji.ofUnicode(CANCEL))
    );
    protected static final LongFunction<List<Button>> LEFT_RIGHT_CANCEL = (userId) -> List.of(
        Button.primary("previous:" + userId, "Previous").withEmoji(Emoji.ofUnicode(LEFT_ARROW)),
        Button.primary("next:" + userId, "Next").withEmoji(Emoji.ofUnicode(RIGHT_ARROW)),
        Button.secondary("cancel:" + userId, "Exit").withEmoji(Emoji.ofUnicode(CANCEL))
    );
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10, (r) -> {
        final Thread t = new Thread(r, "Menu Thread");
        t.setDaemon(true);
        return t;
    });


    private final CommandManager.ReactionListenerRegistry listenerRegistry;

    public ReactionCommand(CommandManager.ReactionListenerRegistry registry) {
        this.listenerRegistry = registry;
    }

    protected final void addButtons(Message message, int timeout, TimeUnit timeUnit, Consumer<Button> callback) {
        if (!ReactionListener.instances.containsKey(message.getIdLong())) {
            new ReactionListener(message, listenerRegistry, timeout, timeUnit, callback);
        }
    }

    protected final void disableButtons(Message message) {
        disableButtons(message, true);
    }

    protected final void disableButtons(Message message, boolean removeButtons) {
        ReactionListener reactionListener = ReactionListener.instances.get(message.getIdLong());

        if (reactionListener != null) {
            reactionListener.stop(removeButtons);
        }
    }


    public static final class ReactionListener {
        private static final TLongObjectMap<ReactionListener> instances = MiscUtil.newLongMap();
        private final long messageId;
        private final long channelId;
        private final CommandManager.ReactionListenerRegistry registry;
        private final Consumer<Button> callback;
        private final ScheduledFuture<?> timeoutFuture;

        private boolean shouldRemoveButtons = true;

        ReactionListener(Message message, CommandManager.ReactionListenerRegistry registry, int timeout, TimeUnit timeUnit,
                                Consumer<Button> callback) {

            instances.put(message.getIdLong(), this);

            this.messageId = message.getIdLong();
            this.channelId = message.getTextChannel().getIdLong();
            this.registry = registry;
            this.callback = callback;
            this.timeoutFuture = scheduler.schedule(this::cleanup, timeout, timeUnit);

            registry.register(this);
        }

        public void handle(ButtonClickEvent event) {
            // we don't need to check the button id as we know what buttons are on this message
            if (event.getMessageIdLong() != messageId) {
                return;
            }

            if (!event.isFromGuild() || !event.getComponentId().endsWith(event.getUser().getId())) {
                event.deferReply(true).setContent("This button is not for you :P").queue();
                return;
            }

            event.deferEdit().queue();

            callback.accept(event.getButton());
        }

        private void stop(boolean removeButtons) {
            this.shouldRemoveButtons = removeButtons;
            this.timeoutFuture.cancel(true);
            this.cleanup();
        }

        // TODO: supply edit hook
        private void cleanup() {
            registry.remove(this);

            final TextChannel channel = GhostBot.getInstance().getShardManager().getTextChannelById(channelId);

            if (channel != null) {
                channel.retrieveMessageById(messageId).queue((message) -> {
                    if (shouldRemoveButtons) {
                        message.editMessage(message)
                            .setActionRows(ActionRow.of())
                            .queue();
                    } else {
                        final List<Button> disabledButtons = message.getButtons()
                            .stream()
                            .map(Button::asDisabled)
                            .collect(Collectors.toList());

                        message.editMessage(message)
                            .setActionRows(ActionRow.of(disabledButtons))
                            .queue();
                    }
                });
            }

            instances.remove(messageId);
        }
    }
}
