package me.duncte123.ghostBot;

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

        try {
            new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(true)
                    .setGame(Game.playing("Going Ghost"))
                    .setToken("MjE1MDExOTkyMjc1MTI0MjI1.DPHwAg.LQuz8_Qb09uz0jHEk5bteR00ww0")
                    .addEventListener(new BotListener())
                    .buildAsync();
        }
        catch (LoginException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }


}
