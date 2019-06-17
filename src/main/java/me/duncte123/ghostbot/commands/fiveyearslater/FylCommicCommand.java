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

package me.duncte123.ghostbot.commands.fiveyearslater;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.commands.ReactionCommand;
import me.duncte123.ghostbot.objects.CommandEvent;
import me.duncte123.ghostbot.objects.fyl.FylChapter;
import me.duncte123.ghostbot.objects.fyl.FylComic;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
    private final FylComic comic;

    public FylCommicCommand(CommandManager.ReactionListenerRegistry registry, ObjectMapper jackson) {
        super(registry);

        FylComic tempComic;

        try {
            final String text = new String(Files.readAllBytes(new File("5yearslater_NEW.json").toPath()));
            tempComic = jackson.readValue(text, FylComic.class);
        } catch (IOException e) {
            tempComic = null;
            e.printStackTrace();
        }

        comic = tempComic;
    }

    @Override
    public void execute(CommandEvent event) {
        final List<String> args = event.getArgs();
        final User author = event.getAuthor();

        int page = 0;
        int chapter = 0;

        if (args.size() > 0) {
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

        final List<FylChapter> chapterList = comic.getChapters();

        if (chapter >= chapterList.size()) {
            sendMsg(event, "Chapter " + (chapter + 1) + " is not known");

            return;
        }

        final FylChapter fylChapter = chapterList.get(chapter);

        if (page > fylChapter.getPages()) {
            sendMsg(event, "Page " + page + " is not known in that chapter");
            return;
        }

        final AtomicInteger pageIndex = new AtomicInteger(page);
        final AtomicInteger chapterIndex = new AtomicInteger(chapter);
        final AtomicReference<FylChapter> chapterRef = new AtomicReference<>(fylChapter);

        sendMsg(event,
            new MessageBuilder()
                .append("Use the emotes at the bottom to navigate through pages, use the ❌ emote when you are done reading.\n")
                .append("The controls have a timeout of 30 minutes")
                .setEmbed(getEmbed(chapterIndex.get(), pageIndex.get()))
                .build(),
            (m) -> this.addReactions(m, LEFT_RIGHT_CANCEL,
                newLongSet(author.getIdLong()), 30, TimeUnit.MINUTES, (index) -> {

                    if (index >= 2) { //cancel button or other error
                        stopReactions(m, false);
                        return;
                    }

                    FylChapter chap = chapterRef.get();
                    final int totalPagesChap = chap.getPages();
                    int nextPage = pageIndex.updateAndGet(
                        (current) -> index == 1 ? Math.min(current + 1, totalPagesChap) : Math.max(current - 1, -1)
                    );

                    if ((nextPage + 1) > chap.getPages()) {
                        nextPage = pageIndex.updateAndGet((__) -> 0);
                        int i = chapterIndex.incrementAndGet();
                        chap = chapterRef.updateAndGet((__) -> chapterList.get(i));
                    } else if (nextPage == -1) {
                        int i = chapterIndex.decrementAndGet();

                        if (i > -1) {
                            chap = chapterRef.updateAndGet((__) -> chapterList.get(i));
                            final int chapPages = chap.getPages();
                            nextPage = pageIndex.updateAndGet((__) -> chapPages - 1);
                        } else {
                            chapterIndex.updateAndGet((__) -> 0);
                        }
                    }

                    if (nextPage >= 0 && nextPage <= chap.getPages()) {
                        m.editMessage(getEmbed(chapterIndex.get(), nextPage)).queue();
                    }
                }
            )
        );
    }

    private MessageEmbed getEmbed(int numChapter, int numPage) {
        final FylChapter chapter = comic.getChapters().get(numChapter);
        final String page = chapter.getPagesUrl().get(numPage);

        String url = comic.getBaseUrl() + chapter.getPageId() + '/' + page;

        if (comic.isUseWixUrl()) {
            url = comic.getWixUrl() + page.substring(2);
        }

        return EmbedUtils.defaultEmbed()
            .setImage(url)
            .setThumbnail(FYL_ICON)
            .setTitle("Chapter: " + chapter.getName(), chapter.getChapterUrl())
            .setTimestamp(null)
            .setFooter(String.format("Chapter: %s, PageL %s/%s", numChapter + 1, numPage + 1, chapter.getPages()), Variables.FOOTER_ICON)
            .build();
    }

    @Override
    public String getName() {
        return "5yl";
    }

    @Override
    public String getHelp() {
        return "Read the Five years later comic within discord (comic website: <http://kurothewebsite.com/5yearslater>)\n" +
            "Usage: `gb." + getName() + " [page:number/chapter:number]`";
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "5ylcomic",
            "fiveyearslater",
            "fiveyearslatercomic"
        );
    }

    private static int getNumberFromArg(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
