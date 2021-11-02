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

package me.duncte123.ghostbot.commands.fiveyearslater;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.messaging.MessageConfig;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.commands.ReactionCommand;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.objects.fyl.FylChapter;
import me.duncte123.ghostbot.objects.fyl.FylComic;
import me.duncte123.ghostbot.variables.Variables;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class FylCommicCommand extends ReactionCommand {
    private static final String PAGE_SELECTOR = "page:";
    private static final String CHAPTER_SELECTOR = "chapter:";
    private static final String FYL_ICON = "https://ghostbot.duncte123.me/img/5YL_Logo_2020.png";
    private final FylComic comic;

    public FylCommicCommand(CommandManager.ReactionListenerRegistry registry, ObjectMapper jackson) {
        super(registry);

        FylComic tempComic;

        try {
            final String text = new String(Files.readAllBytes(new File("./data/5yearslater_NEW.json").toPath()));
            tempComic = jackson.readValue(text, FylComic.class);
        } catch (IOException e) {
            tempComic = null;
            e.printStackTrace();
        }

        comic = tempComic;
    }

    @Override
    public void execute(ICommandEvent event) {
        final User author = event.getAuthor();
        final Pair<Integer, Integer> pageAndChapter = getPageAndChapter(event);

        int page = pageAndChapter.getLeft();
        int chapter = pageAndChapter.getRight();

        if (page > 0) {
            page--;
        }

        if (chapter > 0) {
            chapter--;
        }

        final List<FylChapter> chapterList = comic.getChapters();

        if (chapter < 0 || chapter >= chapterList.size()) {
            event.reply("Chapter " + (chapter + 1) + " is not known");
            return;
        }

        final FylChapter fylChapter = chapterList.get(chapter);

        if (page < 0 || page > fylChapter.getPages()) {
            event.reply("Page " + page + " is not known in that chapter");
            return;
        }

        final long userId = author.getIdLong();
        final AtomicInteger pageIndex = new AtomicInteger(page);
        final AtomicInteger chapterIndex = new AtomicInteger(chapter);
        final AtomicReference<FylChapter> chapterRef = new AtomicReference<>(fylChapter);
        final MessageConfig messageConfig = new MessageConfig.Builder()
            .setChannel(event.getChannel())
            .setMessage("Use the emotes at the bottom to navigate through pages, use the ❌ emote when you are done reading.\n" +
                "The controls have a timeout of 30 minutes")
            .setEmbeds(getEmbed(chapterIndex.get(), pageIndex.get()))
            .configureMessageBuilder(
                (builder) -> {
                    final int chapI = chapterIndex.get();
                    final int pageI = pageIndex.get();

                    builder.setActionRows(
                        LEFT_RIGHT_CANCEL.toActionRow(
                            userId,
                            chapI == 0 && pageI == 0,
                            chapI == chapterList.size() - 1 && pageI == fylChapter.getPages() - 1
                        )
                    );
                }
            )
            .setSuccessAction(
                (msg) -> this.enableButtons(msg, 30, TimeUnit.MINUTES, (btnEvent) -> {
                    final String buttonId = btnEvent.getComponentId();

                    //  something that can never happen or cancel button
                    if (buttonId.startsWith("cancel")) {
                        disableButtons(btnEvent);
                        return;
                    }

                    FylChapter chap = chapterRef.get();
                    final int totalPagesChap = chap.getPages();
                    int displayPage = pageIndex.updateAndGet(
                        (current) -> buttonId.startsWith("next") ? Math.min(current + 1, totalPagesChap) : Math.max(current - 1, -1)
                    );

                    if ((displayPage + 1) > chap.getPages()) {
                        displayPage = pageIndex.updateAndGet((__) -> 0);
                        int i = chapterIndex.incrementAndGet();
                        chap = chapterRef.updateAndGet((__) -> chapterList.get(i));
                    } else if (displayPage == -1) {
                        int i = chapterIndex.decrementAndGet();

                        if (i > -1) {
                            chap = chapterRef.updateAndGet((__) -> chapterList.get(i));
                            final int chapPages = chap.getPages();
                            displayPage = pageIndex.updateAndGet((__) -> chapPages - 1);
                        } else {
                            chapterIndex.set(0);
                        }
                    }

                    if (displayPage >= 0 && displayPage <= chap.getPages()) {
                        final int displayChapter = chapterIndex.get();
                        btnEvent.deferEdit()
                            .setEmbeds(getEmbed(displayChapter, displayPage).build())
                            .setActionRows(
                                LEFT_RIGHT_CANCEL.toActionRow(
                                    userId,
                                    displayChapter == 0 && displayPage == 0,
                                    displayChapter == chapterList.size() - 1 && displayPage == totalPagesChap - 1
                                )
                            )
                            .queue();
                    } else {
                        // reset the page index
                        pageIndex.set(0);
                        btnEvent.deferReply(true)
                            .setContent("Invalid page")
                            .queue();
                    }
                })
            )
            .build();

        event.reply(messageConfig);
    }

    private EmbedBuilder getEmbed(int numChapter, int numPage) {
        final FylChapter chapter = comic.getChapters().get(numChapter);
        final String page = chapter.getPagesUrl().get(numPage);

        String url = comic.getBaseUrl() + chapter.getPageId() + '/' + page;

        if (comic.isUseWixUrl()) {
            url = comic.getWixUrl() + page.substring(2);
        }

        return EmbedUtils.getDefaultEmbed()
            .setImage(url)
            .setThumbnail(FYL_ICON)
            .setTitle("Chapter: " + chapter.getName(), chapter.getChapterUrl())
            .setTimestamp(null)
            .setFooter(String.format("Chapter: %s, Page: %s/%s", numChapter + 1, numPage + 1, chapter.getPages()), Variables.FOOTER_ICON);
    }

    @Override
    public String getName() {
        return "5yl";
    }

    @Override
    public String getHelp() {
        return "Read the Five years later comic within discord (website: <https://www.theinktank.co/5yearslater>)\n" +
            "Usage: `gb." + getName() + " [page:number/chapter:number]`";
    }

    private Pair<Integer, Integer> getPageAndChapter(ICommandEvent event) {
        int page = 0;
        int chapter = 0;

        final OptionMapping pageOpt = event.getOption("page");

        if (pageOpt != null) {
            page = (int) pageOpt.getAsLong();
        }

        final OptionMapping chapterOpt = event.getOption("chapter");

        if (chapterOpt != null) {
            chapter = (int) chapterOpt.getAsLong();
        }

        return Pair.of(page, chapter);
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(
            new OptionData(INTEGER, "page", "select the page to start at"),
            new OptionData(INTEGER, "chapter", "select the chapter to start at")
        );
    }

    @Override
    public List<String> getAliases() {
        return List.of(
            "fyl",
            "5ylcomic",
            "fiveyearslater",
            "fiveyearslatercomic"
        );
    }
}
