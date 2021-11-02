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

package me.duncte123.ghostbot.commands.main;

import groovy.lang.GroovyShell;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static me.duncte123.botcommons.messaging.MessageUtils.*;

public class EvalCommand extends Command {
    private final GroovyShell engine = new GroovyShell();
    private final ExecutorService service = Executors.newCachedThreadPool((it) -> new Thread(it, "Eval-Thread"));
    private final List<String> packageImports = List.of(
        "java.io",
        "java.lang",
        "java.util",
        "net.dv8tion.jda.api",
        "net.dv8tion.jda.api.entities",
        "net.dv8tion.jda.api.entities.impl",
        "net.dv8tion.jda.api.managers",
        "net.dv8tion.jda.api.managers.impl",
        "net.dv8tion.jda.api.utils",
        "me.duncte123.ghostBot.utils",
        "me.duncte123.ghostBot.commands.main",
        "me.duncte123.ghostBot.commands.dannyPhantom",
        "me.duncte123.ghostBot"
    );

    @Override
    public void execute(ICommandEvent event) {

        if (event.getAuthor().getIdLong() != Variables.OWNER_ID) {
            return;
        }

        // TODO: MULTILINE WHEN
        if (event.isSlash()) {
            event.reply("Does not support slash");
            return;
        }

        final Message message = event.getMessage();

        if (event.getArgs().isEmpty()) {
            sendSuccess(message);
            return;
        }

        engine.setVariable("event", event);
        engine.setVariable("jda", event.getJDA());
        engine.setVariable("channel", event.getChannel());
        engine.setVariable("author", event.getAuthor());
        engine.setVariable("guild", event.getGuild());
        engine.setVariable("args", event.getArgs());

        final String script = String.format(
            "import %s.*\n\n%s",
            String.join(".*\nimport ", packageImports),
            message.getContentRaw().split("\\s+", 2)[1]
        );

        try {
            service.submit(() -> {
                final Object result = engine.evaluate(script);

                if (result != null) {
                    sendMsg(event, result.toString());
                }

                sendSuccess(message);
            }).get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            try {
                sendErrorWithMessage(message, e.getCause().toString());
            } catch (NullPointerException ignored) {
                sendErrorWithMessage(message, e.toString());
            }
        } finally {
            engine.getContext().getVariables().clear();
        }
    }

    @Override
    public String getName() { return "eval"; }

    @Override
    public String getHelp() { return "evaluate groovy code"; }

    @Override
    public CommandCategory getCategory() { return CommandCategory.HIDDEN; }
}
