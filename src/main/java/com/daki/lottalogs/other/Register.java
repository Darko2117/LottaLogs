package com.daki.lottalogs.other;

import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logging.listeners.LoggingCrazyCrates;
import com.daki.lottalogs.logging.listeners.LoggingFarmLimiter;
import com.daki.lottalogs.logging.listeners.LoggingGriefPrevention;
import com.daki.lottalogs.logging.listeners.LoggingMyPet;
import com.daki.lottalogs.logging.listeners.LoggingNoAPI;
import com.daki.lottalogs.logging.LoggingSearch;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Register {

    public static void registerEvents() {

        HandlerList.unregisterAll(LottaLogs.getInstance());

        registerEvents(new LoggingNoAPI());
        registerEvents(new LoggingCrazyCrates());
        registerEvents(new LoggingFarmLimiter());
        registerEvents(new LoggingGriefPrevention());
        registerEvents(new LoggingMyPet());

    }

    private static void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            LottaLogs.getInstance().getServer().getPluginManager().registerEvents(listener, LottaLogs.getInstance());
        }
    }

    public static void registerCommands() {

        LottaLogs.getInstance().getCommand("searchlogs").setExecutor(new LoggingSearch());
        LottaLogs.getInstance().getCommand("searchlogs").setTabCompleter(new LoggingSearch());
        LottaLogs.getInstance().getCommand("lottalogsreload").setExecutor(new Reload());

    }

}
