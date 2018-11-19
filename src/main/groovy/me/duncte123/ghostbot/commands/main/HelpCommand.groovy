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

package me.duncte123.ghostbot.commands.main

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import java.util.regex.Pattern

class HelpCommand extends Command {
    @Override
    void execute(CommandEvent commandEvent) {

        def args = commandEvent.args
        def event = commandEvent.event

        if (args.length > 0) {
            String toSearch = args.join(' ')

            if (toSearch.startsWith(Variables.PREFIX)) {
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.PREFIX), '')
            }

            if (toSearch.startsWith(Variables.OTHER_PREFIX)) {
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.OTHER_PREFIX), '')
            }

            for (Command cmd : SpoopyUtils.commandManager.commands) {
                if (cmd.name == toSearch) {
                    sendMessage(event, "Command help for `$cmd.name` :\n" +
                        "$cmd.help${cmd.aliases.length > 0 ? "\nAliases: ${cmd.aliases.join(', ')}" : ''}")
                    return
                } else {
                    for (String alias : cmd.aliases) {
                        if (alias == toSearch) {
                            sendMessage(event, "Command help for `$cmd.name` :\n" +
                                "$cmd.help${cmd.aliases.length > 0 ? "\nAliases: ${cmd.aliases.join(', ')}" : ''}")
                            return
                        }

                    }
                }
            }

            sendMessage(event, "That command could not be found, try ${Variables.PREFIX}help for a list of commands")
            return
        }


        def spaceCommands = getCommandsForCategory(CommandCategory.SPACE)
        def audioCommands = getCommandsForCategory(CommandCategory.AUDIO)
        def imageCommands = getCommandsForCategory(CommandCategory.IMAGE)
        def wikiCommands = getCommandsForCategory(CommandCategory.WIKI)
        def textCommands = getCommandsForCategory(CommandCategory.TEXT)
        def otherCommands = getCommandsForCategory(CommandCategory.NONE)
        def characterCommands = getCommandsForCategory(CommandCategory.CHARACTERS)


        def helpEmbed = EmbedUtils.defaultEmbed()
            .setDescription("Use `${Variables.PREFIX}help [command]` for more info about a command")
            .addField('Commands for all you space nerds', buildCommands(spaceCommands), false)
            .addField('Audio commands', buildCommands(audioCommands), false)
            .addField('Image commands', buildCommands(imageCommands), false)
            .addField('Text commands', buildCommands(textCommands), false)
            .addField('Wiki commands', buildCommands(wikiCommands), false)
            .addField('Other commands', buildCommands(otherCommands), false)
            .addField('Character commands', buildCommands(characterCommands), false)

        if (event.fromSlack) {
            sendMessage(event, helpEmbed)
            return
        }

        def jdaEvent = event.originalEvent as GuildMessageReceivedEvent

        jdaEvent.author.openPrivateChannel().queue({
            it.sendMessage(helpEmbed.build()).queue({
                sendMessage(event, "$jdaEvent.author.asMention, check your dms")
            }, {
                sendMessage(event, helpEmbed)
            })
        }, {
            sendMessage(event, helpEmbed)
        })

    }

    @Override
    String getName() { 'help' }

    @Override
    String getHelp() { 'Shows a list of all the commands' }

    @Override
    String[] getAliases() { ['commands'] }

    private static String buildCommands(List<String> commands) {
        return "`${Variables.PREFIX + commands.join("`, `${Variables.PREFIX}")}`"
    }

    private static List<String> getCommandsForCategory(CommandCategory commandCategory) {

        def manager = SpoopyUtils.commandManager

        def temp = manager.commands.stream()
            .filter { (it.category == commandCategory) }
            .map { it.name }.collect()

        manager.commands.stream()
            .filter { (it.category == commandCategory) }
            .map { it.aliases }.forEach(temp.&addAll)

        return temp
    }

    @Override
    boolean isSlackCompatible() { true }
}
