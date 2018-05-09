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

import me.duncte123.ghostBot.objects.Category_java;
import me.duncte123.ghostBot.objects.Command_java;
import me.duncte123.ghostBot.utils.EmbedUtils_java;
import me.duncte123.ghostBot.utils.SpoopyUtils_java;
import me.duncte123.ghostBot.variables.Variables_java;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.duncte123.ghostBot.utils.MessageUtils_java.sendEmbed;
import static me.duncte123.ghostBot.utils.MessageUtils_java.sendMsg;

public class HelpCommandJava extends Command_java {
    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            String toSearch = StringUtils.join(args, " ");
            if (toSearch.startsWith(Variables_java.PREFIX))
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables_java.PREFIX), "");

            for (Command_java cmd : SpoopyUtils_java.commandManager.getCommands()) {
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

            sendMsg(event, "That command could not be found, try " + Variables_java.PREFIX + "help for a list of commands");
            return;
        }

        List<String> audioCommands = getCommandsForCategory(Category_java.AUDIO);
        List<String> imageCommands = getCommandsForCategory(Category_java.IMAGE);
        List<String> wikiCommands = getCommandsForCategory(Category_java.WIKI);
        List<String> textCommands = getCommandsForCategory(Category_java.TEXT);
        List<String> otherCommands = getCommandsForCategory(Category_java.NONE);

        MessageEmbed helpEmbed = EmbedUtils_java.defaultEmbed()
                .setDescription("Use `" + Variables_java.PREFIX + "help [command]` for more info about a command")
                .addField("Audio commands",
                        "`" + Variables_java.PREFIX + StringUtils.join(audioCommands, "`\n`" + Variables_java.PREFIX) + "`", false)
                .addField("Image commands",
                        "`" + Variables_java.PREFIX + StringUtils.join(imageCommands, "`\n`" + Variables_java.PREFIX) + "`", false)
                .addField("Text commands",
                        "`" + Variables_java.PREFIX + StringUtils.join(textCommands, "`\n`" + Variables_java.PREFIX) + "`", false)
                .addField("Wiki commands",
                        "`" + Variables_java.PREFIX + StringUtils.join(wikiCommands, "`\n`" + Variables_java.PREFIX) + "`", false)
                .addField("Other commands", "`" + Variables_java.PREFIX + StringUtils.join(otherCommands, "`\n`" + Variables_java.PREFIX) + "`", false)
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

    private List<String> getCommandsForCategory(Category_java category) {

        List<String> temp = SpoopyUtils_java.commandManager.getCommands()
                .stream().filter(c -> c.getCategory().equals(category)).map(Command_java::getName).collect(Collectors.toList());
        SpoopyUtils_java.commandManager.getCommands()
                .stream().filter(c -> c.getCategory().equals(category) )
                .map(cmd -> List.of(cmd.getAliases())).forEach(temp::addAll);

        return temp;
    }
}
