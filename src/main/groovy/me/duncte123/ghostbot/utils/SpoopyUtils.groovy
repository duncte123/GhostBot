/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
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

package me.duncte123.ghostbot.utils

import gnu.trove.set.TLongSet
import gnu.trove.set.hash.TLongHashSet
import me.duncte123.ghostBot.objects.config.GhostBotConfig
import me.duncte123.botcommons.config.ConfigUtils
import me.duncte123.ghostbot.CommandManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.utils.cache.MemberCacheView

import java.nio.charset.StandardCharsets

class SpoopyUtils {

    static final GhostBotConfig config = ConfigUtils.loadFromFile("config.json", GhostBotConfig.class)
    static final AudioUtils audio = AudioUtils.instance
    static final CommandManager commandManager = new CommandManager()

    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1" +
            "?q=%s" +
            "&prettyPrint=false" +
            "&cx=012048784535646064391:v-fxkttbw54" +
            "&num=10" +
            "&hl=en" +
            "&searchType=image" +
            "&filter=1" +
            "&safe=medium" +
            "&key=$config.api.google"

    // [0] = users, [1] = bots
    static double[] getBotRatio(Guild g) {

        MemberCacheView memberCache = g.getMemberCache()
        double totalCount = memberCache.size()
        double botCount = memberCache.stream().filter { it.user.bot }.count()
        double userCount = totalCount - botCount

        //percent in users
        double userCountP = (userCount / totalCount) * 100

        //percent in bots
        double botCountP = (botCount / totalCount) * 100

        return new double[]{Math.round(userCountP), Math.round(botCountP)}
    }

    static TextChannel getPublicChannel(Guild guild) {

        TextChannel pubChann = guild.getTextChannelCache().getElementById(guild.getId())

        if (pubChann == null || !pubChann.canTalk()) {
            return guild.getTextChannelCache().stream().filter(TextChannel::canTalk).findFirst().orElse(null)
        }

        return pubChann
    }

    static String getGoogleSearchUrl(String query) {
        return String.format(GOOGLE_URL, encodeUrl(query))
    }

    static String encodeUrl(String inp) {
        return URLEncoder.encode(inp, StandardCharsets.UTF_8);
    }

    static TLongSet newLongSet(long... ids) {
        return new TLongHashSet(ids)
    }

    static boolean isLong(String input) {
        try {
            Long.parseUnsignedLong(input)
            return true
        } catch (NumberFormatException ignored) {
            return false
        }
    }
}
