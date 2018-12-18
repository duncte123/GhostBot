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

package me.duncte123.ghostbot.commands.dannyphantom.image

import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.commands.dannyphantom.text.QuotesCommand
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.objects.tumblr.TumblrPost
import me.duncte123.ghostbot.utils.SpoopyUtils
import org.jetbrains.annotations.NotNull
import org.jsoup.nodes.Element

import java.util.function.Consumer

class DPArtistsCommand extends ImageBase {
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

    private final String[] artists = [
        'earthphantom',
        'allyphantomrush',
        'umbrihearts',
        'amethystocean-adr',
        'amethystocean',
        'amethyst-ocean',
        'ceciliaspen'
    ]

    @Override
    void execute(CommandEvent event) {

        def args = event.args

        if (!name.equalsIgnoreCase(event.invoke)) {
            args = [event.invoke] as String[]
        }

        if (args.length < 1) {
            sendMessage(event, "Correct usage is `gb.$name <artist name>`")
            return
        }

        switch (args[0]) {
            case 'earthphantom':
                doStuff('earthphantom.tumblr.com', event)
                break

            case 'allyphantomrush':
                doStuff('https://allyphantomrush.deviantart.com/', event)
                break

            case 'umbrihearts':
                doStuff('https://umbrihearts.deviantart.com/', event)
                break

            case 'amethystocean-adr':
                doStuff('amethystocean-adr.tumblr.com', event)
                break

            case 'amethystocean':
            case 'amethyst-ocean':
                doStuff('amethyst-ocean.deviantart.com', event)
                break

            case 'ceciliaspen':
                doStuff('ceciliaspen.tumblr.com', event)
                break

            case 'list':
                sendMessage(event, "The current list of artists is: `${artists.join('"`, `"')}`")
                break

            default:
                sendMessage(event, 'This artist is not in the list of artists that have approved their art to be in this bot.\n' +
                    "Use `gb.$name list` for a list of artists.")
                break

        }

    }

    @Override
    String getName() { 'artist' }

    @Override
    String getHelp() { 'Get the latest post of a GhostBotApproved™ artist' }

    @Override
    String[] getAliases() {
        return artists
    }

    private static String[] extractInfo(String a) {
        return a.replaceAll('https?://', '').split('\\.')
    }

    private static String getTumblrProfilePictureUrl(String domain) {
        return "https://api.tumblr.com/v2/blog/$domain/avatar/48"
    }

    private void extractPictureFromTumblr(String username, @NotNull Consumer<TumblrPost> cb) {
        def url = "https://api.tumblr.com/v2/blog/${username}.tumblr.com/posts?api_key=" +
            "${SpoopyUtils.config.api.tumblr}&type=photo&limit=1"

        WebUtils.ins.getJSONObject(url).async {
            cb.accept(
                gson.fromJson(it.getJSONObject('response')
                    .getJSONArray('posts').getJSONObject(0).toString(), TumblrPost.class)
            )
        }

    }

    private void getDeviantartDataXmlOnly(String usn, Consumer<LocalDeviantData> cb) {
        WebUtils.ins.scrapeWebPage('https://backend.deviantart.com/rss.xml' +
            "?type=deviation&q=by%3A$usn+sort%3Atime+meta%3Aall").async {
            //get an item
            Element item = it.selectFirst('item')
            cb.accept(new LocalDeviantData(
                item.selectFirst('media|copyright').attr('url'),
                item.selectFirst('media|content[medium="image"]').attr('url'),
                item.selectFirst('title').text(),
                item.select('media|credit').get(1).text(),
                item.selectFirst('guid[isPermaLink="true"]').text()
            ))
        }
    }

    private void doStuff(String url, CommandEvent event) {
        def info = extractInfo(url)
        def usn = info[0]
        def type = info[1]

        if (type.equalsIgnoreCase('tumblr')) {
            def profilePicture = getTumblrProfilePictureUrl(url)

            extractPictureFromTumblr(usn) {

                if (!it.type.equalsIgnoreCase('photo')) {
                    sendMessage(event, "Got a post of type `$it.type` for the type `photo`\n" +
                        'WTF tumblr?\n' +
                        "Here's the link anyway: <$it.post_url>")
                    return
                }

                sendMessage(event,
                    EmbedUtils.defaultEmbed()
                        .setAuthor(usn, it.post_url, profilePicture)
                        .setTitle('Link to post', it.post_url)
                        .setDescription(QuotesCommand.parseText(it.caption))
                        .setThumbnail(profilePicture)
                        .setImage(it.photos[0].original_size.url)
                )

            }
        }

        if (type.equalsIgnoreCase('deviantart')) {

            getDeviantartDataXmlOnly(usn) {
                sendMessage(event,
                    EmbedUtils.defaultEmbed()
                        .setAuthor(usn, it.authorUrl, it.avatarUrl)
                        .setTitle(it.title, it.link)
                        .setThumbnail(it.avatarUrl)
                        .setImage(it.thumbnailUrl)
                )
            }

        }
    }

    @Override
    boolean isSlackCompatible() { true }

    private class LocalDeviantData {

        String authorUrl
        String thumbnailUrl
        String title
        String avatarUrl
        String link

        LocalDeviantData(String a, String t, String ti, String pfp, String l) {
            this.authorUrl = a
            this.thumbnailUrl = t
            this.title = ti
            this.avatarUrl = pfp
            this.link = l
        }
    }
}
