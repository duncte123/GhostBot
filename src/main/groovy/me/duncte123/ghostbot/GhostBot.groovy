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

package me.duncte123.ghostbot

import fredboat.audio.player.LavalinkManager
import me.duncte123.botcommons.messaging.EmbedUtils
import me.duncte123.botcommons.web.WebUtils
import me.duncte123.ghostbot.kuroslounge.FilterLogs
import me.duncte123.ghostbot.utils.SpoopyUtils
import me.duncte123.ghostbot.variables.Variables
import me.duncte123.ghostbotslack.GhostBotSlack
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.utils.cache.CacheFlag
import org.slf4j.LoggerFactory

import java.time.Instant

class GhostBot {

    static GhostBot instance
    final ShardManager shardManager
    private GhostBotSlack slack

    GhostBot() {
        def logger = LoggerFactory.getLogger(GhostBot.class)
        this.slack = new GhostBotSlack()

        logger.info('Booting GhostBot')
        def token = SpoopyUtils.config.discord.token
        def totalShards = SpoopyUtils.config.discord.totalShards
        WebUtils.userAgent = "Mozilla/5.0 (compatible; GhostBot/v${Variables.VERSION}; +https://github.com/duncte123/GhostBot)"
        EmbedUtils.setEmbedBuilder {
            return new EmbedBuilder()
                    .setColor(0x6ffe32)
                    .setFooter("GhostBot", Variables.FOOTER_ICON)
                    .setTimestamp(Instant.now())
        }

        LavalinkManager.ins.start()
        def botListener = new BotListener(slack)
        def filterLogs = new FilterLogs()
        def builder = new DefaultShardManagerBuilder()
                .setShardsTotal(totalShards)
                .setToken(token)
//                .setGame(Game.watching("${Variables.PREFIX}help | #GoGhostAgain"))
                .setGame(Game.playing('GhostBot 2.0 | Now with popup blocker'))
                .setDisabledCacheFlags(EnumSet.of(CacheFlag.EMOTE, CacheFlag.GAME))
                .addEventListeners(botListener, filterLogs)


        if (LavalinkManager.ins.enabled) {
            builder.addEventListeners(LavalinkManager.ins.lavalink)
        }

        shardManager = builder.build()
    }

    static void main(String[] args) {
        instance = new GhostBot()
    }

    JDA getShard(int shardId) {
        return shardManager.getShardById(shardId)
    }
}
