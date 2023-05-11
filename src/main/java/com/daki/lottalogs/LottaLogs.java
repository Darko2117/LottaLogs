package com.daki.lottalogs;

import org.bukkit.plugin.java.JavaPlugin;

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

        Config.checkAndReloadConfig();
        Config.configSetup();

        Logging.initiate();

        APIs.connectAPIs();

        Register.registerEvents();
        Register.registerCommands();

        LottaLogs.getInstance().getLogger().info("--------------------------------------------------");

    }

}
