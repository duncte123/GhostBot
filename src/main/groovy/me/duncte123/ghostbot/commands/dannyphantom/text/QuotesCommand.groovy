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
import me.duncte123.ghostbot.objects.tumblr.TumblrPost
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.utils.TumblrUtils
import me.duncte123.ghostbot.variables.Variables
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
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
    private final TLongList badPostIds = new TLongArrayList()

    QuotesCommand() {
        ['156199508936',
         '141701068521',
         '139748205676',
         '145485004576',
         '131957587201',
         '145767003281',
         '122464866251',
         '149288809271',
         '131048227566',
         '160064523456',
         '146961714036',
         '157865830301',
         '136789766336',
         '148512885491',
         '137376851771',
         '147819522951',
         '147825378346',
         '156199957996',
         '143194957186',
         '121801283241',
         '121891439031',
         '144734161886',
         '130808913006',
         '130834334051',
         '131278048551',
         '163028433406',
         '150823532681',
         '173944925826',
         '127476921111',
         '174190854511'].stream().map {
            badPostIds.add(Long.parseLong(it))
        }

        reloadQuotes()
    }

    @Override
    void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.size() > 0) {
            def joined = args.join('')

            if (joined.startsWith('id:')) {


                def id = joined.substring('id:'.length())

                if (!id.empty) {
                    def idLong = Long.parseLong(id)

                    getPostFromId(idLong, { sendQuote(event, it) }, { sendMsg(event, it) })
                }

                return
            } else if ('total' == args[0]) {
                sendMsg(event, "There are a total of ${allQuotes.size()} quotes in the system at the moment")
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

    private static void sendQuote(GuildMessageReceivedEvent event, TumblrPost post) {
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

        sendEmbed(event, eb.build())
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

        for (String type : types) {

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
    }
}
