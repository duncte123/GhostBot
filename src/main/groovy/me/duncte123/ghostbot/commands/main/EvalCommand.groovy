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
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static me.duncte123.botcommons.messaging.MessageUtils.*

class EvalCommand extends Command {

    private final GroovyShell engine = new GroovyShell()
    private final ExecutorService service = Executors.newCachedThreadPool { new Thread(it, 'Eval-Thread') }
    private final List<String> packageImports = [
        'java.io',
        'java.lang',
        'java.util',
        'net.dv8tion.jda.core',
        'net.dv8tion.jda.core.entities',
        'net.dv8tion.jda.core.entities.impl',
        'net.dv8tion.jda.core.managers',
        'net.dv8tion.jda.core.managers.impl',
        'net.dv8tion.jda.core.utils',
        'me.duncte123.ghostBot.utils',
        'me.duncte123.ghostBot.commands.main',
        'me.duncte123.ghostBot.commands.dannyPhantom',
        'me.duncte123.ghostBot'
    ]

    @Override
    void execute(CommandEvent event) {

        if (event.author.idLong != Variables.OWNER_ID) {
            return
        }

        def message = event.message

        if (event.args.length == 0) {
            sendSuccess(message)
            return
        }

        engine.setVariable('event', event)
        engine.setVariable('jda', event.api)
        engine.setVariable('channel', event.channel)
        engine.setVariable('author', event.author)
        engine.setVariable('guild', event.guild)
        engine.setVariable('args', event.args)

        final def script = "import ${packageImports.join('.*\nimport ')}.*\n\n" +
            message.contentRaw.split('\\s+', 2)[1]

        try {
            service.submit {
                def result = engine.evaluate(script)

                if (result != null) {
                    sendMsg(event.event, result.toString())
                }

                sendSuccess(message)
            }.get(1, TimeUnit.MINUTES)


        } catch (Exception e) {
            try {
                sendErrorWithMessage(message, e.cause.toString())
            } catch (NullPointerException ignored) {
                sendErrorWithMessage(message, e.toString())
            }

        }

    }

    @Override
    String getName() { 'eval' }

    @Override
    String getHelp() { 'evaluate groovy code' }

    @Override
    CommandCategory getCategory() { CommandCategory.HIDDEN }
}
