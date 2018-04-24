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
import me.duncte123.botCommons.web.WebUtils;
import me.duncte123.ghostBot.objects.Category;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.objects.tumblr.TumblrDialogue;
import me.duncte123.ghostBot.objects.tumblr.TumblrPost;
import me.duncte123.ghostBot.utils.EmbedUtils;
import me.duncte123.ghostBot.utils.MessageUtils;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuotesCommand extends Command {

    private static final Logger logger = LoggerFactory.getLogger(QuotesCommand.class);
    private final String[] types = {"chat", "text", "quote"};
    private final String[] messages = {
            "Starting to reload quotes",
            "Clearing posts",
            "Clearing indexes",
            "<a:downloading:437572253605953557> Downloading new quotes",
            "Found {TOTAL} quotes.",
            "Adding {COUNT_NEW) new quotes to list",
            "Going Ghost <a:DPTransform8bit:425714608406528012>",
            "Finished <:DPJoy:425714609702305804> ({TOTAL} quotes in the system)"
    };
	
	private final Long[] badPostIds = {
		156199508936L,
		141701068521L
	};
	
    private final List<TumblrPost> tumblrPosts = new ArrayList<>();
    private final Map<String, Integer> indexes = new HashMap<>();

    private int oldCount = 0;

    public QuotesCommand() {
        reloadQuotes();
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        if (args.length > 0) {
            if("reload".equals(args[0])) {

                if (event.getAuthor().getId().equals(Variables.OWNER_ID)) {
                    reloadQuotes();
                    new Thread(() ->
                            MessageUtils.sendMsg(event, messages[0], success -> {
                                for (String m : Arrays.copyOfRange(messages, 1, messages.length)) {
                                    try {
                                        Thread.sleep(3500);
                                    } catch (InterruptedException ignored) {
                                    }
									String mf;
                                    if (m.contains("{COUNT_NEW)")) {
                                        mf = m.replaceAll(Pattern.quote("{COUNT_NEW)"), String.valueOf(tumblrPosts.size() - oldCount));
                                    } else if (m.contains("{TOTAL}")) {
                                        mf = m.replaceAll(Pattern.quote("{TOTAL}"), String.valueOf(tumblrPosts.size()));
                                    } else {
                                        mf = m;
                                    }
                                    logger.info(mf);
									success.editMessage(mf).queue();
                                }
                            }), "Message fun thinh").start();
                } else {
                    MessageUtils.sendMsg(event, "Only the bot owner can reload quotes");
                }
            } else if("total".equals(args[0])) {
                MessageUtils.sendMsg(event, "There are a total of " + tumblrPosts.size() + " quotes in the system at the moment");
            }
            return;
        }

        String gid = event.getGuild().getId();
        if (!indexes.containsKey(gid) || indexes.get(gid) >= tumblrPosts.size()) {
            indexes.put(gid, 0);
        }

        int index = indexes.get(gid);
        TumblrPost post = tumblrPosts.get(index);
        String type = post.type;

        EmbedBuilder eb = EmbedUtils.defaultEmbed()
                .setTitle("Link to Post", post.short_url);

        switch (type) {
            case "chat":
                List<TumblrDialogue> dialogue = post.dialogue;
                dialogue.forEach(
                        a -> eb.appendDescription("**").appendDescription(a.label)
                                .appendDescription("** ")
                                .appendDescription(StringEscapeUtils.unescapeHtml4(a.phrase))
                                .appendDescription("\n")
                );
                break;
            case "text":
                String bodyRaw = post.body.replaceAll(Pattern.quote("<br/>"), "\n");
                String replacePWith = bodyRaw.contains("</p>\n") ? "" : "\n";
                String bodyParsed = bodyRaw.replaceAll(Pattern.quote("<p>"), "")
                        .replaceAll("\\*", "\\\\*")
                        .replaceAll(Pattern.quote("</p>"), replacePWith)
                        .replaceAll(Pattern.quote("<i>"), "_")
                        .replaceAll(Pattern.quote("</i>"), "_")
                        .replaceAll(Pattern.quote("<b>"), "**")
                        .replaceAll(Pattern.quote("</b>"), "**")
                        .replaceAll("<a(?:.*)>(.*)<\\/a>", "$1");
                eb.setDescription(StringEscapeUtils.unescapeHtml4(bodyParsed));
                break;
            case "quote":
                String text = StringEscapeUtils.unescapeHtml4(post.text);
                String source = post.source;
                eb.setDescription("\"" + text + "\"");
                eb.appendDescription("\n\n - _ " + source + "_");
                break;
        }

        indexes.put(gid, ++index);
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
    public Category getCategory() {
        return Category.TEXT;
    }

    private void reloadQuotes() {
        List<Long> filterIds = Arrays.asList(badPostIds);
        oldCount = tumblrPosts.size();
        tumblrPosts.clear();
        indexes.clear();
        for (String type : types) {
            logger.info("Getting quotes from type " + type);
            WebUtils.ins.getAson(
                    String.format(
                            "https://api.tumblr.com/v2/blog/totallycorrectdannyphantomquotes.tumblr.com/posts?api_key=%s&type=%s",
                            SpoopyUtils.config.getString("api.tumblr", "API_KEY"),
                            type
                    )
            ).async(json -> {
                int total = json.getInt("response.total_posts");
                for(int i = 0; i <= total; i+=20 ) {
                    WebUtils.ins.getAson(
                            String.format(
                                    "https://api.tumblr.com/v2/blog/totallycorrectdannyphantomquotes.tumblr.com/posts" +
                                            "?api_key=%s&type=%s&limit=20&offset=%s",
                                    SpoopyUtils.config.getString("api.tumblr", "API_KEY"),
                                    type,
                                    i
                            )
                    ).async(j -> {
                        AsonArray<Ason> fetched = j.getJsonArray("response.posts");
                        logger.info("Got " + fetched.size() + " quotes from type " + type);
                        List<TumblrPost> posts = Ason.deserializeList(fetched, TumblrPost.class);
                        List<TumblrPost> filteredPosts = posts.stream().filter(post -> !filterIds.contains(post.id)).collect(Collectors.toList());
                        tumblrPosts.addAll(
                                filteredPosts
                        );
                        Collections.shuffle(tumblrPosts);
                    });
                }
            });
        }
    }
}
