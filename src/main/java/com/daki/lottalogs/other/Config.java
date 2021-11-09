package com.daki.lottalogs.other;

import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logging.Logging;
import com.daki.lottalogs.logging.logs.Log;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private enum Messages {

        NoPermission("Messages.NoPermission", "&cYou do not have permission to do this."),
        IncorrectUsageSearchLogsCommand("Messages.IncorrectUsageSearchLogsCommand", "&cUsage of this command is /searchlogs <normal/special/additional>."),
        IncorrectUsageSearchNormalLogsCommand("Messages.IncorrectUsageSearchNormalLogsCommand", "&cUsage of this command is /searchlogs normal <numberOfDays> <searchString>."),
        IncorrectUsageSearchSpecialLogsCommand("Messages.IncorrectUsageSearchSpecialLogsCommand", "&cWrong usage of this command, honestly just check the guide on how to use it, I can't explain it in one message..."),
        IncorrectUsageSearchAdditionalLogsCommand("Messages.IncorrectUsageSearchAdditionalLogsCommand", "&cUsage of this command is /searchlogs additional <logName> <numberOfDays> <searchString>.");

        private final String path;
        private final String message;

        Messages(String path, String message) {
            this.path = path;
            this.message = message;
        }

    }

    public static void configSetup() {

        FileConfiguration config = LottaLogs.getInstance().getConfig();

        {   // Messages

            for (Messages message : Messages.values()) {
                if (!config.contains(message.path)) {
                    config.set(message.path, message.message);
                    notFoundInConfigMessage(message.path);
                }
            }

        }   // ----------------------------------------------------------------------------------------------------

        {   // Logging

            String checkedPath;

            for (Log cachedLog : Logging.getCachedLogs()) {
                checkedPath = "Logging." + cachedLog.getName() + ".Enabled";
                if (!config.contains(checkedPath)) {
                    config.set(checkedPath, cachedLog.isEnabled());
                    notFoundInConfigMessage(checkedPath);
                }
                checkedPath = "Logging." + cachedLog.getName() + ".DaysOfLogsToKeep";
                if (!config.contains(checkedPath)) {
                    config.set(checkedPath, cachedLog.getDaysOfLogsToKeep());
                    notFoundInConfigMessage(checkedPath);
                }
            }

        }   // ----------------------------------------------------------------------------------------------------

        {   // SearchLogs

            if (!config.contains("SearchLogs.OutputPath")) {
                config.set("SearchLogs.OutputPath", new File(LottaLogs.getInstance().getDataFolder() + File.separator + "search-output" + File.separator).getAbsolutePath());
                notFoundInConfigMessage("SearchLogs.OutputPath");
            }
            if (!config.contains("SearchLogs.MaxFileSizeWithoutCompression")) {
                config.set("SearchLogs.MaxFileSizeWithoutCompression", 8);
                notFoundInConfigMessage("SearchLogs.MaxFileSizeWithoutCompression");
            }
            if (!config.contains("SearchLogs.NormalSearchBlacklistedStrings")) {
                List<String> blacklistedStrings = new ArrayList<>();
                blacklistedStrings.add("BlacklistedStringsGoHere");
                blacklistedStrings.add("665709478282752744");
                config.set("SearchLogs.NormalSearchBlacklistedStrings", blacklistedStrings);
                notFoundInConfigMessage("SearchLogs.NormalSearchBlacklistedStrings");
            }
            if (!config.contains("SearchLogs.AdditionalLogs")) {
                List<String> logNamesAndPaths = new ArrayList<>();
                String defaultName = "LogNameGoesHere";
                String defaultPath = UtilityMethods.getServerJarPath() + "PathGoesHere";
                logNamesAndPaths.add("Name:" + defaultName + " " + "Path:" + defaultPath);
                config.set("SearchLogs.AdditionalLogs", logNamesAndPaths);
                notFoundInConfigMessage("SearchLogs.AdditionalLogs");
            }

        }   // ----------------------------------------------------------------------------------------------------

        LottaLogs.getInstance().saveConfig();

    }

    static void notFoundInConfigMessage(String string) {

        LottaLogs.getInstance().getLogger().info(string + " not found in the config, creating it now.");

    }

}
