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
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.utils.WebUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;

public class ImageCommand extends Command {

    private final String url = "https://www.googleapis.com/customsearch/v1" +
            "?q=%s&cx=012048784535646064391:v-fxkttbw54&hl=en&searchType=image&key=" + SpoopyUtils.config.getString("api.google");
    private final String[] keywords = {
            "Danny Phantom",
            "Pitch Pearl",
            "Pitch Pearl",
            "Danny Fenton",
            "Samantha Manson",
            "Sam Manson",
            "Tucker Foley",
            "Jack Fenton",
            "Maddy Fenton",
            "Jazz Fenton",
            "Vlad Plasmius",
            /*"Danny Fenton (Danny Phantom)",
            "Sam Manson (Danny Phantom)",
            "Tucker Foley (Danny Phantom)",
            "Jack Fenton (Danny Phantom)",
            "Maddy Fenton (Danny Phantom)",
            "Jazz Fenton (Danny Phantom)",
            "Vlad Masters (Danny Phantom)",
            "Vlad Plasmius (Danny Phantom)",*/
            "Danny Fenton"
    };

    private final String[] pornKeywords = {
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "porn",
            "yaoi",
            "naked",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        String keyword = keywords[SpoopyUtils.random.nextInt(keywords.length)];
        /* if(event.getChannel().isNSFW()) {
            String selected = pornKeywords[SpoopyUtils.random.nextInt(pornKeywords.length)];
            keyword += !"".equals(selected) ? " " + selected : "";
        }*/
        try {
            Ason data = WebUtils.getAson(String.format(url, keyword.replaceAll(" ", "+")));
            AsonArray<Ason> arr = data.getJsonArray("items");
            if (arr.size() == 0) {
                execute(invoke, args, event);
                return;
            }
            Ason randomItem = arr.getJsonObject(SpoopyUtils.random.nextInt(arr.size()));
            sendMsg(event, new MessageBuilder()
                    .append("Keyword: ")
                    .append(keyword)
                    .setEmbed(EmbedUtils.defaultEmbed()
                            .setTitle(randomItem.getString("title"), randomItem.getString("image.contextLink"))
                            .setImage(randomItem.getString("link")).build()).build());
            /*sendEmbed(event,
                    EmbedUtils.defaultEmbed()
                            .setTitle(randomItem.getString("title"), randomItem.getString("image.contextLink"))
                            .setImage(randomItem.getString("link")).build()
            );*/
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            sendMsg(event, "Something went wrong while looking up the image");
        }
    }

    @Override
    public String getName() {
        return "image";
    }

    @Override
    public Category getCategory() {
        return Category.IMAGE;
    }

    @Override
    public String getHelp() {
        return "Gives you a random Danny Phantom <:DPEmblemInvertStroke:402746292788264960> related image from google";
    }
}