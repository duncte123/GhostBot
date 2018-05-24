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

package me.duncte123.ghostBot;

import me.duncte123.ghostBot.commands.ReactionCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.audio.*;
import me.duncte123.ghostBot.commands.dannyPhantom.image.*;
import me.duncte123.ghostBot.commands.dannyPhantom.text.GamesCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.RandomGhostCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiUserCommand;
import me.duncte123.ghostBot.commands.main.*;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import org.apache.commons.collections4.set.UnmodifiableSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class CommandManager {

    private final Set<Command> commands = ConcurrentHashMap.newKeySet();
    final ScheduledExecutorService commandService = Executors.newScheduledThreadPool(5,
            (r) -> new Thread(r, "Command-Thread"));

    final ReactionListenerRegistry reactListReg = new ReactionListenerRegistry();

    public CommandManager() {
        this.addCommand(new GoingGhostCommand());
        this.addCommand(new WailCommand());
        this.addCommand(new FruitloopCommand());
        this.addCommand(new EmberCommand());
        this.addCommand(new BoxGhostCommand());

        this.addCommand(new ImageCommand());
        this.addCommand(new GifCommand());
        this.addCommand(new OtherGhostCommands());
        this.addCommand(new DoppelgangerComicCommand(this.reactListReg));
        this.addCommand(new DPArtistsCommand());

        this.addCommand(new WikiCommand());
        this.addCommand(new WikiUserCommand());

        this.addCommand(new QuotesCommand());
        this.addCommand(new RandomGhostCommand());
        this.addCommand(new GamesCommand());

        this.addCommand(new HelpCommand());
        this.addCommand(new AboutCommand());

        this.addCommand(new ReloadAudioCommand());
        this.addCommand(new EvalCommand());
    }

    public Set<Command> getCommands() {
        return UnmodifiableSet.unmodifiableSet(commands);
    }

    private Command getCommand(String name) {
        Optional<Command> cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();

        if (cmd.isPresent()) {
            return cmd.get();
        }

        cmd = commands.stream().filter(c -> Arrays.asList(c.getAliases()).contains(name)).findFirst();

        return cmd.orElse(null);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean addCommand(Command command) {
        if (command.getName().contains(" ")) {
            throw new IllegalArgumentException("Name can't have spaces!");
        }

        //ParallelStream for less execution time
        if (this.commands.stream().anyMatch((cmd) -> cmd.getName().equalsIgnoreCase(command.getName()))) {
            List<String> aliases = Arrays.asList(this.commands.stream().filter((cmd) -> cmd.getName().equalsIgnoreCase(command.getName())).findFirst().get().getAliases());
            for (String alias : command.getAliases()) {
                if (aliases.contains(alias)) {
                    return false;
                }
            }
            return false;
        }
        this.commands.add(command);

        return true;
    }

    void handleCommand(GuildMessageReceivedEvent event) {
        final String rw = event.getMessage().getContentRaw();
        final String[] split = rw.replaceFirst("(?i)" + Pattern.quote(Variables.PREFIX), "")
                .split("\\s+");
        final String invoke = split[0].toLowerCase();
        final String[] args = Arrays.copyOfRange(split, 1, split.length);

        Command cmd = getCommand(invoke);

        if (cmd != null) {
            event.getChannel().sendTyping().queue();
            commandService.schedule(() -> {
                try {
                    cmd.execute(invoke, args, event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, TimeUnit.MILLISECONDS);
            /*cmd.execute(invoke, args, event);*/
        }

    }

    /**
     * Taken from:
     * https://github.com/Almighty-Alpaca/JDA-Butler/blob/master/src/main/java/com/almightyalpaca/discord/jdabutler/commands/Dispatcher.java#L135-L168
     */
    public static class ReactionListenerRegistry {
        private final Set<ReactionCommand.ReactionListener> listeners;

        private ReactionListenerRegistry() {
            this.listeners = new HashSet<>();
        }

        public void register(final ReactionCommand.ReactionListener listener) {
            synchronized (this.listeners) {
                this.listeners.add(listener);
            }
        }

        public void remove(final ReactionCommand.ReactionListener listener) {
            synchronized (this.listeners) {
                this.listeners.remove(listener);
            }
        }

        void handle(final MessageReactionAddEvent event) {
            synchronized (this.listeners) {
                for (final ReactionCommand.ReactionListener listener : this.listeners)
                    listener.handle(event);
            }
        }
    }
}
