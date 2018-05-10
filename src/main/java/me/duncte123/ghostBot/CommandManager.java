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

import me.duncte123.ghostBot.commands.dannyPhantom.audio.*;
import me.duncte123.ghostBot.commands.dannyPhantom.image.GifCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.image.ImageCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.image.OtherGhostCommands;
import me.duncte123.ghostBot.commands.dannyPhantom.text.GamesCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.text.RandomGhostCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiCommand;
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiUserCommand;
import me.duncte123.ghostBot.commands.main.AboutCommand;
import me.duncte123.ghostBot.commands.main.HelpCommand;
import me.duncte123.ghostBot.commands.main.ReloadAudioCommand;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.set.UnmodifiableSet;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CommandManager {

    private final Set<Command> commands = ConcurrentHashMap.newKeySet();

    public CommandManager() {
        this.addCommand(new GoingGhostCommand());
        this.addCommand(new WailCommand());
        this.addCommand(new FruitloopCommand());
        this.addCommand(new EmberCommand());
        this.addCommand(new BoxGhostCommand());

        this.addCommand(new ImageCommand());
        this.addCommand(new GifCommand());
        this.addCommand(new OtherGhostCommands());

        this.addCommand(new WikiCommand());
        this.addCommand(new WikiUserCommand());

        this.addCommand(new QuotesCommand());
        this.addCommand(new RandomGhostCommand());
        this.addCommand(new GamesCommand());

        this.addCommand(new HelpCommand());
        this.addCommand(new AboutCommand());
        this.addCommand(new ReloadAudioCommand());
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

    public void handleCommand(GuildMessageReceivedEvent event) {
        final String rw = event.getMessage().getContentRaw();
        final String[] split = rw.replaceFirst("(?i)" + Pattern.quote(Variables.PREFIX), "")
                .split("\\s+");
        final String invoke = split[0].toLowerCase();
        final String[] args = Arrays.copyOfRange(split, 1, split.length);

        Command cmd = getCommand(invoke);

        if (cmd != null) {
            event.getChannel().sendTyping().queue();
            cmd.execute(invoke, args, event);
        }

    }
}
