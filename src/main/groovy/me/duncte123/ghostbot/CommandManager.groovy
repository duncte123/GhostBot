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

package me.duncte123.ghostbot

import me.duncte123.ghostBot.commands.ReactionCommand
import me.duncte123.ghostBot.commands.dannyPhantom.audio.*
import me.duncte123.ghostBot.commands.dannyPhantom.image.*
import me.duncte123.ghostBot.commands.dannyPhantom.text.GamesCommand
import me.duncte123.ghostBot.commands.dannyPhantom.text.PetitionCommand
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand
import me.duncte123.ghostBot.commands.dannyPhantom.text.RandomGhostCommand
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiCommand
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiUserCommand
import me.duncte123.ghostBot.commands.fiveYearsLater.FylCommicCommand
import me.duncte123.ghostBot.commands.fiveYearsLater.FylWikiCommand
import me.duncte123.ghostBot.commands.main.*
import me.duncte123.ghostBot.objects.Command
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import org.apache.commons.collections4.set.UnmodifiableSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern

class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class)
    private final Set<Command> commands = ConcurrentHashMap.newKeySet()
    final ExecutorService commandService = Executors.newCachedThreadPool { new Thread(it, "Command-Thread") }
    final ReactionListenerRegistry reactListReg = new ReactionListenerRegistry()

    CommandManager() {
        this.addCommand(new GoingGhostCommand())
        this.addCommand(new WailCommand())
        this.addCommand(new FruitloopCommand())
        this.addCommand(new EmberCommand())
        this.addCommand(new BoxGhostCommand())

        this.addCommand(new ImageCommand())
        this.addCommand(new GifCommand())
        this.addCommand(new OtherGhostCommands())
        this.addCommand(new DoppelgangerComicCommand(this.reactListReg))
        this.addCommand(new FylCommicCommand(this.reactListReg))
        this.addCommand(new DPArtistsCommand())

        this.addCommand(new WikiCommand())
        this.addCommand(new WikiUserCommand())
        this.addCommand(new FylWikiCommand())
        this.addCommand(new PetitionCommand())

        this.addCommand(new QuotesCommand())
        this.addCommand(new RandomGhostCommand())
        this.addCommand(new GamesCommand())

        this.addCommand(new HelpCommand())
        this.addCommand(new AboutCommand())

        this.addCommand(new ReloadAudioCommand())
        this.addCommand(new EvalCommand())
        this.addCommand(new ShardInfoCommand())
        this.addCommand(new RestartCommand())
        this.addCommand(new PingCommand())
    }

    Set<Command> getCommands() {
        return UnmodifiableSet.unmodifiableSet(commands)
    }

    private Command getCommand(String name) {
        Optional<Command> cmd = commands.stream().filter { it.name.equalsIgnoreCase(name) }.findFirst()

        if (cmd.isPresent()) {
            return cmd.get()
        }

        cmd = commands.stream().filter { Arrays.asList(it.aliases).contains(name) }.findFirst()

        return cmd.orElse(null)
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean addCommand(Command command) {
        if (command.getName().contains(" ")) {
            throw new IllegalArgumentException("Name can't have spaces!")
        }

        //ParallelStream for less execution time
        if (this.commands.stream().anyMatch { it.name.equalsIgnoreCase(command.name) }) {
            List<String> aliases = Arrays.asList(this.commands.stream().filter { it.name
                    .equalsIgnoreCase(command.name) }.findFirst().get().aliases)
            for (String alias : command.aliases) {
                if (aliases.contains(alias)) {
                    return false
                }
            }
            return false
        }
        this.commands.add(command)

        return true
    }

    void handleCommand(GuildMessageReceivedEvent event) {
        final String rw = event.message.contentRaw
        final String[] split = rw.replaceFirst("(?i)" +
                Pattern.quote(Variables.PREFIX) + "|" +
                Pattern.quote(Variables.OTHER_PREFIX), "")
                .split("\\s+")

        final String invoke = split[0].toLowerCase()
        final String[] args = Arrays.copyOfRange(split, 1, split.length)

        Command cmd = getCommand(invoke)

        if (cmd != null) {
            logger.info("Dispatching command \"{}\" in \"{}\" with {}", invoke, event.guild, Arrays.toString(args))
            event.channel.sendTyping().queue()
            commandService.submit {
                try {
                    cmd.execute(invoke, args, event)
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
            /*cmd.execute(invoke, args, event);*/
        } else {
            logger.info("Unknown command: \"{}\" in \"{}\" with {}", invoke, event.guild, Arrays.toString(args))
        }

    }

    /**
     * Taken from:
     * https://github.com/Almighty-Alpaca/JDA-Butler/blob/master/src/main/java/com/almightyalpaca/discord/jdabutler/commands/Dispatcher.java#L135-L168
     */
    static class ReactionListenerRegistry {
        private final Set<ReactionCommand.ReactionListener> listeners

        private ReactionListenerRegistry() {
            this.listeners = new HashSet<>()
        }

        void register(final ReactionCommand.ReactionListener listener) {
            synchronized (this.listeners) {
                this.listeners.add(listener)
            }
        }

        void remove(final ReactionCommand.ReactionListener listener) {
            synchronized (this.listeners) {
                this.listeners.remove(listener)
            }
        }

        void handle(final MessageReactionAddEvent event) {
            synchronized (this.listeners) {
                for (final ReactionCommand.ReactionListener listener : this.listeners)
                    listener.handle(event)
            }
        }
    }

}
