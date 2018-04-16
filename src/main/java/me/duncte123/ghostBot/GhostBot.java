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

package me.duncte123.ghostBot;

import fredboat.audio.player.LavalinkManager;
import me.duncte123.ghostBot.utils.SpoopyUtils;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class GhostBot {

    private static final Logger logger = LoggerFactory.getLogger(GhostBot.class);

    private static final GhostBot ins = new GhostBot();
    private static JDA jda;


    public static void main(String[] args) throws Exception {
        logger.info("Booting GhostBot");
        String token = SpoopyUtils.config.getString("discord.token");
        LavalinkManager.ins.start();
        try {
            JDABuilder builder = new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(true)
                    .setGame(Game.playing(Variables.PREFIX + "help | Going Ghost"))
                    .setToken(token)
                    .addEventListener(new BotListener());

            if(LavalinkManager.ins.isEnabled())
                builder.addEventListener(LavalinkManager.ins.getLavalink());

                    jda = builder.buildAsync();
        } catch (LoginException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    public JDA getFakeShard(int shardId) {
        return jda;
    }

    public static GhostBot getInstance() {
        return ins;
    }
}
