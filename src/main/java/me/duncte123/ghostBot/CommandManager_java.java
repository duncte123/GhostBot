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

import me.duncte123.ghostBot.objects.Command_java;
import me.duncte123.ghostBot.variables.Variables_java;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.collections4.set.UnmodifiableSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CommandManager_java {

    private final Set<Command_java> commands = ConcurrentHashMap.newKeySet();

    public CommandManager_java() {
        /*this.addCommand(new GoingGhostCommandJava());
        this.addCommand(new WailCommandJava());
        this.addCommand(new FruitloopCommandJava());
        this.addCommand(new EmberCommandJava());
        this.addCommand(new BoxGhostCommandJava());

        this.addCommand(new ImageCommandJava());
        this.addCommand(new GifCommandJava());
        this.addCommand(new OtherGhostCommands());

        this.addCommand(new WikiCommandJava());
        this.addCommand(new WikiUserCommandJava());

        this.addCommand(new QuotesCommandJava());
        this.addCommand(new RandomGhostCommandJava());
        this.addCommand(new GamesCommandJava());

        this.addCommand(new HelpCommandJava());
        this.addCommand(new AboutCommandJava());
        this.addCommand(new ReloadAudioCommandJava());*/
    }

    public Set<Command_java> getCommands() {
        return UnmodifiableSet.unmodifiableSet(commands);
    }

    private Command_java getCommand(String name) {
        Optional<Command_java> cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();

        if (cmd.isPresent()) {
            return cmd.get();
        }

        cmd = commands.stream().filter(c -> Arrays.asList(c.getAliases()).contains(name)).findFirst();

        return cmd.orElse(null);
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean addCommand(Command_java command) {
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
        final String[] split = rw.replaceFirst("(?i)" + Pattern.quote(Variables_java.PREFIX), "")
                .split("\\s+");
        final String invoke = split[0].toLowerCase();
        final String[] args = Arrays.copyOfRange(split, 1, split.length);

        Command_java cmd = getCommand(invoke);

        if (cmd != null)
            cmd.execute(invoke, args, event);
    }
}
