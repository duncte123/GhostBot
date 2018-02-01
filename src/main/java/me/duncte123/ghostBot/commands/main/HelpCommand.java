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

package me.duncte123.ghostBot.commands.main;

import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HelpCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            String toSearch = StringUtils.join(args, " ");
            if (toSearch.startsWith(Variables.PREFIX))
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.PREFIX), "");

            for (Command cmd : SpoopyUtils.commandManager.getCommands()) {
                if (cmd.getName().equals(toSearch)) {
                    sendMsg(event, "Command help for `" + cmd.getName() + "` :\n" + cmd.getHelp() + (cmd.getAliases().length > 0 ? "\nAliases: " + StringUtils.join(cmd.getAliases(), ", ") : ""));
                    return;
                } else {
                    for (String alias : cmd.getAliases()) {
                        if (alias.equals(toSearch)) {
                            sendMsg(event, "Command help for `" + cmd.getName() + "` :\n" + cmd.getHelp() + (cmd.getAliases().length > 0 ? "\nAliases: " + StringUtils.join(cmd.getAliases(), ", ") : ""));
                            return;
                        }

                    }

                }
            }

            sendMsg(event, "That command could not be found, try " + Variables.PREFIX + "help for a list of commands");
            return;
        }

        List<String> dannyPhantomCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c -> c.getCategory().equals(Category.AUDIO)).map(Command::getName).collect(Collectors.toList());
        List<String> imageCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c -> c.getCategory().equals(Category.IMAGE)).map(Command::getName).collect(Collectors.toList());
        List<String> wikiCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c -> c.getCategory().equals(Category.WIKI)).map(Command::getName).collect(Collectors.toList());
        List<String> otherCommands = SpoopyUtils.commandManager.getCommands()
                .stream().filter(c -> c.getCategory().equals(Category.NONE)).map(Command::getName).collect(Collectors.toList());

        MessageEmbed helpEmbed = EmbedUtils.defaultEmbed()
                .setDescription("Use `" + Variables.PREFIX + "help [command]` for more info about a command")
                .addField("Audio commands",
                        "`" + Variables.PREFIX + StringUtils.join(dannyPhantomCommands, "`\n`" + Variables.PREFIX) + "`", false)
                .addField("Image commands",
                        "`" + Variables.PREFIX + StringUtils.join(imageCommands, "`\n`" + Variables.PREFIX) + "`", false)
                .addField("Wiki commands",
                        "`" + Variables.PREFIX + StringUtils.join(wikiCommands, "`\n`" + Variables.PREFIX) + "`", false)
                .addField("Other commands", "`" + Variables.PREFIX + StringUtils.join(otherCommands, "`\n`" + Variables.PREFIX) + "`", false)
                .build();

        event.getAuthor().openPrivateChannel().queue(pc -> pc.sendMessage(helpEmbed).queue(
                msg -> sendMsg(event, event.getAuthor().getAsMention() + ", check your dms"),
                error -> sendEmbed(event, helpEmbed)
                ),
                error -> sendEmbed(event, helpEmbed)
        );
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows a list of all the commands";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"commands"};
    }
}
