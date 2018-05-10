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

package me.duncte123.ghostBot.commands.ownerOnly

import me.duncte123.ghostBot.objects.Command
import me.duncte123.ghostBot.objects.CommandCategory
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import java.util.concurrent.*

import static me.duncte123.ghostBot.utils.MessageUtils.*

class EvalCommand extends Command {

    private final GroovyShell shell = new GroovyShell()
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1,
            { new Thread(it, "Eval-Thread") })
    private final def packageImports = List.of(
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
            "me.duncte123.ghostBot.commands",
            "me.duncte123.ghostBot.commands.dannyPhantom",
            "me.duncte123.ghostBot.commands.ownerOnly",
            "me.duncte123.ghostBot")

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if(event.author.id == Variables.OWNER_ID) {
            shell.setVariable("event", event)
            shell.setVariable("jda", event.JDA)
            shell.setVariable("channel", event.channel)
            shell.setVariable("member", event.member)
            shell.setVariable("author", event.author)
            shell.setVariable("guild", event.guild)
            shell.setVariable("args", args)

            def script = "import ${packageImports.join(".*\nimport ")}.*\n\n${args.join(" ")}"
            println script

            try {
                ScheduledFuture<Object> task = service.schedule((Callable){ shell.evaluate(script) },
                        0L, TimeUnit.NANOSECONDS)

                Object result = task.get(1, TimeUnit.MINUTES)
                //cancel it after 1 minute
                //service.schedule({ task.cancel(true) }, 60, TimeUnit.SECONDS)

                println result.toString()

                if(result != null)
                    sendMsg(event, result.toString())
            } catch (Exception e) {
                try {
                    sendErrorWithMessage(event.message, e.cause.toString())
                }
                catch (NullPointerException ignored) {
                    sendErrorWithMessage(event.message, e.toString())
                }
            }
            sendSuccess(event.message)

        }

    }

    @Override
    String getName() {
        return "eval"
    }

    @Override
    String getHelp() {
        return "evaluate groovy code"
    }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.HIDDEN
    }
}
