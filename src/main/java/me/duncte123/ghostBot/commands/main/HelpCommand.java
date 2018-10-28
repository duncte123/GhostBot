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

import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class HelpCommand extends Command {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            String toSearch = StringUtils.join(args, " ");
            if (toSearch.startsWith(Variables.PREFIX))
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.PREFIX), "");

            for (Command cmd : SpoopyUtils.getCommandManager().getCommands()) {
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

        List<String> audioCommands = getCommandsForCategory(CommandCategory.AUDIO);
        List<String> imageCommands = getCommandsForCategory(CommandCategory.IMAGE);
        List<String> wikiCommands = getCommandsForCategory(CommandCategory.WIKI);
        List<String> textCommands = getCommandsForCategory(CommandCategory.TEXT);
        List<String> otherCommands = getCommandsForCategory(CommandCategory.NONE);
        List<String> characterCommands = getCommandsForCategory(CommandCategory.CHARACTERS);

        MessageEmbed helpEmbed = EmbedUtils.defaultEmbed()
                .setDescription("Use `" + Variables.PREFIX + "help [command]` for more info about a command")
                .addField("Audio commands", buildCcmmands(audioCommands), false)
                .addField("Image commands", buildCcmmands(imageCommands), false)
                .addField("Text commands", buildCcmmands(textCommands), false)
                .addField("Wiki commands", buildCcmmands(wikiCommands), false)
                .addField("Other commands", buildCcmmands(otherCommands), false)
                .addField("Character commands", buildCcmmands(characterCommands), false)
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

    private String buildCcmmands(List<String> commands) {
        return "`" + Variables.PREFIX + StringUtils.join(commands, "`, `" + Variables.PREFIX) + "`";
    }

    private List<String> getCommandsForCategory(CommandCategory commandCategory) {

        CommandManager manager = SpoopyUtils.getCommandManager();

        List<String> temp = manager.getCommands()
                .stream().filter(c -> c.getCategory().equals(commandCategory)).map(Command::getName).collect(Collectors.toList());
        manager.getCommands()
                .stream().filter(c -> c.getCategory().equals(commandCategory))
                .map(cmd -> Arrays.asList(cmd.getAliases())).forEach(temp::addAll);

        return temp;
    }
}
