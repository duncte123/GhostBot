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

import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl

import javax.script.ScriptEngine
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static me.duncte123.botcommons.messaging.MessageUtils.sendErrorWithMessage
import static me.duncte123.botcommons.messaging.MessageUtils.sendErrorWithMessage
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg
import static me.duncte123.botcommons.messaging.MessageUtils.sendSuccess

class EvalCommand implements Command {

    private final ScriptEngine engine = new GroovyScriptEngineImpl()
    private final ExecutorService service = Executors.newCachedThreadPool { new Thread(it, "Eval-Thread") }
    private final List<String> packageImports = [
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
    ]

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (event.author.idLong != Variables.OWNER_ID) {
            return
        }

        engine.put("event", event)
        engine.put("jda", event.JDA)
        engine.put("channel", event.channel)
        engine.put("member", event.member)
        engine.put("author", event.author)
        engine.put("guild", event.guild)
        engine.put("args", args)

        final def script = "import ${packageImports.join(".*\nimport ")}.*\n\n" +
                event.message.contentRaw.split("\\s+", 2)[1]

        try {
            def task = service.submit {
                return engine.eval(script)
            }

            def result = task.get(1, TimeUnit.MINUTES)

            if (result != null) sendMsg(event, result.toString())

            sendSuccess(event.message)
        } catch (Exception e) {
            try {
                sendErrorWithMessage(event.message, e.cause.toString())
            } catch (NullPointerException ignored) {
                sendErrorWithMessage(event.message, e.toString())
            }

        }

    }

    @Override
    String getName() { "eval" }

    @Override
    String getHelp() { "evaluate groovy code" }

    @Override
    CommandCategory getCategory() { CommandCategory.HIDDEN }
}
