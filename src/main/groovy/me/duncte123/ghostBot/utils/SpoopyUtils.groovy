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

package me.duncte123.ghostBot.utils

import me.duncte123.botCommons.config.Config
import me.duncte123.ghostBot.CommandManager
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.TextChannel

class SpoopyUtils {

    //private vars
    private static final def CU = new ConfigUtils()
    private static String GOOGLE_URL = null

    //public vars
    static final def AUDIO = AudioUtils.ins
    static final Config CONFIG = CU.config
    static final Config IMAGES = CU.images
    static final def RANDOM = new Random()
    static final def WIKI_HOLDER = new WikiHolder("https://dannyphantom.wikia.com")
    static final def COMMAND_MANAGER = new CommandManager()

    static {
//        WIKI_HOLDER.domain = "https://dannyphantom.wikia.com"
        GOOGLE_URL = "https://www.googleapis.com/customsearch/v1" +
                "?q=%s" +
                "&prettyPrint=false" +
                "&cx=012048784535646064391:v-fxkttbw54" +
                "&num=10" +
                "&hl=en" +
                "&searchType=image" +
                "&filter=1" +
                "&safe=medium" +
                "&key=${CONFIG.getString("api.google")}"
    }

    // [0] = users, [1] = bots
    static double[] getBotRatio(Guild g) {

        def memberCache = g.memberCache
        double totalCount = memberCache.size()
        double botCount = memberCache.stream().filter { it.user.bot }.count()
        double userCount = totalCount - botCount

        //percent in users
        double userCountP = (userCount / totalCount) * 100

        //percent in bots
        double botCountP = (botCount / totalCount) * 100

        return [ Math.round(userCountP), Math.round(botCountP) ]
    }

    static TextChannel getPublicChannel(Guild guild) {

        def pubChann = guild.textChannelCache.getElementById(guild.getId())

        if (pubChann == null || !pubChann.canTalk()) {
            return guild.textChannelCache.stream().filter { it.canTalk() } .findFirst().orElse(null)
        }

        return pubChann
    }

    static String getGoogleSearchUrl(String query) {
        return String.format(GOOGLE_URL, encodeUrl(query))
    }

    static String encodeUrl(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8")
        } catch (UnsupportedEncodingException exc) {
            exc.printStackTrace()
            return input.replace(" ", "%20")
        }
    }

}