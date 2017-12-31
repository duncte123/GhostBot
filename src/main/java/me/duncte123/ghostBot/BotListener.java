package me.duncte123.ghostBot;

import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(BotListener.class);

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Logged in as " + String.format("%#s", event.getJDA().getSelfUser()));
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot() || event.getAuthor().isFake()) return;
        if(!event.getMessage().getContentRaw().startsWith(Variables.PREFIX)) return;

        if(event.getMessage().getContentRaw().equals(Variables.PREFIX + "shutdown") && event.getAuthor().getId().equals(Variables.OWNER_ID)) {
            logger.info("Shutting down!!");
            event.getMessage().addReaction("✅").queue(
                    //Shutdown on both success and failure
                    success -> event.getJDA().shutdown(),
                    failure -> event.getJDA().shutdown()
            );
            return;
        }

        SpoopyUtils.commandManager.handleCommand(event);
    }
}
