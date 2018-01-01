package me.duncte123.ghostBot.utils;

import me.duncte123.ghostBot.CommandManager;
import me.duncte123.ghostBot.config.Config;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SpoopyUtils {
    public static final AudioUtils audio = new AudioUtils();
    public static final Config config = new ConfigUtils().loadConfig();
    public static final Random random = new Random();
    public static final ScheduledExecutorService service
            = Executors.newScheduledThreadPool(5, r -> new Thread(r, "Music-Shutdown-Thread"));

    public static final CommandManager commandManager = new CommandManager();
}

