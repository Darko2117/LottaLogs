package com.daki.lottalogs;

import java.util.Map;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import com.daki.lottalogs.logs.Log;

public class Register {

    public static void registerEvents() {

        HandlerList.unregisterAll(LottaLogs.getInstance());

        for (Map.Entry<String, Log> entry : Logging.getCachedLogs().entrySet()) {
            if (entry.getValue().isEnabled()) {

                if (entry.getValue().getName().equals("PlayerLocationLog"))
                    continue;

                LottaLogs.getInstance().getServer().getPluginManager().registerEvents((Listener) entry.getValue(), LottaLogs.getInstance());

            }
        }

    }

    public static void registerCommands() {

        LottaLogs.getInstance().getCommand("reload").setExecutor(new ReloadCommand());
        LottaLogs.getInstance().getCommand("searchlogs").setExecutor(new LoggingSearch());

        LottaLogs.getInstance().getCommand("searchlogs").setTabCompleter(new LoggingSearch());

    }

}
