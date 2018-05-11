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
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.commands.dannyPhantom.text.QuotesCommand;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed;
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class DPArtistsCommand extends Command {
    /*
    http://earthphantom.tumblr.com/ (approved)
    http://amethystocean-adr.tumblr.com/

    https://allyphantomrush.deviantart.com/

    http://deadlandsqueen.tumblr.com/
    http://askfentonworks.tumblr.com/
    http://thickerthanectoplasm.tumblr.com/
    http://umbrihearts.tumblr.com/

    needed things
    URL: link of the artist page
    Type: Website where the data is from

    Obtaining data:
    Arrays.toString("URL".replaceAll("https?://", "").split("\\."))
    the array should be
    [0] = URL
    [1] = Type
    (and some more useless info)
*/

    private final List<String> artits = List.of(
            "earthphantom"
    );

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {
        if (args.length < 1) {
            sendMsg(event, "Correct usage is `gb." + getName() + " <artist name>`");
            return;
        }

        switch (args[0]) {

            case "earthphantom": {
                doStuff("http://earthphantom.tumblr.com/", event);
                break;
            }

            case "list": {
                sendMsg(event, "The current list of artists is: `" + String.join("`, `", artits) + "`");
                break;
            }

            default: {
                sendMsg(event, "This artist is not in the list of artists that have approved their art to be in this bot.\n" +
                        "Use `gb." + getName() + " list` for a list of artists.");
                break;
            }
        }
    }

    @Override
    public String getName() {
        return "artist";
    }

    @Override
    public String getHelp() {
        return "get the latest post of an artist";
    }

    private void doStuff(String url, GuildMessageReceivedEvent event) {
        String[] i = extractInfo(url);
        String usn = i[0];
        String type = i[1];
        if (type.equalsIgnoreCase("tumblr")) {
            extractPictureFromTumblr(usn, post ->
                    sendEmbed(event,
                            EmbedUtils.defaultEmbed()
                                    .setAuthor(usn, post.short_url, null)
                                    .setTitle(post.title, post.short_url)
                                    .setDescription(QuotesCommand.parseText(post.caption))
                                    .setImage(post.photos.get(0).original_size.url)
                                    .build()
                    )
            );
        } else if (type.equalsIgnoreCase("deviantart")) {
            //
        }
    }

    private String[] extractInfo(String a) {
        return a.replaceAll("https?://", "").split("\\.");
    }

    private void extractPictureFromTumblr(String username, @NotNull Consumer<TumblrPost> cb) {
        String url = String.format(
                "https://api.tumblr.com/v2/blog/%s.tumblr.com/posts?api_key=%s&type=photo&limit=1",
                username,
                SpoopyUtils.config.getString("api.tumblr", "API_KEY")
        );
        WebUtils.ins.getAson(url).async(ason ->
                cb.accept(Ason.deserializeList(ason.getJsonArray("response.posts"), TumblrPost.class).get(0)));

    }

}
