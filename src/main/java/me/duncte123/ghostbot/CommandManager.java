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

package me.duncte123.ghostbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.ghostbot.commands.ReactionCommand;
import me.duncte123.ghostbot.commands.dannyphantom.audio.*;
import me.duncte123.ghostbot.commands.dannyphantom.image.*;
import me.duncte123.ghostbot.commands.dannyphantom.text.*;
import me.duncte123.ghostbot.commands.dannyphantom.wiki.WikiCommand;
import me.duncte123.ghostbot.commands.dannyphantom.wiki.WikiUserCommand;
import me.duncte123.ghostbot.commands.fiveyearslater.FylCommicCommand;
import me.duncte123.ghostbot.commands.fiveyearslater.FylWikiCommand;
import me.duncte123.ghostbot.commands.main.*;
import me.duncte123.ghostbot.commands.space.ISSCommand;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.objects.command.JDASlashCommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private final Map<String, String> aliases = new ConcurrentHashMap<>();
    private final ExecutorService commandService = Executors.newCachedThreadPool((it) -> new Thread(it, "Command-Thread"));
    final ReactionListenerRegistry reactListReg = new ReactionListenerRegistry();

    public CommandManager(Container container) {
        final ObjectMapper jackson = container.getJackson();

        this.addCommand(new GoingGhostCommand());
        this.addCommand(new WailCommand());
        this.addCommand(new FruitloopCommand());
        this.addCommand(new EmberCommand());
        this.addCommand(new DespacitoCommand());
        this.addCommand(new BoxGhostCommand());

        this.addCommand(new ImageCommand());
        this.addCommand(new GifCommand());
        this.addCommand(new OtherGhostCommands(jackson));
        this.addCommand(new DoppelgangerComicCommand(this.reactListReg, container));
        this.addCommand(new TheelectricundeadCommand(this.reactListReg, container));
        this.addCommand(new FylCommicCommand(this.reactListReg, jackson));
        this.addCommand(new DPArtistsCommand());

        this.addCommand(new WikiCommand());
        this.addCommand(new WikiUserCommand());
        this.addCommand(new FylWikiCommand());
        this.addCommand(new PetitionCommand());

        this.addCommand(new QuotesCommand());
        this.addCommand(new AuCommand());
        this.addCommand(new RandomGhostCommand());
        this.addCommand(new GamesCommand());
        this.addCommand(new TheJCommand());

        this.addCommand(new HelpCommand());
        this.addCommand(new AboutCommand());
        this.addCommand(new VoteCommand());

        this.addCommand(new ReloadAudioCommand());
        this.addCommand(new EvalCommand());
        this.addCommand(new ShardInfoCommand());
        this.addCommand(new RestartCommand());
        this.addCommand(new PingCommand());
        this.addCommand(new UptimeCommand());

        this.addCommand(new DrakeCommand());
        this.addCommand(new ISSCommand());
    }

    private void addCommand(Command command) {
        if (command.getName().contains(" ")) {
            throw new IllegalArgumentException("Name can't have spaces!");
        }

        if (this.commands.containsKey(command.getName())) {
            throw new IllegalArgumentException(String.format("Command %s already present", command.getName()));
        }

        for (final String alias : command.getAliases()) {
            if (this.aliases.containsKey(alias)) {
                throw new IllegalArgumentException(String.format("Alias %s already present", alias));
            }
        }

        this.commands.put(command.getName(), command);

        for (final String alias : command.getAliases()) {
            this.aliases.put(alias, command.getName());
        }
    }

    public Collection<Command> getCommands() {
        return this.commands.values();
    }

    public Command getCommand(String name) {
        Command found = this.commands.get(name);

        if (found == null) {
            final String forAlias = this.aliases.get(name);

            if (forAlias != null) {
                found = this.commands.get(forAlias);
            }
        }

        return found;
    }

    void handleSlashCommand(SlashCommandEvent event, Container container) {
        if (!event.isFromGuild()) {
            // ignore dms
            return;
        }

        final String invoke = event.getName();
        final Command cmd = getCommand(invoke);

        if (cmd == null) {
            event.reply("This command cannot be handled right now").setEphemeral(true).queue();
            return;
        }

        final Guild guild = event.getGuild();

        if (cmd.shouldAck()) {
            event.deferReply().setEphemeral(false).queue();
        }

        dispatchCommand(cmd, guild, invoke, () ->  new JDASlashCommandEvent(event, container));
    }

    void handleCommand(GuildMessageReceivedEvent event) {
        final String rw = event.getMessage().getContentRaw();
        final String[] split = rw.replaceFirst("(?i)" +
            Pattern.quote(Variables.PREFIX) + "|" +
            Pattern.quote(Variables.OTHER_PREFIX), "")
            .split("\\s+");

        final String invoke = split[0].toLowerCase();

        final Command cmd = getCommand(invoke);

        if (cmd != null) {
            event.getChannel()
                .sendMessage("Normal have been deprecated in favor of slash commands.\nRead as to why here: <https://support-dev.discord.com/hc/en-us/articles/4404772028055>")
                .reference(event.getMessage())
                .mentionRepliedUser(false)
                .queue();
        }
    }

    private void dispatchCommand(@Nullable Command cmd, Guild guild, String invoke, Supplier<ICommandEvent> eventSupplier) {
        if (cmd == null) {
            logger.info("Unknown command: \"{}\" in \"{}\"", invoke, guild);

            return;
        }


        commandService.submit(() -> {
            final ICommandEvent event = eventSupplier.get();

            logger.info(
                "Dispatching command \"{}\" in \"{}\" with {}",
                cmd.getClass().getSimpleName(),
                guild,
                event.getArgs()
            );

            try {
                event.getChannel().sendTyping().queue();

                cmd.execute(event);
            } catch (Exception e) {
                e.printStackTrace();
                event.reply("Something went wrong when processing your command");
            }
        });
    }

    ExecutorService getCommandService() {
        return commandService;
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

        boolean handle(final ButtonClickEvent event) {
            synchronized (this.listeners) {
                for (final ReactionCommand.ReactionListener listener : this.listeners) {
                    // there's only one handler per message
                    if (listener.handle(event)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

}
