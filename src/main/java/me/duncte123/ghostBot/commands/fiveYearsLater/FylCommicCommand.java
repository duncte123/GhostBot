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

package me.duncte123.ghostBot.commands.fiveYearsLater;

import com.google.gson.Gson;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.messaging.MessageUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostBot.commands.ReactionCommand;
import me.duncte123.ghostBot.objects.fyl.FylChapter;
import me.duncte123.ghostBot.objects.fyl.FylComic;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static me.duncte123.ghostbot.utils.SpoopyUtils.newLongSet;

public class FylCommicCommand extends ReactionCommand {

    private static final String PAGE_SELECTOR = "page:";
    private static final String CHAPTER_SELECTOR = "chapter:";
    private static final String FYL_ICON = "https://cdn.discordapp.com/emojis/374708234772283403.png?v=1";
    private FylComic comic;

    public FylCommicCommand(CommandManager.ReactionListenerRegistry registry) {
        super(registry);
        File conf = new File("5yearslater_NEW.json");
        try {
            FileReader file = new FileReader(conf);
            this.comic = new Gson().fromJson(file, FylComic.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(String invoke, String[] args, GuildMessageReceivedEvent event) {

        int page = 0;
        int chapter = 0;
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith(PAGE_SELECTOR)) {
                    page = getNumberFromArg(arg.substring(PAGE_SELECTOR.length()));
                } else if (arg.startsWith(CHAPTER_SELECTOR)) {
                    chapter = getNumberFromArg(arg.substring(CHAPTER_SELECTOR.length())) - 1;
                }
            }

        }

        if (page > 0) {
            page--;
        }

        List<FylChapter> chapterList = comic.chapters;

        if (chapter >= chapterList.size()) {
            sendMsg(event, "Chapter " + (chapter + 1) + " is not known");
            return;
        }

        FylChapter fylChapter = chapterList.get(chapter);

        if (page > fylChapter.pages) {
            sendMsg(event, "Page  " + page + " is not known in that chapter");
            return;
        }

        AtomicInteger pageIndex = new AtomicInteger(page);
        AtomicInteger chapterIndex = new AtomicInteger(chapter);
        AtomicReference<FylChapter> chapterRef = new AtomicReference<>(fylChapter);

        MessageUtils.sendMsg(event,
                new MessageBuilder()
                        .append("Use the emotes at the bottom to navigate through pages, use the ❌ emote when you are done reading.\n")
                        .append("The controls have a timeout of 30 minutes")
                        .setEmbed(getEmbed(chapterIndex.get(), pageIndex.get()))
                        .build(),
                m -> this.addReactions(m, Arrays.asList(ReactionCommand.LEFT_ARROW, ReactionCommand.RIGHT_ARROW,
                        ReactionCommand.CANCEL), newLongSet(event.getAuthor().getIdLong()), 30, TimeUnit.MINUTES, index -> {
                            if (index >= 2) { //cancel button or other error
                                stopReactions(m, false);
                                return;
                            }
                            FylChapter chap = chapterRef.get();
                            int nextPage = pageIndex.updateAndGet(current -> index == 1 ? Math.min(current + 1, chap.pages) : Math.max(current - 1, -1));

                            if ((nextPage + 1) > chap.pages) {
                                nextPage = pageIndex.updateAndGet(c -> 0);
                                int i = chapterIndex.incrementAndGet();
                                chapterRef.updateAndGet(c -> chapterList.get(i));
                            } else if (nextPage == -1) {
                                int i = chapterIndex.decrementAndGet();

                                if (i > -1) {
                                    FylChapter captt = chapterRef.updateAndGet(c -> chapterList.get(i));
                                    nextPage = pageIndex.updateAndGet(c -> captt.pages - 1);
                                } else {
                                    chapterIndex.updateAndGet(c -> 0);
                                }
                            }

                            if (nextPage >= 0 && nextPage <= chap.pages)
                                m.editMessage(getEmbed(chapterIndex.get(), nextPage)).queue();
                        }
                )
        );

    }

    private int getNumberFromArg(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private MessageEmbed getEmbed(int numChapter, int numPage) {
        final FylChapter chapter = comic.chapters.get(numChapter);
        final String page = chapter.pages_url.get(numPage);
        String url = comic.baseUrl + chapter.page_id + "/" + page;

        if (comic.useWixUrl) {
            url = comic.wixUrl + page.substring(2);
        }

        return EmbedUtils.defaultEmbed()
                .setImage(url)
                .setThumbnail(FYL_ICON)
                .setTitle("Chapter: " + chapter.name, chapter.chapter_url)
                .setTimestamp(null)
                .setFooter(String.format("Chapter: %s, Page: %s/%s", numChapter + 1, numPage + 1, chapter.pages), Variables.FOOTER_ICON)
                .build();
    }


    @Override
    public String getName() {
        return "5yl";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"5ylcomic", "fiveyearslater", "fiveyearslatercomic"};
    }

    @Override
    public String getHelp() {
        return "Read the Five years later comic within discord (comic website: <http://kurothewebsite.com/5yearslater>)\n" +
                "Usage: `gb." + getName() + " [page:number/chapter:number]`";
    }
}
