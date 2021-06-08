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

package me.duncte123.ghostbot.commands.dannyphantom.image;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.duncte123.botcommons.messaging.MessageConfig;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.commands.ReactionCommand;
import me.duncte123.ghostbot.objects.command.CommandCategory;
import me.duncte123.ghostbot.objects.command.ICommandEvent;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import me.duncte123.ghostbot.objects.tumblr.TumblrPost;
import me.duncte123.ghostbot.utils.Container;
import me.duncte123.ghostbot.utils.TumblrUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.duncte123.botcommons.messaging.MessageUtils.sendMsg;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

abstract class TumblrComicBase extends ReactionCommand {
    private static final String PAGE_SELECTOR = "page:";
    private static final String CHAPTER_SELECTOR = "chapter:";
    final List<TumblrPost> pages = new ArrayList<>();
    int[] chapters;
    String filename;
    String blogUrl;

    TumblrComicBase(CommandManager.ReactionListenerRegistry registry) {
        super(registry);
    }

    @Override
    public void execute(ICommandEvent event) {
        final List<String> args = event.getArgs();
        final User author = event.getAuthor();

        if (pages.isEmpty()) {
            event.reply("Something went wrong with loading the pages, please notify duncte123#1245");
            return;
        }

        int page = 0;

        if (args.size() > 0) {

            if (event.isSlash()) {
                final OptionMapping pageOpt = event.getOption("page");
                final OptionMapping chapterOpt = event.getOption("chapter");

                if (pageOpt != null) {
                    page = (int) pageOpt.getAsLong();
                } else if (chapterOpt != null) {
                    page = chapters[(int) chapterOpt.getAsLong() - 1];
                }
            } else {
                final String arg = String.join("", args).toLowerCase();

                if (arg.startsWith(PAGE_SELECTOR)) {
                    page = getNumberFromArg(arg.substring(PAGE_SELECTOR.length()));
                } else if (arg.startsWith(CHAPTER_SELECTOR)) {
                    try {
                        page = chapters[getNumberFromArg(arg.substring(CHAPTER_SELECTOR.length())) - 1];
                    } catch (IndexOutOfBoundsException ignored) {
                        sendMsg(event, "That chapter is not known");

                        return;
                    }
                }
            }
        }

        if (page > 0) {
            page--;
        }

        if (page > pages.size()) {
            event.reply("I could not find a page with number " + (page + 1));
            return;
        }

        final long userId = author.getIdLong();
        final AtomicInteger pa = new AtomicInteger(page);
        final int startPage = pa.get();
        final MessageConfig messageConfig = new MessageConfig.Builder()
            .setChannel(event.getChannel())
            .setMessage("Use the emotes at the bottom to navigate through pages, use the ❌ emote when you are done reading.\n" +
                "The controls have a timeout of 30 minutes")
            .setEmbed(getEmbed(startPage))
            .configureMessageBuilder(
                (builder) -> builder.setActionRows(
                    LEFT_RIGHT_CANCEL.toActionRow(
                        userId,
                        startPage == 0,
                        startPage == pages.size() - 1
                    )
                )
            )
            .setSuccessAction((msg) -> this.enableButtons(msg, 30, TimeUnit.MINUTES,
                (btnEvent) -> {
                    final String buttonId = btnEvent.getComponentId();

                    if (buttonId.startsWith("cancel")) {
                        disableButtons(btnEvent);
                        return;
                    }

                    final int pageToDisplay = pa.updateAndGet(
                        (current) -> buttonId.startsWith("next") ? Math.min(current + 1, pages.size() - 1) : Math.max(current - 1, 0)
                    );

                    btnEvent.deferEdit()
                        .setEmbeds(getEmbed(pageToDisplay).build())
                        .setActionRows(
                            LEFT_RIGHT_CANCEL.toActionRow(
                                userId,
                                pageToDisplay == 0,
                                pageToDisplay == pages.size() - 1
                            )
                        )
                        .queue();
                })
            )
            .build();

        event.reply(messageConfig);
    }

    private int getNumberFromArg(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException ignored) {
            return pages.size();
        }
    }

    String getProfilePicture() {
        return "https://api.tumblr.com/v2/blog/" + blogUrl + "/avatar/48";
    }

    @Nonnull
    abstract EmbedBuilder getEmbed(int page);

    abstract Predicate<TumblrPost> getFilter();

    @Override
    public List<OptionData> getCommandOptions() {
        return List.of(
            new OptionData(INTEGER, "page", "Select the current page to view"),
            new OptionData(INTEGER, "chapter", "Go to a specific chapter, WILL BE IGNORED IF PAGE IS USED")
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.IMAGE;
    }

    void loadPages(Container container) {
        final String clsName = getClass().getSimpleName();
        final GhostBotConfig config = container.getConfig();
        final ObjectMapper mapper = container.getJackson();

        logger.info("Loading {} pages", clsName);

        final File comicFile = new File(filename);

        if (comicFile.exists()) {
            List<TumblrPost> posts = null;
            try {
                posts = mapper.readValue(comicFile, new TypeReference<>() {});
            } catch (IOException e) {
                logger.error("Failed to load " + clsName + " comic", e);
            }

            if (posts != null) {
                pages.addAll(posts);
                logger.info("(cached) Loaded {} pages from the {} comic.", pages.size(), clsName);
            }

            return;
        }

        final Predicate<TumblrPost> postFilter = getFilter();

        TumblrUtils.getInstance().fetchAllFromAccount(blogUrl, "photo", config, mapper, (posts) -> {
            Stream<TumblrPost> stream  = posts.stream();

            if (postFilter != null) {
                stream = stream.filter(postFilter);
            }

            final List<TumblrPost> ps = stream.collect(Collectors.toList());

            Collections.reverse(ps);

            pages.addAll(ps);
            logger.info("Loaded {} pages from the {} comic.", pages.size(), clsName);

            try {
                if (!comicFile.exists()) {
                    comicFile.createNewFile();
                }

                mapper.writeValue(comicFile, ps);
                logger.info("Wrote {} to json", clsName);
            } catch (Exception e) {
                logger.error("Failed to write " + clsName, e);
            }
        });
    }
}
