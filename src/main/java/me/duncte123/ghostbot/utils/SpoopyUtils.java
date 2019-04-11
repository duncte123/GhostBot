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

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import me.duncte123.botcommons.config.ConfigUtils;
import me.duncte123.ghostbot.CommandManager;
import me.duncte123.ghostbot.objects.config.GhostBotConfig;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.cache.MemberCacheView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SpoopyUtils {

    private static final GhostBotConfig config;
    private static final String GOOGLE_URL;

    static {
        String tempGoogle;
        GhostBotConfig tempConfig;

        try {
            tempConfig = ConfigUtils.loadFromFile("config.json", GhostBotConfig.class);
            tempGoogle = "https://www.googleapis.com/customsearch/v1" +
                "?q=%s" +
                "&prettyPrint=false" +
                "&cx=012048784535646064391:v-fxkttbw54" +
                "&num=10" +
                "&hl=en" +
                "&searchType=image" +
                "&filter=1" +
                "&safe=medium" +
                "&key=" + tempConfig.api.google;

        } catch (IOException e) {
            tempConfig = null;
            tempGoogle = "";
            e.printStackTrace();
        }

        GOOGLE_URL = tempGoogle;
        config = tempConfig;
    }

    private static final ObjectMapper jackson = new ObjectMapper();
    private static final AudioUtils audio = AudioUtils.getInstance();
    private static final CommandManager commandManager = new CommandManager();

    public static GhostBotConfig getConfig() {
        return config;
    }

    public static AudioUtils getAudio() {
        return audio;
    }

    public static ObjectMapper getJackson() {
        return jackson;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

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
            return guild.getTextChannelCache().stream().filter(TextChannel::canTalk).findFirst().orElse(null);
        }

        return pubChann;
    }

    public static String getGoogleSearchUrl(String query) {
        return String.format(GOOGLE_URL, encodeUrl(query));
    }

    public static String encodeUrl(String inp) {
        return URLEncoder.encode(inp, StandardCharsets.UTF_8);
    }

    public static TLongSet newLongSet(long... ids) {
        return new TLongHashSet(ids);
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
