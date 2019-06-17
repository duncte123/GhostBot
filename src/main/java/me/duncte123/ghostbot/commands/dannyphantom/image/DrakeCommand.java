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

package me.duncte123.ghostbot.commands.dannyphantom.image;

import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.botcommons.web.WebUtilsErrorUtils;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.IOUtil;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static me.duncte123.botcommons.web.WebUtils.EncodingType.APPLICATION_JSON;

public class DrakeCommand extends ImageBase {
    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();

        if (!event.getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_ATTACH_FILES)) {
            sendMsg(event, "I need permission to upload files to this channel");

            return;
        }

        if (args.isEmpty()) {
            sendMsg(event, "Missing arguments, usage: `"+ Variables.PREFIX+getName() +" <top text>|<bottom text>`");

            return;
        }

        final String[] split = String.join(" ", args).split("\\|");

        if (split.length < 2 || split[0].isEmpty() || split[1].isEmpty()) {
            sendMsg(event, "Missing arguments, usage: `"+ Variables.PREFIX+getName() +" <top text>|<bottom text>`");

            return;
        }

        final boolean shouldDab = event.getInvoke().equalsIgnoreCase("ddrake");

        genDanny(split[0], split[1], shouldDab, event.getContainer().getConfig(), (it) -> sendImage(event, it));
    }

    @Override
    public String getName() { return "drake"; }

    @Override
    public List<String> getAliases() {
        return List.of("ddrake");
    }

    @Override
    public String getHelp() {
        return "Generates a drake meme with Danny\n" +
            "Usage: `"+ Variables.PREFIX+getName() +" <top text>|<bottom text>`";
    }

    private static void genDanny(String top, String bottom, boolean dabbing, GhostBotConfig config, Consumer<byte[]> callback) {
        final JSONObject json = new JSONObject().put("top", top).put("bottom", bottom).put("dabbing", dabbing);
        final RequestBody body = RequestBody.create(null, json.toString());

        final Request.Builder request = WebUtils.defaultRequest()
            .url("https://apis.duncte123.me/memes/dannyphantomdrake")
            .post(body)
            .addHeader("Authorization", config.api_token)
            .addHeader("Content-Type", APPLICATION_JSON.getType());

        WebUtils.ins.prepareRaw(request.build(),
            (it) -> IOUtil.readFully(WebUtilsErrorUtils.getInputStream(it))
        ).async(callback);
    }
}
