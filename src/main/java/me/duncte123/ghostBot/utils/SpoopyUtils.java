package me.duncte123.ghostBot.utils;

import me.duncte123.fandomApi.FandomApi;
import me.duncte123.ghostBot.CommandManager;
import me.duncte123.ghostBot.config.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.cache.MemberCacheView;

import java.util.Random;

public class SpoopyUtils {
    public static final AudioUtils audio = new AudioUtils();
    public static final Config config = new ConfigUtils().loadConfig();
    public static final Random random = new Random();
    public static final FandomApi FANDOM_API = new FandomApi("http://dannyphantom.wikia.com");

    public static final CommandManager commandManager = new CommandManager();

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
}

