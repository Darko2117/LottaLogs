package com.daki.lottalogs;

import org.bukkit.plugin.java.JavaPlugin;
import com.daki.lottalogs.other.APIs;
import com.daki.lottalogs.other.Config;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Metrics;
import com.daki.lottalogs.other.Register;

public class LottaLogs extends JavaPlugin {

    private static LottaLogs instance;

    public static LottaLogs getInstance() {

        return instance;

    }

    @Override
    public void onLoad() {

        instance = this;

    }

    @Override
    public void onEnable() {

        LottaLogs.getInstance().getLogger().info("--------------------------------------------------");
        LottaLogs.getInstance().getLogger().info("LottaLogs starting...");

        long start = System.currentTimeMillis();

        Config.checkAndReloadConfig();
        Config.configSetup();

        Logging.initiate();

        APIs.connectAPIs();

        Register.registerEvents();
        Register.registerCommands();

        new Metrics(this, 19063);

        long end = System.currentTimeMillis();

        LottaLogs.getInstance().getLogger().info("LottaLogs loaded, took " + (end - start) + "ms.");
        LottaLogs.getInstance().getLogger().info("--------------------------------------------------");

    }

}
