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
import me.duncte123.ghostBot.objects.CommandCategory;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static me.duncte123.botcommons.messaging.MessageUtils.*;

public class EvalCommand extends Command {

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
    private final ExecutorService service = Executors.newCachedThreadPool((it) -> new Thread(it, "Eval-Thread"));
    private final List<String> packageImports = Arrays.asList(
            "java.io",
            "java.lang",
            "java.util",
            "net.dv8tion.jda.core",
            "net.dv8tion.jda.core.entities",
            "net.dv8tion.jda.core.entities.impl",
            "net.dv8tion.jda.core.managers",
            "net.dv8tion.jda.core.managers.impl",
            "net.dv8tion.jda.core.utils",
            "me.duncte123.ghostBot.utils",
            "me.duncte123.ghostBot.commands.main",
            "me.duncte123.ghostBot.commands.dannyPhantom",
            "me.duncte123.ghostBot"
    );

    @Override
    public void execute(String invoke, final String[] args, GuildMessageReceivedEvent event) {

        if (event.getAuthor().getIdLong() != Variables.OWNER_ID) {
            return;
        }

        engine.put("event", event);
        engine.put("jda", event.getJDA());
        engine.put("channel", event.getChannel());
        engine.put("member", event.getMember());
        engine.put("author", event.getAuthor());
        engine.put("guild", event.getGuild());
        engine.put("args", args);

        final String script = "import " + String.join(".*\nimport ", packageImports) + ".*\n\n" +
                event.getMessage().getContentRaw().split("\\s+", 2)[1];

        try {
            Future<Object> task = service.submit(() -> engine.eval(script));

            Object result = task.get(1, TimeUnit.MINUTES);

            if (result != null) sendMsg(event, result.toString());

            sendSuccess(event.getMessage());
        } catch (Exception e) {
            try {
                sendErrorWithMessage(event.getMessage(), e.getCause().toString());
            } catch (NullPointerException ignored) {
                sendErrorWithMessage(event.getMessage(), e.toString());
            }

        }
    }

    @Override
    public String getName() {
        return "eval";
    }

    @Override
    public String getHelp() {
        return "evaluate groovy code";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.HIDDEN;
    }
}
