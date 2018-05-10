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
 *     but WITHOUT ANY WARRANTY without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.duncte123.ghostBot

import me.duncte123.ghostBot.commands.AboutCommand
import me.duncte123.ghostBot.commands.HelpCommand
import me.duncte123.ghostBot.commands.dannyPhantom.audio.BoxGhostCommand
import me.duncte123.ghostBot.commands.dannyPhantom.audio.EmberCommand
import me.duncte123.ghostBot.commands.dannyPhantom.audio.FruitLoopCommand
import me.duncte123.ghostBot.commands.dannyPhantom.audio.GoingGhostCommand
import me.duncte123.ghostBot.commands.dannyPhantom.audio.WailCommand
import me.duncte123.ghostBot.commands.dannyPhantom.image.GifCommand
import me.duncte123.ghostBot.commands.dannyPhantom.image.ImageCommand
import me.duncte123.ghostBot.commands.dannyPhantom.image.OtherGhostCommands
import me.duncte123.ghostBot.commands.dannyPhantom.text.GamesCommand
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand
import me.duncte123.ghostBot.commands.dannyPhantom.text.RandomGhostCommand
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiCommand
import me.duncte123.ghostBot.commands.dannyPhantom.wiki.WikiUserCommand
import me.duncte123.ghostBot.commands.ownerOnly.EvalCommand
import me.duncte123.ghostBot.objects.Command
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.collections4.set.UnmodifiableSet

import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

class CommandManager {

    private final Set<Command> commands = ConcurrentHashMap.newKeySet()

    CommandManager() {
        addCommand(new HelpCommand())
        addCommand(new AboutCommand())

        addCommand(new WikiCommand())
        addCommand(new WikiUserCommand())

        /*addCommand(new GamesCommand())
        addCommand(new QuotesCommand())
        addCommand(new RandomGhostCommand())

        addCommand(new ImageCommand())
        addCommand(new GifCommand())
        addCommand(new OtherGhostCommands())

        addCommand(new BoxGhostCommand())
        addCommand(new EmberCommand())
        addCommand(new FruitLoopCommand())
        addCommand(new GoingGhostCommand())
        addCommand(new WailCommand())*/

        addCommand(new EvalCommand())
    }

    Set<Command> getCommands() {
        return UnmodifiableSet.unmodifiableSet(commands)
    }

    private Command getCommand(String name) {
        Optional<Command> cmd = commands.stream().filter{
            it.name.equalsIgnoreCase(name)
        }.findFirst()

        if (cmd.isPresent()) {
            return cmd.get()
        }

        cmd = commands.stream().filter{
            it.aliases.contains(name)
        }.findFirst()

        return cmd.orElse(null)
    }

    @SuppressWarnings("UnusedReturnValue")
    private boolean addCommand(Command command) {
        if(command.name == null) {
            throw new IllegalArgumentException("Command has a null name: $command")
        }
        if (command.name.contains(" ")) {
            throw new IllegalArgumentException("Name can't have spaces!")
        }

        if (commands.stream().anyMatch { it.name.equalsIgnoreCase(command.name) }) {
            List<String> aliases = Arrays.asList(commands.stream().filter {
                it.name.equalsIgnoreCase(command.name)
            }.findFirst().get().aliases)
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
        final String[] split = rw.replaceFirst("(?i)${Pattern.quote(Variables.PREFIX)}", "")
                .split("\\s+")
        final String invoke = split[0].toLowerCase()
        final String[] args = Arrays.copyOfRange(split, 1, split.length)

        Command cmd = getCommand(invoke)

        if (cmd != null) {
            event.channel.sendTyping().queue {
                cmd.execute(invoke, args, event)
            }
        }
    }
}
