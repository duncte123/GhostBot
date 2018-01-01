package me.duncte123.ghostBot;

import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class GhostBot {

    private static final Logger logger = LoggerFactory.getLogger(GhostBot.class);


    public static void main(String[] args) throws Exception{

        logger.info("Booting GhostBot");
        String token = SpoopyUtils.config.getString("discord.token");

        try {
            new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(true)
                    .setGame(Game.playing(Variables.PREFIX + "help | Going Ghost"))
                    .setToken(token)
                    .addEventListener(new BotListener())
                    .buildAsync();
        }
        catch (LoginException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }


}
