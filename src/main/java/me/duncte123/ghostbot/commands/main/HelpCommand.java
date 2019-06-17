/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.commands.main;

import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class HelpCommand extends Command {
    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();
        final CommandManager manager = event.getContainer().getCommandManager();

        if (args.size() > 0) {
            String toSearch = String.join(" ", args);

            if (toSearch.startsWith(Variables.PREFIX)) {
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.PREFIX), "");
            }

            if (toSearch.startsWith(Variables.OTHER_PREFIX)) {
                toSearch = toSearch.replaceFirst(Pattern.quote(Variables.OTHER_PREFIX), "");
            }

            final Command cmd = manager.getCommand(toSearch);

            if (cmd != null) {
                sendMsg(event, String.format(
                    "Command help for `%s`:%n%s%s",
                    cmd.getName(),
                    cmd.getHelp(),
                    cmd.getAliases().isEmpty() ? "" : "\nAliases: " + String.join(", ", cmd.getAliases())
                ));

                return;
            }
            sendMsg(event, "That command could not be found, try " + Variables.PREFIX + "help for a list of commands");

            return;
        }

        final List<String> spaceCommands = getCommandsForCategory(CommandCategory.SPACE, manager);
        final List<String> audioCommands = getCommandsForCategory(CommandCategory.AUDIO, manager);
        final List<String> imageCommands = getCommandsForCategory(CommandCategory.IMAGE, manager);
        final List<String> wikiCommands = getCommandsForCategory(CommandCategory.WIKI, manager);
        final List<String> textCommands = getCommandsForCategory(CommandCategory.TEXT, manager);
        final List<String> otherCommands = getCommandsForCategory(CommandCategory.NONE, manager);
        final List<String> characterCommands = getCommandsForCategory(CommandCategory.CHARACTERS, manager);

        final EmbedBuilder helpEmbed = EmbedUtils.defaultEmbed()
            .setDescription("Use `" + Variables.PREFIX + "help [command]` for more info about a command")
            .addField("Commands for all you space nerds", buildCommands(spaceCommands), false)
            .addField("Audio commands", buildCommands(audioCommands), false)
            .addField("Image commands", buildCommands(imageCommands), false)
            .addField("Text commands", buildCommands(textCommands), false)
            .addField("Wiki commands", buildCommands(wikiCommands), false)
            .addField("Other commands", buildCommands(otherCommands), false)
            .addField("Character commands", buildCommands(characterCommands), false);

        event.getAuthor().openPrivateChannel().queue((it) ->
            it.sendMessage(helpEmbed.build()).queue(
                (m) -> sendMsg(event, event.getAuthor().getAsMention() + ", check your dms"),
                (e) -> sendEmbed(event, helpEmbed)
            ), (e) -> sendEmbed(event, helpEmbed)
        );

    }

    @Override
    public String getName() { return "help"; }

    @Override
    public String getHelp() { return "Shows a list of all the commands"; }

    @Override
    public List<String> getAliases() { return List.of("commands"); }

    private static String buildCommands(List<String> commands) {
        return String.format("`%s%s`", Variables.PREFIX, String.join("`, `" + Variables.PREFIX, commands));
    }

    private static List<String> getCommandsForCategory(CommandCategory commandCategory, CommandManager manager) {
        final List<String> temp = manager.getCommands().stream()
            .filter((it) -> it.getCategory() == commandCategory)
            .map(Command::getName).collect(Collectors.toList());

        manager.getCommands().stream()
            .filter((it) -> it.getCategory() == commandCategory)
            .map(Command::getAliases).forEach(temp::addAll);

        return temp;
    }
}
