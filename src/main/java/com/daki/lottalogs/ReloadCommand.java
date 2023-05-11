package com.daki.lottalogs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Config.checkAndReloadConfig();

        Logging.cacheLogs();
        Logging.createBlankLogFiles(LottaLogs.getInstance().getConfig().getString("Logging.CreatingBlankFiles"));

        Register.registerEvents();

        Methods.sendMessageAndLog(sender, "&aPlugin reloaded!");

        return true;

    }

}
