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
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.util.function.Consumer;

import static me.duncte123.ghostBot.utils.MessageUtils.sendEmbed;
import static me.duncte123.ghostBot.utils.MessageUtils.sendMsg;

public class DPArtistsCommand extends ImageCommand {
    /*
    http://earthphantom.tumblr.com/ (approved)
    http://amethystocean-adr.tumblr.com/
    http://doppelgangercomic.tumblr.com/ (approved, has own command)

    https://allyphantomrush.deviantart.com/

    http://deadlandsqueen.tumblr.com/
    http://askfentonworks.tumblr.com/
    http://thickerthanectoplasm.tumblr.com/
    http://umbrihearts.tumblr.com/ or https://umbrihearts.deviantart.com/

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

    private final String[] artists = {
            "earthphantom",
            "allyphantomrush",
            "umbrihearts",
            "doppelganger",
            "amethystocean-adr",
            "ceciliaspen"
    };

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (!getName().equalsIgnoreCase(invoke)) {
            args = new String[]{invoke};
        }

        if (args.length < 1) {
            sendMsg(event, "Correct usage is `gb." + getName() + " <artist name>`");
            return;
        }

        switch (args[0]) {

            case "earthphantom": {
                doStuff("earthphantom.tumblr.com", event);
                break;
            }

            case "allyphantomrush": {
                doStuff("https://allyphantomrush.deviantart.com/", event);
                break;
            }

            case "umbrihearts": {
                doStuff("https://umbrihearts.deviantart.com/", event);
                break;
            }

            case "doppelganger": {
                doStuff("doppelgangercomic.tumblr.com", event);
                break;
            }

            case "amethystocean-adr": {
                doStuff("amethystocean-adr.tumblr.com", event);
                break;
            }

            case "ceciliaspen": {
                doStuff("ceciliaspen.tumblr.com", event);
                break;
            }

            case "list": {
                sendMsg(event, "The current list of artists is: `" + String.join("`, `", artists) + "`");
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
    public String[] getAliases() {
        return artists;
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
            String profilePicture = getTumblrProfilePictureUrl(url);
            extractPictureFromTumblr(usn, post ->
                    sendEmbed(event,
                            EmbedUtils.defaultEmbed()
                                    .setAuthor(usn, post.post_url, profilePicture)
                                    .setTitle("Link to post", post.post_url)
                                    .setDescription(QuotesCommand.parseText(post.caption))
                                    .setThumbnail(profilePicture)
                                    .setImage(post.photos.get(0).original_size.url)
                                    .build()
                    )
            );
        } else if (type.equalsIgnoreCase("deviantart")) {

            getDeviantartDataXmlOnly(usn, data ->
                    sendEmbed(event,
                            EmbedUtils.defaultEmbed()
                                    .setAuthor(usn, data.authorUrl, data.avatarUrl)
                                    .setTitle(data.title, data.link)
                                    .setThumbnail(data.avatarUrl)
                                    .setImage(data.thumbnailUrl)
                                    .build()
                    )
            );
            //Old junk that I might remove soon
            /*getDeviantartData(usn, data -> {
                Oembed embed = data.getRight();
                sendEmbed(event,
                        EmbedUtils.defaultEmbed()
                                .setAuthor(usn, embed.author_url, data.getLeft())
                                .setTitle(embed.title, data.getMiddle())
                                .setThumbnail(data.getLeft())
                                .setImage(embed.thumbnail_url)
                                .build()
                );
            });*/
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

    private String getTumblrProfilePictureUrl(String domain) {
        return "https://api.tumblr.com/v2/blog/" + domain + "/avatar/48";
    }

    //Old junk that I might remove soon
    /*private void getDeviantartData(String usn, Consumer<Triple<String, String, Oembed>> cb) {
        WebUtils.ins.getText("https://backend.deviantart.com/rss.xml?type=deviation&q=by%3A" +
                usn + "+sort%3Atime+meta%3Aall").async(txt -> {
            Document doc = Jsoup.parse(txt, "", Parser.xmlParser());
            Element item = doc.select("item").get(0);
            String link = item.selectFirst("link").text();
            String avatarUrl = item.select("[role=\"author\"]").get(1).text();
            //Use https://backend.deviantart.com/oembed?url= on the returned url
            WebUtils.ins.getAson("https://backend.deviantart.com/oembed?url=" + link).async(ason -> {
                Oembed oembed = Ason.deserialize(ason, Oembed.class);
                cb.accept(Triple.of(avatarUrl, link, oembed));
            });
        });
    }*/

    private void getDeviantartDataXmlOnly(String usn, Consumer<LocalDeviantData> cb) {
        WebUtils.ins.getText("https://backend.deviantart.com/rss.xml?type=deviation&q=by%3A" +
                usn + "+sort%3Atime+meta%3Aall").async(txt -> {
            Document doc = Jsoup.parse(txt, "", Parser.xmlParser());
            //get an item
            Element item = doc.selectFirst("item");
            cb.accept(new LocalDeviantData(
                    item.selectFirst("media|copyright").attr("url"),
                    item.selectFirst("media|content[medium=\"image\"]").attr("url"),
                    item.selectFirst("title").text(),
                    item.select("media|credit").get(1).text(),
                    item.selectFirst("link").text()
            ));
        });
    }

    private class LocalDeviantData {

        String authorUrl;
        String thumbnailUrl;
        String title;
        String avatarUrl;
        String link;

        LocalDeviantData(String a, String t, String ti, String pfp, String l) {
            this.authorUrl = a;
            this.thumbnailUrl = t;
            this.title = ti;
            this.avatarUrl = pfp;
            this.link = l;
        }
    }

}
