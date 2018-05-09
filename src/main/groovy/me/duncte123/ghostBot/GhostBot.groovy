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

package me.duncte123.ghostBot

import fredboat.audio.player.LavalinkManager
import me.duncte123.ghostBot.utils.SpoopyUtils
import me.duncte123.ghostBot.variables.Variables
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import org.slf4j.LoggerFactory

import javax.security.auth.login.LoginException

class GhostBot {

    private static final def logger = LoggerFactory.getLogger(GhostBot.class)

    static final GhostBot instance = new GhostBot()
    private static JDA jda

    static void main(String[] args) {
        //Just some testing
        /*String usn = "allyphantomrush"
        WebUtils.ins.getText("https://backend.deviantart.com/rss.xml?type=deviation&q=by%3A" +
                usn + "+sort%3Atime+meta%3Aall").async(txt -> {
            Document doc = Jsoup.parse(txt, "", Parser.xmlParser())
            Elements items = doc.select("item")
            items.forEach(item -> System.out.println(item.selectFirst("link").text()))
            //Use https://backend.deviantart.com/oembed?url= on the returned url
        });*/

        logger.info("Booting GhostBot")
        String token = SpoopyUtils.CONFIG.getString("discord.token")
        LavalinkManager.ins.start()
        try {
            def builder = new JDABuilder(AccountType.BOT)
                    .setAudioEnabled(true)
                    .setGame(Game.playing("${Variables.PREFIX}help | Going Ghost"))
                    .setToken(token)
                    .addEventListener(new BotListener())

            if (LavalinkManager.ins.isEnabled())
                builder.addEventListener(LavalinkManager.ins.getLavalink())

            jda = builder.buildAsync()
        } catch (LoginException e) {
            e.printStackTrace()
            System.exit(-1)
        }
    }

    @SuppressWarnings(["GroovyUnusedDeclaration", "GrMethodMayBeStatic"])
    JDA getFakeShard(int shardId) {
        return jda
    }

    private GhostBot() {}
}
