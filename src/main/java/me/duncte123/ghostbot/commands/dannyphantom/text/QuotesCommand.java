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

package me.duncte123.ghostbot.commands.dannyphantom.text;

import com.fasterxml.jackson.core.type.TypeReference;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.objects.Command;
import me.duncte123.ghostbot.objects.CommandCategory;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.tumblr.TumblrDialogue;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.SpoopyUtils;
import me.duncte123.ghostbot.utils.TumblrUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.duncte123.botcommons.messaging.MessageUtils.sendEmbed;
import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;

public class QuotesCommand extends Command {
    private static final String DOMAIN = "totallycorrectdannyphantomquotes.tumblr.com";
    private final String[] types = {"chat", "text", "quote"};
    private final List<TumblrPost> allQuotes = new ArrayList<>();
    private final TLongObjectMap<List<TumblrPost>> guildQuotes = new TLongObjectHashMap<>();
    private final TLongList badPostIds = new TLongArrayList(new long[]{
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
    });

    public QuotesCommand() {
        reloadQuotes();
    }

    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();

        if (args.size() > 0) {
            final String joined = String.join("", args);

            if (joined.startsWith("id:")) {
                final String id = joined.substring("id:".length());

                if (!id.isEmpty()) {
                    final long idLong = Long.parseLong(id);

                    getPostFromId(idLong, (it) -> sendQuote(event, it), (it) -> sendMsg(event, it));
                }

                return;
            } else if ("total".equalsIgnoreCase(args.get(0))) {
                sendMsg(event, "There are a total of " + allQuotes.size() + " quotes in the system at the moment");

                return;
            }
        }

        final long gid = event.getGuild().getIdLong();

        if (!guildQuotes.containsKey(gid) || guildQuotes.get(gid).size() == 0) {
            guildQuotes.put(gid, new ArrayList<>(allQuotes));
        }

        final List<TumblrPost> posts = guildQuotes.get(gid);
        final TumblrPost post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

        posts.remove(post);
        sendQuote(event, post);
    }

    @Override
    public String getName() {
        return "quote";
    }

    @Override
    public String getHelp() {
        return "Get a random quote from http://totallycorrectdannyphantomquotes.tumblr.com/";
    }

    @Override
    public List<String> getAliases() {
        return List.of("'quotes'");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TEXT;
    }

    private void getPostFromId(long id, Consumer<TumblrPost> cb, Consumer<String> fail) {
        final Optional<TumblrPost> opt = allQuotes.stream().filter((it) -> it.id == id).findFirst();

        if (opt.isPresent()) {
            cb.accept(opt.get());

            return;
        }

        TumblrUtils.getInstance().fetchSinglePost(DOMAIN, id,
            (it) -> {
                allQuotes.add(it);
                cb.accept(it);
            },
            (it) -> fail.accept("Something went wrong: " + it.getMessage())
        );

    }

    private void reloadQuotes() {
        final File quotesFile = new File("quotes.json");

        if (quotesFile.exists()) {
            try {
                final List<TumblrPost> quotes = SpoopyUtils.getJackson().readValue(quotesFile, new TypeReference<List<TumblrPost>>() {});

                allQuotes.addAll(quotes);
                logger.info("Loading quotes from file");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        allQuotes.clear();
        guildQuotes.clear();

        final AtomicInteger counter = new AtomicInteger();

        for (String type : types) {

            logger.info("Getting quotes from type {}", type);

            TumblrUtils.getInstance().fetchAllFromAccount(DOMAIN, type,
                (posts) -> {
                    try {
                        counter.incrementAndGet();

                        final List<TumblrPost> filteredPosts = posts.stream().filter((it) -> !badPostIds.contains(it.id)).collect(Collectors.toList());

                        allQuotes.addAll(filteredPosts);
                        logger.info("Fetched {} quotes from type {}", filteredPosts.size(), type);

                        if (counter.get() == types.length) {
                            quotesFile.createNewFile();

                            final BufferedWriter writer = new BufferedWriter(new FileWriter(quotesFile));
                            writer.write(SpoopyUtils.getJackson().writeValueAsString(allQuotes));
                            writer.close();

                            logger.info("Wrote quotes to file");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            );
        }
    }

    private void sendQuote(CommandEvent event, TumblrPost post) {
        final EmbedBuilder eb = EmbedUtils.defaultEmbed()
            .setTitle("Link to Post", post.post_url)
            .setFooter("Quote id: " + post.id, Variables.FOOTER_ICON);

        switch (post.type) {
            case "chat":
                final List<TumblrDialogue> dialogue = post.dialogue;

                dialogue.forEach(
                    (it) -> eb.appendDescription(String.format("**%s** %s\n", it.getLabel(), parseText(it.getPhrase())))
                );

                break;
            case "text":
                eb.setDescription(parseText(post.body));
                break;

            case "quote":
                eb.setDescription(String.format("\"%s\"\n\n - _%s_", parseText(post.text), parseText(post.source)));
                break;
        }

        sendEmbed(event, eb);
    }

    public static String parseText(String raw) {

        if (raw == null || raw.isEmpty()) {
            return "";
        }

        raw = StringEscapeUtils.unescapeHtml4(raw);

        final String input = raw.replaceAll(Pattern.quote("<br/>"), "\n");
        final String replacePWith = input.contains("</p>\n") ? "" : "\n";

        return input
            //show the stars and remove the ps
            .replaceAll("\\*", "\\\\*")
            .replaceAll(Pattern.quote("<p>"), "")
            .replaceAll(Pattern.quote("</p>"), replacePWith)
            .replaceAll(Pattern.quote("<p/>"), replacePWith) //because some posts are fucked
            //Italics
            .replaceAll(Pattern.quote("<i>"), "*")
            .replaceAll(Pattern.quote("</i>"), "*")
            .replaceAll(Pattern.quote("<em>"), "*")
            .replaceAll(Pattern.quote("</em>"), "*")
            //bold
            .replaceAll(Pattern.quote("<b>"), "**")
            .replaceAll(Pattern.quote("</b>"), "**")
            .replaceAll(Pattern.quote("<strong>"), "**")
            .replaceAll(Pattern.quote("</strong>"), "**")
            // strikethrough
            .replaceAll(Pattern.quote("<strike>"), "~~")
            .replaceAll(Pattern.quote("</strike>"), "~~")
            //useless crap that we don"t need
            .replaceAll(Pattern.quote("<small>"), "")
            .replaceAll(Pattern.quote("</small>"), "")
            //links
            .replaceAll("<a(?:.*)href=\"(\\S+)\"(?:.*)>(.*)</a>", "[$2]($1)")
            // Lists
            .replaceAll("<ul>", "")
            .replaceAll("</ul>", "")
            .replaceAll("<li>", " - ")
            .replaceAll("</li>", "");
    }
}
