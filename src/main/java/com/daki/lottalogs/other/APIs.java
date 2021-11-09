package com.daki.lottalogs.other;

import com.daki.lottalogs.LottaLogs;
import de.Keyle.MyPet.MyPetPlugin;
import me.filoghost.farmlimiter.FarmLimiter;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class APIs extends JavaPlugin {

    public static boolean GriefPreventionFound = false;
    public static boolean MyPetFound = false;
    public static boolean CrazyCratesFound = false;
    public static boolean FarmLimiterFound = false;

    public static void APIConnect() {

        GriefPreventionFound = GriefPreventionApiCheck() != null; //
        MyPetFound = MyPetApiCheck() != null; //
        CrazyCratesFound = CrazyCratesApiCheck() != null; //
        FarmLimiterFound = FarmLimiterApiCheck() != null; //

        if (GriefPreventionFound)
            LottaLogs.getInstance().getLogger().info(ConsoleColors.BLUE_BRIGHT + "GriefPrevention found!... " + ConsoleColors.RESET);
        else
            LottaLogs.getInstance().getLogger().info(ConsoleColors.RED + "GriefPrevention not found!... " + ConsoleColors.RESET);

        if (MyPetFound)
            LottaLogs.getInstance().getLogger().info(ConsoleColors.BLUE_BRIGHT + "MyPet found!... " + ConsoleColors.RESET);
        else
            LottaLogs.getInstance().getLogger().info(ConsoleColors.RED + "MyPet not found!... " + ConsoleColors.RESET);

        if (CrazyCratesFound)
            LottaLogs.getInstance().getLogger().info(ConsoleColors.BLUE_BRIGHT + "CrazyCrates found!... " + ConsoleColors.RESET);
        else
            LottaLogs.getInstance().getLogger().info(ConsoleColors.RED + "CrazyCrates not found!... " + ConsoleColors.RESET);

        if (FarmLimiterFound)
            LottaLogs.getInstance().getLogger().info(ConsoleColors.BLUE_BRIGHT + "FarmLimiter found!... " + ConsoleColors.RESET);
        else
            LottaLogs.getInstance().getLogger().info(ConsoleColors.RED + "FarmLimiter not found!... " + ConsoleColors.RESET);

    }

    public static GriefPrevention GriefPreventionApiCheck() {
        try {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("GriefPrevention");
            if (plugin instanceof GriefPrevention) {
                return (GriefPrevention) plugin;
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static MyPetPlugin MyPetApiCheck() {
        try {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("MyPet");
            if (plugin instanceof MyPetPlugin) {
                return (MyPetPlugin) plugin;
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static me.badbones69.crazycrates.Main CrazyCratesApiCheck() {
        try {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
            if (plugin instanceof me.badbones69.crazycrates.Main) {
                return (me.badbones69.crazycrates.Main) plugin;
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public static FarmLimiter FarmLimiterApiCheck() {
        try {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("FarmLimiter");
            if (plugin instanceof FarmLimiter) {
                return (FarmLimiter) plugin;
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

}