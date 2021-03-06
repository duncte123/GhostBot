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

package me.duncte123.ghostbot.objects.command;

import me.duncte123.botcommons.messaging.MessageConfig;
import me.duncte123.ghostbot.utils.Container;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JDASlashCommandEvent implements ICommandEvent {

    private final SlashCommandEvent event;
    private final Container container;
    private final InteractionHook hook;

    public JDASlashCommandEvent(SlashCommandEvent event, Container container) {
        this.event = event;
        this.container = container;
        this.hook = event.getHook().setEphemeral(false);
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public String getInvoke() {
        return this.event.getName();
    }

    @Override
    public List<String> getArgs() {
        return this.event.getOptions().stream()
            .filter((it) -> it.getType() == OptionType.STRING)
            .map(OptionMapping::getAsString)
            .collect(Collectors.toList());
    }

    @Override
    public OptionMapping getOption(String name) {
        return this.event.getOption(name);
    }

    @Override
    public Guild getGuild() {
        return this.event.getGuild();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return null;
    }

    @Override
    public TextChannel getChannel() {
        return this.event.getTextChannel();
    }

    @Override
    public Message getMessage() {
        return null;
    }

    @Override
    public User getAuthor() {
        return this.event.getUser();
    }

    @Override
    public Member getMember() {
        return this.event.getMember();
    }

    @Override
    public JDA getJDA() {
        return this.event.getJDA();
    }

    public SlashCommandEvent getSlashEvent() {
        return this.event;
    }

    @Override
    public void reply(String content) {
        if (this.event.isAcknowledged()) {
            this.hook.sendMessage(content).queue();
            return;
        }

        this.event.reply(content).setEphemeral(false).queue();
    }

    @Override
    public void reply(MessageConfig config) {
        if (!this.event.isAcknowledged()) {
            // be lazy, we can complete this cuz command thread
            event.deferReply().setEphemeral(false).complete();
        }

        final MessageBuilder messageBuilder = config.getMessageBuilder();
        final EmbedBuilder embed = config.getEmbed();

        if (embed != null) {
            messageBuilder.setEmbed(embed.build());
        }

        final Message message = messageBuilder.build();

        final Consumer<? super Message> successAction = config.getSuccessAction();
        final Consumer<? super Throwable> failureAction = config.getFailureAction();

        this.hook.sendMessage(message).queue(successAction, failureAction);
    }

    @Override
    public void reply(EmbedBuilder embed) {
        if (this.event.isAcknowledged()) {
            this.hook.sendMessageEmbeds(embed.build()).queue();
            return;
        }

        this.event.replyEmbeds(embed.build()).setEphemeral(false).queue();
    }
}
