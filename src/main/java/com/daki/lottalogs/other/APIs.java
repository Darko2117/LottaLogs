package com.daki.lottalogs.other;

import java.util.HashMap;
import org.bukkit.Bukkit;
import com.badbones69.crazycrates.CrazyCrates;
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

        sendFoundMessage("CrazyCrates", (crazyCratesAPI = (CrazyCrates) Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates")) != null);
        sendFoundMessage("FarmLimiter", (farmLimiterFound = (Bukkit.getServer().getPluginManager().getPlugin("FarmLimiter") != null)));
        sendFoundMessage("GriefPrevention", (griefPreventionAPI = (GriefPrevention) Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention")) != null);
        sendFoundMessage("MyPet", (myPetAPI = (MyPetPlugin) Bukkit.getServer().getPluginManager().getPlugin("MyPet")) != null);

        foundAPIsForLogs.put("CrazyCratesCratePrizesLog", crazyCratesAPI != null);
        foundAPIsForLogs.put("FarmLimiterLog", farmLimiterFound);
        foundAPIsForLogs.put("GriefPreventionClaimsCreatedLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsDeletedLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsExpiredLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("GriefPreventionClaimsResizedLog", griefPreventionAPI != null);
        foundAPIsForLogs.put("MyPetItemPickupLog", myPetAPI != null);

    }

    private static void sendFoundMessage(String pluginName, boolean found) {

        if (found) {
            LottaLogs.getInstance().getLogger().info(ConsoleColors.BLUE_BRIGHT + pluginName + " found!");
        } else {
            LottaLogs.getInstance().getLogger().info(ConsoleColors.RED + pluginName + " not found!");
        }

    }

}
