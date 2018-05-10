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

package me.duncte123.ghostBot.commands

import me.duncte123.ghostBot.objects.Command
import me.duncte123.ghostBot.objects.CommandCategory
import me.duncte123.ghostBot.utils.EmbedUtils
import me.duncte123.ghostBot.utils.SpoopyUtils
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.apache.commons.lang3.StringUtils

import java.util.regex.Pattern

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg

class HelpCommand extends Command {

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            String toSearch = StringUtils.join(args, " ")
            if (toSearch.startsWith(Variables.PREFIX))
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.PREFIX), "")

            for (Command cmd : (SpoopyUtils.COMMAND_MANAGER.commands)) {
                if (cmd.getName() == toSearch) {
                    sendMsg(event, "Command help for `$cmd.name` :\n$cmd.help${cmd.aliases.length > 0 ? "\nAliases: " + cmd.aliases.join(", ") : ""}")
                    return
                } else {
                    for (String alias : cmd.getAliases()) {
                        if (alias == toSearch) {
                            sendMsg(event, "Command help for `$cmd.name` :\n$cmd.help${cmd.aliases.length > 0 ? "\nAliases: " + cmd.aliases.join(", ") : ""}")
                            return
                        }

                    }

                }
            }

            sendMsg(event, "That command could not be found, try ${Variables.PREFIX}help for a list of commands")
            return
        }

        List<String> audioCommands = getCommandsForCategory(CommandCategory.AUDIO)
        List<String> imageCommands = getCommandsForCategory(CommandCategory.IMAGE)
        List<String> wikiCommands = getCommandsForCategory(CommandCategory.WIKI)
        List<String> textCommands = getCommandsForCategory(CommandCategory.TEXT)
        List<String> otherCommands = getCommandsForCategory(CommandCategory.NONE)

        MessageEmbed helpEmbed = EmbedUtils.defaultEmbed()
                .setDescription("Use `${Variables.PREFIX}help [command]` for more info about a command")
                .addField("Audio commands",
                "`$Variables.PREFIX${audioCommands.join("`\n`$Variables.PREFIX")}`", false)
                .addField("Image commands",
                "`$Variables.PREFIX${imageCommands.join("`\n`$Variables.PREFIX")}`", false)
                .addField("Text commands",
                "`$Variables.PREFIX${textCommands.join("`\n`$Variables.PREFIX")}`", false)
                .addField("Wiki commands",
                "`$Variables.PREFIX${wikiCommands.join("`\n`$Variables.PREFIX")}`", false)
                .addField("Other commands",
                "`$Variables.PREFIX${otherCommands.join("`\n`$Variables.PREFIX")}`", false)
                .build()

        event.author.openPrivateChannel().queue ({
            it.sendMessage(helpEmbed).queue(
                    {msg -> sendMsg(event, event.author.asMention + ", check your dms")},
                    {error -> sendEmbed(event, helpEmbed)}
            )},
                {error -> sendEmbed(event, helpEmbed)
        })
    }

    @Override
    String getName() {
        return "help"
    }

    @Override
    String getHelp() {
        return "Shows a list of all the commands"
    }

    @Override
    String[] getAliases() {
        return ["commands"]
    }

    private static List<String> getCommandsForCategory(CommandCategory category) {

        List<String> temp = SpoopyUtils.COMMAND_MANAGER.commands
                .stream().filter{ it.category == category }.map{it.name}.collect().toList() as List<String>

        SpoopyUtils.COMMAND_MANAGER.commands
                .stream().filter{ it.category == category }
                .map{ List.of(it.aliases) }.forEach(temp.&addAll)

        return temp
    }
    
}
