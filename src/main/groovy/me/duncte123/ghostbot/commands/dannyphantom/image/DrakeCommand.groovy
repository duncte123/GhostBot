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

package me.duncte123.ghostbot.commands.dannyphantom.image

import me.duncte123.botcommons.web.WebUtils
import me.duncte123.botcommons.web.WebUtilsErrorUtils
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.utils.IOUtil
import okhttp3.RequestBody
import org.json.JSONObject

import java.util.function.Consumer

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg
import static me.duncte123.botcommons.web.WebUtils.EncodingType.APPLICATION_JSON

class DrakeCommand extends ImageBase {
    @Override
    void execute(CommandEvent event) {

        def args = event.args

        if (!event.selfMember.hasPermission(event.channel, Permission.MESSAGE_ATTACH_FILES)) {
            sendMsg(event.event, 'I need permission to upload files to this channel')
            return
        }

        if (args.length == 0) {
            sendMsg(event.event, "Missing arguments, usage: `$Variables.PREFIX$name <top text>|<bottom text>`")
            return
        }

        def split = args.join(' ').split('\\|')

        if (split.length < 2 || split[0].empty || split[1].empty) {
            sendMsg(event.event, "Missing arguments, usage: `$Variables.PREFIX$name <top text>|<bottom text>`")
            return
        }

        def shouldDab = event.invoke.equalsIgnoreCase('ddrake')

        genDanny(split[0], split[1], shouldDab) {
            sendImage(event, it)
        }
    }

    @Override
    String getName() { 'drake' }

    @Override
    String[] getAliases() {[
        'ddrake'
    ]}

    @Override
    String getHelp() { """Generates a drake meme with Danny
Usage: `$Variables.PREFIX$name <top text>|<bottom text>`""" }

    private static void genDanny(String top, String bottom, boolean dabbing, Consumer<byte[]> callback) {
        def json = new JSONObject().put('top', top).put('bottom', bottom).put('dabbing', dabbing)
        def body = RequestBody.create(null, json.toString())

        def request = WebUtils.defaultRequest()
            .url('https://apis.duncte123.me/memes/dannyphantomdrake')
            .post(body)
            .addHeader('Authorization', SpoopyUtils.config.api_token)
            .addHeader('Content-Type', APPLICATION_JSON.type)

        WebUtils.ins.prepareRaw(request.build(), {
            IOUtil.readFully(WebUtilsErrorUtils.getInputStream(it))
        }).async(callback)
    }
}
