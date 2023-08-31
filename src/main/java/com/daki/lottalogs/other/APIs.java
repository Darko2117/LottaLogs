package com.daki.lottalogs.other;

import java.util.HashMap;
import org.bukkit.Bukkit;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.daki.lottalogs.LottaLogs;
import de.Keyle.MyPet.MyPetPlugin;
import lombok.Getter;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class APIs {

    @Getter
    private static final HashMap<String, Boolean> foundAPIsForLogs = new HashMap<>();

    @Getter
    private static CrazyCrates crazyCratesAPI;
    @Getter
    private static boolean farmLimiterFound;
    @Getter
    private static GriefPrevention griefPreventionAPI;
    @Getter
    private static MyPetPlugin myPetAPI;

    public static void connectAPIs() {

        connectCrazyCrates();
        sendFoundMessage("CrazyCrates", crazyCratesAPI != null);
        foundAPIsForLogs.put("CrazyCratesCratePrizesLog", crazyCratesAPI != null);

        connectFarmLimiter();
        sendFoundMessage("FarmLimiter", farmLimiterFound);
        foundAPIsForLogs.put("FarmLimiterLog", farmLimiterFound);

        connectGriefPrevention();
        sendFoundMessage("GriefPrevention", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsCreatedLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsDeletedLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsExpiredLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsResizedLog", griefPreventionAPI != null);

        connectMyPet();
        sendFoundMessage("MyPet", myPetAPI != null);
        foundAPIsForLogs.put("MyPetItemPickupLog", myPetAPI != null);

    }

    private static void connectCrazyCrates() {
        try {

            crazyCratesAPI = (CrazyCrates) Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");

        } catch (Throwable ignored) {
        }
    }

    private static void connectFarmLimiter() {
        try {

            farmLimiterFound = (Bukkit.getServer().getPluginManager().getPlugin("FarmLimiter") != null);

        } catch (Throwable ignored) {
        }
    }

    private static void connectGriefPrevention() {
        try {

            griefPreventionAPI = (GriefPrevention) Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");

        } catch (Throwable ignored) {
        }
    }

    private static void connectMyPet() {
        try {

            myPetAPI = (MyPetPlugin) Bukkit.getServer().getPluginManager().getPlugin("MyPet");

        } catch (Throwable ignored) {
        }
    }

    private static void sendFoundMessage(String pluginName, boolean found) {

        if (found) {
            LottaLogs.getInstance().getLogger().info(ConsoleColors.BLUE_BRIGHT + pluginName + " found!" + ConsoleColors.RESET);
        } else {
            LottaLogs.getInstance().getLogger().info(ConsoleColors.RED + pluginName + " not found!" + ConsoleColors.RESET);
        }

    }

}
