package com.daki.lottalogs;

import com.daki.lottalogs.other.BukkitTaskCache;
import com.daki.lottalogs.other.Register;
import com.daki.lottalogs.other.Reload;
import org.bukkit.plugin.java.JavaPlugin;

public class LottaLogs extends JavaPlugin {

    private static LottaLogs instance;

    public static LottaLogs getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        LottaLogs.getInstance().getLogger().info("--------------------------------------------------");

        Reload.reload();

        Register.registerCommands();

        LottaLogs.getInstance().getLogger().info("LottaLogs started...");
        LottaLogs.getInstance().getLogger().info("--------------------------------------------------");

    }

}
