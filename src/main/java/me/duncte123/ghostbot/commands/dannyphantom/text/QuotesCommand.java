/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2021  Duncan "duncte123" Sterken
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
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.objects.command.Command;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.objects.tumblr.TumblrDialogue;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.utils.TumblrUtils;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class QuotesCommand extends Command {
    private static final String DOMAIN = "totallycorrectdannyphantomquotes.tumblr.com";
    private final String[] types = {"chat", "text", "quote"};

    @Override
    public void execute(ICommandEvent event) {
        final List<String> args = event.getArgs();

        if (!args.isEmpty()) {
            final OptionMapping idOpt = event.getOption("id");

            if (idOpt != null) {
                getPostFromId(idOpt.getAsLong(), event.getContainer(), (it) -> sendQuote(event, it), event::reply);
                return;
            }
        }

        final List<TumblrPost> posts = this.getQuotes(event.getContainer());
        final TumblrPost post = posts.get(ThreadLocalRandom.current().nextInt(posts.size()));

        sendQuote(event, post);
    }

    @Override
    public String getName() {
        return "quote";
    }

    @Override
    public String getHelp() {
        return "Get a random quote from https://totallycorrectdannyphantomquotes.tumblr.com/";
    }

    @Override
    public List<String> getAliases() {
        return List.of("quotes");
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TEXT;
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(
            new OptionData(INTEGER, "id", "Select a quote by the id")
        );
    }

    private void getPostFromId(long id, Container container, Consumer<TumblrPost> cb, Consumer<String> fail) {
        TumblrUtils.getInstance().fetchSinglePost(DOMAIN, id, container.getConfig(), container.getJackson(),
            cb,
            (it) -> fail.accept("Something went wrong: " + it.getMessage())
        );

    }

    private List<TumblrPost> getQuotes(Container container) {
        final File quotesFile = new File("./data/quotes.json");

        if (!quotesFile.exists()) {
            this.reloadQuotes(container.getConfig(), container.getJackson(), quotesFile);
            return List.of();
        }

        try {
            return container.getJackson().readValue(quotesFile, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reloadQuotes(GhostBotConfig config, ObjectMapper jackson, File quotesFile) {
        final List<TumblrPost> allQuotes = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger();
        /// <editor-fold>
        final TLongList badPostIds = new TLongArrayList(
            new long[]{
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
                174190854511L,
                189467049076L
            }
        );
        /// </editor-fold>

        for (String type : types) {
            logger.info("Getting quotes from type {}", type);

            TumblrUtils.getInstance().fetchAllFromAccount(DOMAIN, type, config, jackson,
                (posts) -> {
                    try {
                        counter.incrementAndGet();

                        final List<TumblrPost> filteredPosts = posts.stream()
                            .filter((it) -> !badPostIds.contains(it.id))
                            .filter((it) -> {
                                final var tagsList = Arrays.asList(it.tags);

                                return !tagsList.contains("mod talk");
                            })
                            .toList();

                        allQuotes.addAll(filteredPosts);
                        logger.info("Fetched {} quotes from type {}", filteredPosts.size(), type);

                        if (counter.get() == types.length) {
                            if (quotesFile.exists()) {
                                quotesFile.delete();
                            }

                            quotesFile.createNewFile();

                            final BufferedWriter writer = new BufferedWriter(new FileWriter(quotesFile));
                            writer.write(jackson.writeValueAsString(allQuotes));
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

    private void sendQuote(ICommandEvent event, TumblrPost post) {
        final EmbedBuilder eb = EmbedUtils.getDefaultEmbed()
            .setTitle("Click to view original post", post.post_url)
            .setFooter("Quote id: " + post.id, Variables.FOOTER_ICON);

        switch (post.type) {
            case "chat" -> {
                final List<TumblrDialogue> dialogue = post.dialogue;
                dialogue.forEach(
                    (it) -> eb.appendDescription(String.format("**%s** %s\n", it.getLabel(), parseText(it.getPhrase())))
                );
            }
            case "text" -> eb.setDescription(parseText(post.body));
            case "quote" ->
                eb.setDescription(String.format("\"%s\"\n\n - _%s_", parseText(post.text), parseText(post.source)));
            default -> eb.setDescription(String.format("Invalid post type`%s` found", post.type));
        }

        event.reply(eb);
    }

    public static String parseText(String rawInput) {

        if (rawInput == null || rawInput.isEmpty()) {
            return "";
        }

        String raw = StringEscapeUtils.unescapeHtml4(rawInput);

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
