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

package me.duncte123.ghostBot.commands.dannyPhantom.image;

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class GifCommand extends Command {

    private final String url = "https://www.googleapis.com/customsearch/v1" +
            "?q=%s&cx=012048784535646064391:v-fxkttbw54&hl=en&searchType=image&fileType=gif&safe=off&key=" + SpoopyUtils.config.getString("api.google");

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        sendMsg(event, "Loading....", msg -> {
            String keyword = "Danny Phantom gif";
            try {
                WebUtils.ins.getAson(String.format(url, keyword.replaceAll(" ", "+"))).async(data -> {
                    AsonArray<Ason> arr = data.getJsonArray("items");
                    if (arr.size() == 0) {
                        execute(invoke, args, event);
                        return;
                    }
                    Ason randomItem = arr.getJsonObject(SpoopyUtils.random.nextInt(arr.size()));
                    assert randomItem != null;
                    msg.editMessage(new MessageBuilder()
                            .setEmbed(EmbedUtils.defaultEmbed()
                                    .setTitle(randomItem.getString("title"), randomItem.getString("image.contextLink"))
                                    .setImage(randomItem.getString("link")).build()).build())
                            .override(true)
                            .queue();
                }, er -> MessageUtils.sendMsg(event, "Error while looking up image: " + er));
            } catch (NullPointerException e) {
                e.printStackTrace();
                msg.editMessage("Something went wrong while looking up the image").queue();
            }
        });
    }

    @Override
    public String getName() {
        return "gif";
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom gif";
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }
}
