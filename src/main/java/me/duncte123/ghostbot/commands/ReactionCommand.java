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
import me.duncte123.ghostbot.objects.command.Command;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class ReactionCommand extends Command {
    private static final String LEFT_ARROW = "\u2B05";
    private static final String RIGHT_ARROW = "\u27A1";
    private static final String CANCEL = "\u274C";
//    private static final String CANCEL = "\uD83C\uDDFD";
    protected static final ButtonFunction LEFT_RIGHT_CANCEL = (userId, leftDisabled, rightDisabled) -> List.of(
        Button.primary("previous:" + userId, "Previous").withEmoji(Emoji.fromUnicode(LEFT_ARROW)).withDisabled(leftDisabled),
        Button.primary("next:" + userId, "Next").withEmoji(Emoji.fromUnicode(RIGHT_ARROW)).withDisabled(rightDisabled),
        Button.secondary("cancel:" + userId, "Exit").withEmoji(Emoji.fromUnicode(CANCEL))
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

    protected final void enableButtons(Message message, int timeout, TimeUnit timeUnit, Consumer<ButtonClickEvent> callback) {
        if (!ReactionListener.instances.containsKey(message.getIdLong())) {
            new ReactionListener(message, listenerRegistry, timeout, timeUnit, callback);
        }
    }
    protected final void disableButtons(ButtonClickEvent event) {
        ReactionListener reactionListener = ReactionListener.instances.get(event.getMessageIdLong());

        if (reactionListener != null) {
            reactionListener.cleanUpEvent = event;
            reactionListener.stop();
        }
    }


    public static final class ReactionListener {
        private static final TLongObjectMap<ReactionListener> instances = MiscUtil.newLongMap();
        private final long messageId;
        private final CommandManager.ReactionListenerRegistry registry;
        private final Consumer<ButtonClickEvent> callback;
        private final ScheduledFuture<?> timeoutFuture;

        protected ButtonClickEvent cleanUpEvent = null;

        ReactionListener(Message message, CommandManager.ReactionListenerRegistry registry, int timeout, TimeUnit timeUnit,
                                Consumer<ButtonClickEvent> callback) {

            instances.put(message.getIdLong(), this);

            this.messageId = message.getIdLong();
            this.registry = registry;
            this.callback = callback;
            this.timeoutFuture = scheduler.schedule(this::cleanup, timeout, timeUnit);

            registry.register(this);
        }

        public boolean handle(ButtonClickEvent event) {
            // we don't need to check the button id as we know what buttons are on this message
            if (event.getMessageIdLong() != messageId) {
                return false;
            }

            if (!event.isFromGuild() || !event.getComponentId().endsWith(event.getUser().getId())) {
                event.deferReply(true).setContent("This button is not for you :P").queue();
                return true;
            }

            callback.accept(event);

            return true;
        }

        private void stop() {
            this.timeoutFuture.cancel(true);
            this.cleanup();
        }

        // TODO: supply edit hook
        private void cleanup() {
            registry.remove(this);

            if (this.cleanUpEvent != null) {
                this.cleanUpEvent.deferEdit()
                    .setActionRows(
                        ActionRow.of(
                            // message is only null on ephemeral messages
                            this.cleanUpEvent.getMessage()
                                .getButtons()
                                .stream()
                                .map(Button::asDisabled)
                                .collect(Collectors.toList())
                        )
                    )
                    .queue();
            }

            instances.remove(messageId);
        }
    }

    public interface ButtonFunction {
        List<Button> getButtons(long userId, boolean leftDisabled, boolean rightDisabled);

        @Deprecated
        default List<Button> apply(long userId) {
            return getButtons(userId, false, false);
        }

        default ActionRow toActionRow(long userId, boolean leftDisabled, boolean rightDisabled) {
            return ActionRow.of(getButtons(userId, leftDisabled, rightDisabled));
        }
    }
}
