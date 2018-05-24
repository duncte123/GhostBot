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

import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.tumblr.TumblrDialogue;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.utils.TumblrUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuotesCommand extends Command {

    private final String[] types = {"chat", "text", "quote"};
    private final String[] messages = {
            "Starting to reload quotes",
            "Clearing posts",
            "Clearing indexes",
            "<a:downloading:437572253605953557> Downloading new quotes",
            "Found {TOTAL} quotes.",
            "Adding {COUNT_NEW) new quotes to list",
            "Going Ghost <a:DPTransform8bit:425714608406528012>",
            "Finished <:DPJoy:425714609702305804> ({TOTAL} quotes in the system and {COUNT_NEW) new)"
    };

    private final List<TumblrPost> allQuotes = new ArrayList<>();
    private final Map<String, List<TumblrPost>> guildQuotes = new HashMap<>();
    private final List<Long> badPostIds = Arrays.asList(
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
    );
    private int oldCount = 0;

    public QuotesCommand() {
        reloadQuotes();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            if ("reload".equals(args[0])) {

                if (event.getAuthor().getId().equals(Variables.OWNER_ID)) {
                    reloadQuotes();
                    MessageUtils.sendMsg(event, messages[0], success ->
                            new Thread(() -> {
                                for (String m : Arrays.copyOfRange(messages, 1, messages.length)) {
                                    try {
                                        Thread.sleep(3500);
                                    } catch (InterruptedException ignored) {
                                    }
                                    String mf = m;
                                    if (m.contains("{COUNT_NEW)")) {
                                        mf = mf.replaceAll(Pattern.quote("{COUNT_NEW)"), String.valueOf(allQuotes.size() - oldCount));
                                    }
                                    if (m.contains("{TOTAL}")) {
                                        mf = mf.replaceAll(Pattern.quote("{TOTAL}"), String.valueOf(allQuotes.size()));
                                    }
                                    logger.debug(mf);
                                    success.editMessage(mf).queue();
                                }
                                Thread.currentThread().interrupt();
                            }, "Message fun thinh").start());
                    return;
                } else {
                    MessageUtils.sendMsg(event, "Only the bot owner can reload quotes");
                    return;
                }
            } else if ("total".equals(args[0])) {
                MessageUtils.sendMsg(event, "There are a total of " + allQuotes.size() + " quotes in the system at the moment");
                return;
            }
        }

        String gid = event.getGuild().getId();
        if (!guildQuotes.containsKey(gid) || guildQuotes.get(gid).size() == 0) {
            guildQuotes.put(gid, new ArrayList<>(allQuotes));
        }

        /*int index = guildQuotes.get(gid);
        TumblrPost post = allQuotes.get(index);*/
        List<TumblrPost> posts = guildQuotes.get(gid);
        TumblrPost post = posts.get(SpoopyUtils.random.nextInt(posts.size()));
        posts.remove(post);
        String type = post.type;

        EmbedBuilder eb = EmbedUtils.defaultEmbed()
                .setTitle("Link to Post", post.post_url);

        switch (type) {
            case "chat":
                List<TumblrDialogue> dialogue = post.dialogue;
                dialogue.forEach(
                        a -> eb.appendDescription("**").appendDescription(a.label)
                                .appendDescription("** ")
                                .appendDescription(parseText(a.phrase))
                                .appendDescription("\n")
                );
                break;
            case "text":
                eb.setDescription(parseText(post.body));
                break;
            case "quote":
                eb.setDescription("\"" + parseText(post.text) + "\"");
                eb.appendDescription("\n\n - _ " + parseText(post.source) + "_");
                break;
        }

        //guildQuotes.put(gid, ++index);
        MessageUtils.sendEmbed(event, eb.build());
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
    public String[] getAliases() {
        return new String[]{"quotes"};
    }

    @Override
    public Category getCategory() {
        return Category.TEXT;
    }

    private void reloadQuotes() {
        if (SpoopyUtils.config.getBoolean("running_local", false)) return;

        oldCount = allQuotes.size();
        allQuotes.clear();
        guildQuotes.clear();
        for (String type : types) {
            logger.info("Getting quotes from type " + type);
            TumblrUtils.fetchAllFromAccount("totallycorrectdannyphantomquotes.tumblr.com", type, posts -> {
                List<TumblrPost> filteredPosts = posts.stream().filter(post -> !badPostIds.contains(post.id))
                        .collect(Collectors.toList());
                allQuotes.addAll(filteredPosts);
                logger.info("Fetched " + filteredPosts.size() + " quotes from type " + type);
            });
        }
    }

    public static String parseText(String raw) {
        if (raw == null || raw.isEmpty())
            return "";

        raw = StringEscapeUtils.unescapeHtml4(raw);
        String input = raw.replaceAll(Pattern.quote("<br/>"), "\n");
        String replacePWith = input.contains("</p>\n") ? "" : "\n";

        return input
                //show the stars and remove the ps
                .replaceAll("\\*", "\\\\*")
                .replaceAll(Pattern.quote("<p>"), "")
                .replaceAll(Pattern.quote("</p>"), replacePWith)
                .replaceAll(Pattern.quote("<p/>"), replacePWith) //because some posts are fucked
                //Italics
                .replaceAll(Pattern.quote("<i>"), "_")
                .replaceAll(Pattern.quote("</i>"), "_")
                .replaceAll(Pattern.quote("<em>"), "_")
                .replaceAll(Pattern.quote("</em>"), "_")
                //bold
                .replaceAll(Pattern.quote("<b>"), "**")
                .replaceAll(Pattern.quote("</b>"), "**")
                .replaceAll(Pattern.quote("<strong>"), "**")
                .replaceAll(Pattern.quote("</strong>"), "**")
                //useless crap that we don't need
                .replaceAll(Pattern.quote("<small>"), "")
                .replaceAll(Pattern.quote("</small>"), "")
                //links
                .replaceAll("<a(?:.*)href=\"(\\S+)\"(?:.*)>(.*)</a>", "[$2]($1)");
    }
}
