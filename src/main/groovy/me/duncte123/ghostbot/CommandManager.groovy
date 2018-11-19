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

import com.ullink.slack.simpleslackapi.SlackChannel
import com.ullink.slack.simpleslackapi.SlackSession
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted
import me.duncte123.ghostbot.commands.ReactionCommand
import me.duncte123.ghostbot.commands.dannyphantom.audio.*
import me.duncte123.ghostbot.commands.dannyphantom.image.*
import me.duncte123.ghostbot.commands.dannyphantom.text.*
import me.duncte123.ghostbot.commands.dannyphantom.wiki.WikiCommand
import me.duncte123.ghostbot.commands.dannyphantom.wiki.WikiUserCommand
import me.duncte123.ghostbot.commands.fiveyearslater.FylCommicCommand
import me.duncte123.ghostbot.commands.fiveyearslater.FylWikiCommand
import me.duncte123.ghostbot.commands.main.*
import me.duncte123.ghostbot.commands.space.ISSCommand
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.objects.entities.GhostBotMessageEvent
import me.duncte123.ghostbot.objects.entities.impl.jda.JDAMessageEvent
import me.duncte123.ghostbot.objects.entities.impl.slack.SlackMessageEvent
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

import static me.duncte123.ghostbot.objects.CommandHelpers.sendMessage

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
        this.addCommand(new AuCommand())
        this.addCommand(new RandomGhostCommand())
        this.addCommand(new GamesCommand())

        this.addCommand(new HelpCommand())
        this.addCommand(new AboutCommand())

        this.addCommand(new ReloadAudioCommand())
        this.addCommand(new EvalCommand())
        this.addCommand(new ShardInfoCommand())
        this.addCommand(new RestartCommand())
        this.addCommand(new PingCommand())

        this.addCommand(new ISSCommand())
    }

    Set<Command> getCommands() {
        return UnmodifiableSet.unmodifiableSet(commands)
    }

    private Command getCommand(String name) {
        def cmd = commands.stream().filter { it.name.equalsIgnoreCase(name) }.findFirst()

        if (cmd.present) {
            return cmd.get()
        }

        cmd = commands.stream().filter { it.aliases.contains(name) }.findFirst()

        return cmd.orElse(null)
    }

    @SuppressWarnings('UnusedReturnValue')
    private boolean addCommand(Command command) {
        if (command.getName().contains(' ')) {
            throw new IllegalArgumentException('Name can\'t have spaces!')
        }

        //ParallelStream for less execution time
        if (this.commands.stream().anyMatch { it.name.equalsIgnoreCase(command.name) }) {
            def aliases = this.commands.stream().filter {
                it.name
                    .equalsIgnoreCase(command.name)
            }.findFirst().get().aliases

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
        def jdaEvent = new JDAMessageEvent(event)

        handleCommand(event.message.contentRaw, jdaEvent)
    }

    void handleCommand(SlackMessagePosted event, SlackSession session) {
        def slackEvent = new SlackMessageEvent(event, session)

        handleCommand(event.messageContent, slackEvent)
    }

    void handleCommand(String rw, GhostBotMessageEvent event) {
        final def split = rw.replaceFirst('(?i)' +
            Pattern.quote(Variables.PREFIX) + '|' +
            Pattern.quote(Variables.OTHER_PREFIX), '')
            .split('\\s+')

        final def invoke = split[0].toLowerCase()
        final def args = Arrays.copyOfRange(split, 1, split.length)

        def cmd = getCommand(invoke)
        def guild = event.guild.get()

        if (cmd == null) {
            logger.info('Unknown command: "{}" in "{}" with {}', invoke, guild, Arrays.toString(args))
            return
        }

        logger.info('Dispatching command "{}" in "{}" with {}', invoke, guild, Arrays.toString(args))

        if (!event.fromSlack) {
            def jdaEvent = event.originalEvent as GuildMessageReceivedEvent

            jdaEvent.channel.sendTyping().queue()
        } else {
            def session = event.API.get() as SlackSession
            def channel = event.channel.get() as SlackChannel

            session.sendTyping(channel)
        }

        if (!cmd.discordCompatible && !event.fromSlack) {
            sendMessage(event, "messages.compat.no_discord")
            return
        }

        if (!cmd.slackCompatible && event.fromSlack) {
            sendMessage(event, "messages.compat.no_slack")
            return
        }

        commandService.submit {
            try {
                def commandEvent = new CommandEvent(invoke, args, event, event.fromSlack)

                cmd.execute(commandEvent)
            } catch (Exception e) {
                e.printStackTrace()
            }
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
