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

package me.duncte123.ghostbot.commands.dannyphantom.text

import gnu.trove.list.TLongList
import gnu.trove.list.array.TLongArrayList
import gnu.trove.map.TLongObjectMap
import gnu.trove.map.hash.TLongObjectHashMap
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.ghostbot.objects.Command
import me.duncte123.ghostbot.objects.CommandCategory
import me.duncte123.ghostbot.objects.CommandEvent
import me.duncte123.ghostbot.objects.tumblr.TumblrPost
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.utils.TumblrUtils
import me.duncte123.ghostbot.variables.Variables
import org.apache.commons.text.StringEscapeUtils

import java.util.concurrent.ThreadLocalRandom
import java.util.function.Consumer
import java.util.regex.Pattern

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg

class QuotesCommand extends Command {

    private static final String DOMAIN = 'totallycorrectdannyphantomquotes.tumblr.com'
    private final String[] types = ['chat', 'text', 'quote']
    private final List<TumblrPost> allQuotes = []
    private final TLongObjectMap<List<TumblrPost>> guildQuotes = new TLongObjectHashMap<>()
    private final TLongList badPostIds = new TLongArrayList((long[]) [
        156199508936L,
        141701068521L,
        139748205676L,
        145485004576L,
        131957587201L,
        145767003281L,
        122464866251L,
        149288809271L,
        131048227566L,
        160064523456L,
        146961714036L,
        157865830301L,
        136789766336L,
        148512885491L,
        137376851771L,
        147819522951L,
        147825378346L,
        156199957996L,
        143194957186L,
        121801283241L,
        121891439031L,
        144734161886L,
        130808913006L,
        130834334051L,
        131278048551L,
        163028433406L,
        150823532681L,
        173944925826L,
        127476921111L,
        174190854511L
    ])

    QuotesCommand() {
        reloadQuotes()
    }

    @Override
    void execute(CommandEvent event) {

        def args = event.args

        if (args.size() > 0) {
            def joined = args.join('')

            if (joined.startsWith('id:')) {


                def id = joined.substring('id:'.length())

                if (!id.empty) {
                    def idLong = Long.parseLong(id)

                    getPostFromId(idLong, { sendQuote(event, it) }, { sendMessage(event, it) })
                }

                return
            } else if ('total' == args[0]) {
                sendMsg(event.event, "There are a total of ${allQuotes.size()} quotes in the system at the moment")
                return
            }
        }

        def gid = event.guild.idLong

        if (!guildQuotes.containsKey(gid) || guildQuotes.get(gid).size() == 0) {
            guildQuotes.put(gid, new ArrayList<>(allQuotes))
        }

        def posts = guildQuotes.get(gid)
        def post = posts[ThreadLocalRandom.current().nextInt(posts.size())]

        posts.remove(post)
        sendQuote(event, post)
    }

    @Override
    String getName() { 'quote' }

    @Override
    String getHelp() { 'Get a random quote from http://totallycorrectdannyphantomquotes.tumblr.com/' }

    @Override
    String[] getAliases() { ['quotes'] }

    @Override
    CommandCategory getCategory() {
        return CommandCategory.TEXT
    }

    private static void sendQuote(CommandEvent event, TumblrPost post) {
        def eb = EmbedUtils.defaultEmbed()
            .setTitle("Link to Post", post.post_url)
            .setFooter("Quote id: $post.id", Variables.FOOTER_ICON)

        switch (post.type) {
            case 'chat':
                def dialogue = post.dialogue
                dialogue.forEach {
                    eb.appendDescription("**$it.label** ${parseText(it.phrase)}\n")
                }
                break
            case 'text':
                eb.setDescription(parseText(post.body))
                break
            case 'quote':
                eb.setDescription("\"${parseText(post.text)}\"\n\n - _ ${parseText(post.source)}_")
                break
        }

        sendEmbed(event.event, eb)
    }

    private void getPostFromId(long id, Consumer<TumblrPost> cb, Consumer<String> fail) {

        def opt = allQuotes.stream().filter { it.id == id }.findFirst()

        if (opt.present) {
            cb.accept(opt.get())

            return
        }

        TumblrUtils.instance.fetchSinglePost(DOMAIN, id,
            {
                allQuotes.add(it)
                cb.accept(it)
            },
            {
                fail.accept("Something went wrong: $it.message")
            }
        )

    }

    private void reloadQuotes() {
        if (SpoopyUtils.config.running_local) {
            return
        }

        allQuotes.clear()
        guildQuotes.clear()

        for (String _type : types) {

            def type = _type

            logger.info("Getting quotes from type $type")

            TumblrUtils.instance.fetchAllFromAccount(DOMAIN, type,
                { posts ->

                    def filteredPosts = posts.stream().filter {
                        !badPostIds.contains(it.id)
                    }.collect()

                    allQuotes.addAll(filteredPosts)
                    logger.info("Fetched ${filteredPosts.size()} quotes from type $type")
                }
            )
        }
    }

    static String parseText(String raw) {

        if (raw == null || raw.empty) {
            return ""
        }

        raw = StringEscapeUtils.unescapeHtml4(raw)
        def input = raw.replaceAll(Pattern.quote('<br/>'), '\n')
        def replacePWith = input.contains('</p>\n') ? '' : '\n'

        return input
        //show the stars and remove the ps
            .replaceAll('\\*', '\\\\*')
            .replaceAll(Pattern.quote('<p>'), '')
            .replaceAll(Pattern.quote('</p>'), replacePWith)
            .replaceAll(Pattern.quote('<p/>'), replacePWith) //because some posts are fucked
        //Italics
            .replaceAll(Pattern.quote('<i>'), '*')
            .replaceAll(Pattern.quote('</i>'), '*')
            .replaceAll(Pattern.quote('<em>'), '*')
            .replaceAll(Pattern.quote('</em>'), '*')
        //bold
            .replaceAll(Pattern.quote('<b>'), '**')
            .replaceAll(Pattern.quote('</b>'), '**')
            .replaceAll(Pattern.quote('<strong>'), '**')
            .replaceAll(Pattern.quote('</strong>'), '**')
        //useless crap that we don't need
            .replaceAll(Pattern.quote('<small>'), '')
            .replaceAll(Pattern.quote('</small>'), '')
        //links
            .replaceAll('<a(?:.*)href="(\\S+)"(?:.*)>(.*)</a>', '[$2]($1)')
        // Lists
            .replaceAll('<ul>', '')
            .replaceAll('</ul>', '')
            .replaceAll('<li>', ' - ')
            .replaceAll('</li>', '')
    }
}
