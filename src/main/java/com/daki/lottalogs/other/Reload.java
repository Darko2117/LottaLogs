package com.daki.lottalogs.other;

import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logging.Logging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        reload();
        sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
        return true;

    }

    public static void reload() {

        LottaLogs.getInstance().saveDefaultConfig();
        if (UtilityMethods.checkConfig()) {
            LottaLogs.getInstance().reloadConfig();
        } else {
            Bukkit.getPluginManager().disablePlugin(LottaLogs.getInstance());
            return;
        }

        Logging.cacheLogs();

        BukkitTaskCache.cancelRunningTasks();

        Config.configSetup();

        Logging.updateCachedLogsFromConfig();

        Logging.initiate();

        APIs.APIConnect();

        Register.registerEvents();

    }

}
