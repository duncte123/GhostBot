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

package me.duncte123.ghostBot.utils;

import me.duncte123.botCommons.config.Config;
import me.duncte123.ghostBot.CommandManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.cache.MemberCacheView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

public class SpoopyUtils {
    public static final AudioUtils audio = AudioUtils.ins;
    public static final Config config = new ConfigUtils().loadConfig();
    public static final Random random = new Random();

    public static final WikiHolder WIKI_HOLDER = new WikiHolder("https://dannyphantom.wikia.com");

    public static final CommandManager commandManager = new CommandManager();

    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1" +
            "?q=%s" +
            "&prettyPrint=false" +
            "&cx=012048784535646064391:v-fxkttbw54" +
            //"&num=100" +
            "&hl=en" +
            "&searchType=image" +
            "&key=" + config.getString("api.google");

    // [0] = users, [1] = bots
    public static double[] getBotRatio(Guild g) {

        MemberCacheView memberCache = g.getMemberCache();
        double totalCount = memberCache.size();
        double botCount = memberCache.stream().filter(it -> it.getUser().isBot()).count();
        double userCount = totalCount - botCount;

        //percent in users
        double userCountP = (userCount / totalCount) * 100;

        //percent in bots
        double botCountP = (botCount / totalCount) * 100;

        return new double[]{Math.round(userCountP), Math.round(botCountP)};
    }

    public static TextChannel getPublicChannel(Guild guild) {

        TextChannel pubChann = guild.getTextChannelCache().getElementById(guild.getId());

        if (pubChann == null || !pubChann.canTalk()) {
            return guild.getTextChannelCache().stream().filter(TextChannel::canTalk).findFirst().orElse(null);
        }

        return pubChann;
    }

    public static String getGoogleSearchUrl(String query) {
        return String.format(GOOGLE_URL, encodeUrl(query));
    }

    public static String encodeUrl(String in) {
        try {
            return URLEncoder.encode(in, "UTF-8");
        } catch (UnsupportedEncodingException exc) {
            exc.printStackTrace();
            return in.replace(" ", "%20");
        }
    }
}

