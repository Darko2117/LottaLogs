package com.daki.lottalogs;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.yaml.snakeyaml.Yaml;
import com.daki.lottalogs.logs.Log;

public class Config {

    public static void configSetup() {

        FileConfiguration config = LottaLogs.getInstance().getConfig();

        // Logging

        Map<String, Log> sortedLogs = new TreeMap<>(Logging.getAllDefaultLogs());

        for (Map.Entry<String, Log> entry : sortedLogs.entrySet()) {

            Log log = entry.getValue();

            String checkedPath = "Logging." + log.getName() + ".Enabled";

            if (!config.contains(checkedPath)) {
                config.set(checkedPath, log.isEnabled());
                notFoundInConfigMessage(checkedPath);
            }

            checkedPath = "Logging." + log.getName() + ".DaysOfLogsToKeep";

            if (!config.contains(checkedPath)) {
                config.set(checkedPath, log.getDaysOfLogsToKeep());
                notFoundInConfigMessage(checkedPath);
            }

            checkedPath = "Logging." + log.getName() + ".BlacklistedStrings";

            log.getBlacklistedStrings().add("PutStringsInAListLikeThis");

            if (!config.contains(checkedPath)) {
                config.set(checkedPath, log.getBlacklistedStrings());
                notFoundInConfigMessage(checkedPath);
            }

        }

        if (!config.contains("Logging.CreatingBlankFiles")) {
            config.set("Logging.CreatingBlankFiles", "all");
            notFoundInConfigMessage("Logging.CreatingBlankFiles");
        }

        if (!config.contains("Logging.PlayerLocationLog.WriteFrequencySeconds")) {
            config.set("Logging.PlayerLocationLog.WriteFrequencySeconds", 10);
            notFoundInConfigMessage("Logging.PlayerLocationLog.WriteFrequencySeconds");
        }

        // ----------------------------------------------------------------------------------------------------

        // SearchLogs

        if (!config.contains("SearchLogs.OutputPath")) {
            config.set("SearchLogs.OutputPath", new File(LottaLogs.getInstance().getDataFolder() + File.separator + "search-output" + File.separator).getAbsolutePath());
            notFoundInConfigMessage("SearchLogs.OutputPath");
        }

        if (!config.contains("SearchLogs.FileSizeUploadLimitMB")) {
            config.set("SearchLogs.FileSizeUploadLimitMB", 25);
            notFoundInConfigMessage("SearchLogs.FileSizeUploadLimitMB");
        }

        if (!config.contains("SearchLogs.DiscordChannelWebhook")) {
            config.set("SearchLogs.DiscordChannelWebhook", "https://discord.com/api/webhooks/0123456789123456789/So2Me5Ra0Nd4Om2To4Ke5N");
            notFoundInConfigMessage("SearchLogs.DiscordChannelWebhook");
        }

        if (!config.contains("SearchLogs.NormalSearchBlacklistedStrings")) {
            List<String> blacklistedStrings = new ArrayList<>();
            blacklistedStrings.add("PutStringsInAListLikeThis");
            config.set("SearchLogs.NormalSearchBlacklistedStrings", blacklistedStrings);
            notFoundInConfigMessage("SearchLogs.NormalSearchBlacklistedStrings");
        }

        // ----------------------------------------------------------------------------------------------------

        // AdditionalLogs

        if (!config.contains("AdditionalLogs")) {

            List<String> logNamesAndPaths = new ArrayList<>();

            String exampleLogName = "ExampleName";
            String exampleLogPath = new File("").getAbsolutePath() + File.separator + "plugins" + File.separator + "SomePluginFolder" + File.separator + "somePluginLog";
            logNamesAndPaths.add("Name:" + exampleLogName + " " + "Path:" + exampleLogPath);

            config.set("AdditionalLogs", logNamesAndPaths);
            notFoundInConfigMessage("AdditionalLogs");

        }

        // ----------------------------------------------------------------------------------------------------

        LottaLogs.getInstance().saveConfig();

    }

    private static void notFoundInConfigMessage(String string) {

        LottaLogs.getInstance().getLogger().info(string + " not found in the config, creating it now.");

    }

    /*
     * I don't remember exactly why I made this, but I'm pretty sure that in some cases where a value in
     * the config is not set properly it can cause the whole config to reset to default. This tries
     * loading it before the plugin itself does, if it fails it disables the plugin and stops it from
     * resetting it.
     */
    public static void checkAndReloadConfig() {

        try {

            LottaLogs.getInstance().saveDefaultConfig();

            new Yaml().load(new FileInputStream(LottaLogs.getInstance().getDataFolder() + File.separator + "config.yml"));

            LottaLogs.getInstance().reloadConfig();

        } catch (Throwable throwable) {

            throwable.printStackTrace();

            LottaLogs.getInstance().getLogger().warning("Plugin encountered an error while trying to load the config, disabling...");

            Bukkit.getPluginManager().disablePlugin(LottaLogs.getInstance());

        }

    }

}
