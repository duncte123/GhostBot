/*
 *     GhostBot, a Discord bot made for all your Danny Phantom needs
 *     Copyright (C) 2018 - 2019  Duncan "duncte123" Sterken
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

package me.duncte123.ghostbot.utils;

import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.cache.MemberCacheView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SpoopyUtils {

    private static final String GOOGLE_URL = "https://www.googleapis.com/customsearch/v1" +
        "?q=%s" +
        "&prettyPrint=false" +
        "&cx=012048784535646064391:v-fxkttbw54" +
        "&num=10" +
        "&hl=en" +
        "&searchType=image" +
        "&filter=1" +
        "&safe=medium" +
        "&key=%s";

    // [0] = users, [1] = bots
    public static double[] getBotRatio(Guild g) {

        final MemberCacheView memberCache = g.getMemberCache();
        final double totalCount = memberCache.size();
        final double botCount = memberCache.stream().filter((it) -> it.getUser().isBot()).count();
        final double userCount = totalCount - botCount;

        //percent in users
        final double userCountP = (userCount / totalCount) * 100;

        //percent in bots
        final double botCountP = (botCount / totalCount) * 100;

        return new double[]{Math.round(userCountP), Math.round(botCountP)};
    }

    public static TextChannel getPublicChannel(Guild guild) {

        final TextChannel pubChann = guild.getTextChannelById(guild.getId());

        if (pubChann == null || !pubChann.canTalk()) {
            return guild.getTextChannelCache()
                .applyStream(
                    (stream) -> stream.filter(TextChannel::canTalk).findFirst().orElse(null)
                );
        }

        return pubChann;
    }

    public static String getGoogleSearchUrl(String query, String key) {
        return String.format(GOOGLE_URL, encodeUrl(query), key);
    }

    public static String encodeUrl(String inp) {
        return URLEncoder.encode(inp, StandardCharsets.UTF_8);
    }

    public static TLongSet newLongSet(long... ids) {
        return new TSynchronizedLongSet(new TLongHashSet(ids), new Object());
    }

    public static boolean isLong(String input) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Long.parseUnsignedLong(input);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

}
