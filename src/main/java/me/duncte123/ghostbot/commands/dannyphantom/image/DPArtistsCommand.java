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

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class DPArtistsCommand extends ImageBase {
    /*
     *    http://earthphantom.tumblr.com/ (approved)
     *    http://amethystocean-adr.tumblr.com/
     *    http://doppelgangercomic.tumblr.com/ (approved, has own command)
     *
     *    https://allyphantomrush.deviantart.com/
     *
     *    http://deadlandsqueen.tumblr.com/
     *    http://askfentonworks.tumblr.com/
     *    http://thickerthanectoplasm.tumblr.com/
     *    http://umbrihearts.tumblr.com/ or https://umbrihearts.deviantart.com/
     *
     *    needed things
     *    URL: link of the artist page
     *    Type: Website where the data is from
     *
     *    Obtaining data:
     *    Arrays.toString("URL".replaceAll("https?://", "").split("\\."))
     *    the array should be
     *    [0] = URL
     *    [1] = Type
     *    (and some more useless info)
     */

    private final String[] artists = {
        "earthphantom",
        "allyphantomrush",
        "scarletghostx",
        "umbrihearts",
        "amethystocean-adr",
        "amethystocean",
        "amethyst-ocean",
        "ceciliaspen"
    };

    @Override
    public void execute(CommandEvent event) {

        final List<String> args = new ArrayList<>(event.getArgs());

        if (!getName().equalsIgnoreCase(event.getInvoke())) {
            args.clear();
            args.add(event.getInvoke());
        }

        if (args.size() < 1) {
            sendMsg(event, "Correct usage is `gb." + getName() + " <artist name>`");
            return;
        }

        switch (args.get(0)) {
            case "earthphantom":
                doStuff("earthphantom.tumblr.com", event);
                break;

            case "allyphantomrush":
            case "scarletghostx":
                doStuff("https://scarletghostx.deviantart.com/", event);
                break;

            case "umbrihearts":
                doStuff("https://umbrihearts.deviantart.com/", event);
                break;

            case "amethystocean-adr":
                doStuff("amethystocean-adr.tumblr.com", event);
                break;

            case "amethystocean":
            case "amethyst-ocean":
                doStuff("amethyst-ocean.deviantart.com", event);
                break;

            case "ceciliaspen":
                doStuff("ceciliaspen.tumblr.com", event);
                break;

            case "list":
                sendMsg(event, "The current list of artists is: `" + String.join("`, `", artists) + '`');
                break;

            default:
                sendMsg(event, "This artist is not in the list of artists that have approved their art to be in this bot.\n" +
                    "Use `gb." + getName() + " list` for a list of artists.");
                break;

        }

    }

    @Override
    public String getName() { return "artist"; }

    @Override
    public String getHelp() { return "Get the latest post of a GhostBotApproved™ artist"; }

    @Override
    public List<String> getAliases() {
        return Arrays.asList(artists);
    }

    private void extractPictureFromTumblr(String username, GhostBotConfig config,
                                          ObjectMapper mapper, @NotNull Consumer<TumblrPost> cb) {
        final String url = String.format(
            "https://api.tumblr.com/v2/blog/%s.tumblr.com/posts?api_key=%s&type=photo&limit=1",
            username, config.api.tumblr
        );

        WebUtils.ins.getJSONObject(url).async((it) -> {
                try {
                    cb.accept(
                        mapper.readValue(it.get("response")
                            .get("posts").get(0).traverse(), TumblrPost.class)
                    );
                } catch (IOException ignored) { }
            }
        );
    }

    private void getDeviantartDataXmlOnly(String usn, Consumer<LocalDeviantData> cb) {
        WebUtils.ins.scrapeWebPage("https://backend.deviantart.com/rss.xml" +
            "?type=deviation&q=by%3A" + usn + "+sort%3Atime+meta%3Aall").async((it) -> {
            //get an item
            final Element item = it.selectFirst("item");

            cb.accept(new LocalDeviantData(
                item.selectFirst("media|copyright").attr("url"),
                item.selectFirst("media|content[medium=\"image\"]").attr("url"),
                item.selectFirst("title").text(),
                item.select("media|credit").get(1).text(),
                item.selectFirst("guid[isPermaLink=\"true\"]").text()
            ));
        });
    }

    private void doStuff(String url, CommandEvent event) {
        final String[] info = extractInfo(url);
        final String usn = info[0];
        final String type = info[1];
        final GhostBotConfig config = event.getContainer().getConfig();
        final ObjectMapper jackson = event.getContainer().getJackson();

        if (type.equalsIgnoreCase("tumblr")) {
            final String profilePicture = getTumblrProfilePictureUrl(url);

            extractPictureFromTumblr(usn, config, jackson, (it) -> {

                if (!it.type.equalsIgnoreCase("photo")) {
                    sendMsg(event, "Got a post of type `" + it.type + "` for the type `photo`\n" +
                        "WTF tumblr?\n" +
                        "Here\'s the link anyway: <" + it.post_url + '>');

                    return;
                }

                sendEmbed(event,
                    EmbedUtils.defaultEmbed()
                        .setAuthor(usn, it.post_url, profilePicture)
                        .setTitle("Link to post", it.post_url)
                        .setDescription(QuotesCommand.parseText(it.caption))
                        .setThumbnail(profilePicture)
                        .setImage(it.photos.get(0).getOriginalSize().getUrl())
                );
            });
        }

        if (type.equalsIgnoreCase("deviantart")) {
            getDeviantartDataXmlOnly(usn, (it) ->
                sendEmbed(event,
                    EmbedUtils.defaultEmbed()
                        .setAuthor(usn, it.authorUrl, it.avatarUrl)
                        .setTitle(it.title, it.link)
                        .setThumbnail(it.avatarUrl)
                        .setImage(it.thumbnailUrl)
                )
            );
        }
    }

    private static String[] extractInfo(String a) {
        return a.replaceAll("https?://", "").split("\\.");
    }

    private static String getTumblrProfilePictureUrl(String domain) {
        return "https://api.tumblr.com/v2/blog/" + domain + "/avatar/48";
    }

    private class LocalDeviantData {

        final String authorUrl;
        final String thumbnailUrl;
        final String title;
        final String avatarUrl;
        final String link;

        LocalDeviantData(String a, String t, String ti, String pfp, String l) {
            this.authorUrl = a;
            this.thumbnailUrl = t;
            this.title = ti;
            this.avatarUrl = pfp;
            this.link = l;
        }
    }
}