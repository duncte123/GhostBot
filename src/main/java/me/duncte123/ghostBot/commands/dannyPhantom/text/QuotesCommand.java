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

package me.duncte123.ghostBot.commands.dannyPhantom.text;

import com.afollestad.ason.Ason;
import com.afollestad.ason.AsonArray;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.utils.WebUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.regex.Pattern;

public class QuotesCommand extends Command {

    private final String[] types = {"chat", "text", "quote"};

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        try {
            String type = types[SpoopyUtils.random.nextInt(types.length)];
            Ason ason = WebUtils.getAson(
                    String.format(
                            "https://api.tumblr.com/v2/blog/totallycorrectdannyphantomquotes.tumblr.com/posts?api_key=%s&type=%s",
                            SpoopyUtils.config.getString("api.tumblr", "API_KEY"),
                            type
                    )
            );
            AsonArray<Ason> posts = ason.getJsonArray("response.posts");
            Ason selectedPost = posts.getJsonObject(SpoopyUtils.random.nextInt(posts.size()));
            assert selectedPost != null;

            EmbedBuilder eb = EmbedUtils.defaultEmbed()
                    .setTitle("Link to Post", selectedPost.getString("short_url"));

            switch (type) {
                case "chat" :
                    AsonArray<Ason> dialogue = selectedPost.getJsonArray("dialogue");
                    dialogue.forEach(
                            a -> eb.appendDescription("**").appendDescription(a.getString("label"))
                            .appendDescription("** ")
                                    .appendDescription(StringEscapeUtils.unescapeHtml4(a.getString("phrase")))
                                    .appendDescription("\n")
                    );
                    break;
                case "text" :
                    String bodyRaw = selectedPost.getString("body", "");
                    String bodyParsed = bodyRaw.replaceAll(Pattern.quote("<p>"), "")
                            .replaceAll("\\*", "\\\\*")
                            .replaceAll(Pattern.quote("</p>"), "")
                            .replaceAll(Pattern.quote("<b>"), "**")
                            .replaceAll(Pattern.quote("</b>"), "**");
                    eb.setDescription(StringEscapeUtils.unescapeHtml4(bodyParsed));
                    break;
                case "quote" :
                    String text = StringEscapeUtils.unescapeHtml4(selectedPost.getString("text"));
                    String source = selectedPost.getString("source");
                    eb.setDescription("\"" + text + "\"");
                    eb.appendDescription("\n\n - _ " + source + "_");
                    break;
            }

            MessageUtils.sendEmbed(event, eb.build());

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "quote";
    }

    @Override
    public String getHelp() {
        return "Get a random quote from http://totallycorrectdannyphantomquotes.tumblr.com/";
    }
}
