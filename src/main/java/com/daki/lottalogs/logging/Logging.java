package com.daki.lottalogs.logging;

import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logging.listeners.LoggingNoAPI;
import com.daki.lottalogs.logging.logs.ClaimsCreatedLog;
import com.daki.lottalogs.logging.logs.ClaimsDeletedLog;
import com.daki.lottalogs.logging.logs.ClaimsExpiredLog;
import com.daki.lottalogs.logging.logs.ClaimsModifiedLog;
import com.daki.lottalogs.logging.logs.CommandsWithLocationLog;
import com.daki.lottalogs.logging.logs.CratePrizesLog;
import com.daki.lottalogs.logging.logs.DroppedItemsLog;
import com.daki.lottalogs.logging.logs.DroppedItemsOnDeathLog;
import com.daki.lottalogs.logging.logs.EggsThrownLog;
import com.daki.lottalogs.logging.logs.FarmLimiterLog;
import com.daki.lottalogs.logging.logs.ItemsBrokenLog;
import com.daki.lottalogs.logging.logs.ItemsDespawnedLog;
import com.daki.lottalogs.logging.logs.ItemsDestroyedLog;
import com.daki.lottalogs.logging.logs.ItemsPlacedInItemFramesLog;
import com.daki.lottalogs.logging.logs.ItemsTakenOutOfItemFramesLog;
import com.daki.lottalogs.logging.logs.LightningStrikesLog;
import com.daki.lottalogs.logging.logs.Log;
import com.daki.lottalogs.logging.logs.MCMMORepairUseLog;
import com.daki.lottalogs.logging.logs.MinecartsDestroyedLog;
import com.daki.lottalogs.logging.logs.MyPetItemPickupLog;
import com.daki.lottalogs.logging.logs.PickedUpItemsLog;
import com.daki.lottalogs.logging.logs.PlayerLocationLog;
import com.daki.lottalogs.logging.logs.TridentsLog;
import com.daki.lottalogs.logging.logs.UIClicksLog;
import com.daki.lottalogs.other.BukkitTaskCache;
import com.daki.lottalogs.other.UtilityMethods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logging {

    public static final int defaultDaysOfLogsToKeep = 60;

    static int cachedDayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    static ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<>();
    static boolean isWritingLogs = false;

    private static List<Log> cachedLogs;

    public static List<Log> getCachedLogs() {
        return cachedLogs;
    }

    public static Log getCachedLogFromName(String logName) {
        for (Log log : getCachedLogs()) {
            if (log.getName().equals(logName)) {
                return log;
            }
        }
        return null;
    }

    public static void cacheLogs() {

        cachedLogs = new ArrayList<>();
        cachedLogs.add(new ClaimsCreatedLog());
        cachedLogs.add(new ClaimsDeletedLog());
        cachedLogs.add(new ClaimsExpiredLog());
        cachedLogs.add(new ClaimsModifiedLog());
        cachedLogs.add(new CommandsWithLocationLog());
        cachedLogs.add(new CratePrizesLog());
        cachedLogs.add(new DroppedItemsLog());
        cachedLogs.add(new DroppedItemsOnDeathLog());
        cachedLogs.add(new EggsThrownLog());
        cachedLogs.add(new FarmLimiterLog());
        cachedLogs.add(new ItemsBrokenLog());
        cachedLogs.add(new ItemsDespawnedLog());
        cachedLogs.add(new ItemsDestroyedLog());
        cachedLogs.add(new ItemsPlacedInItemFramesLog());
        cachedLogs.add(new ItemsTakenOutOfItemFramesLog());
        cachedLogs.add(new LightningStrikesLog());
        cachedLogs.add(new MCMMORepairUseLog());
        cachedLogs.add(new MinecartsDestroyedLog());
        cachedLogs.add(new MyPetItemPickupLog());
        cachedLogs.add(new PickedUpItemsLog());
        cachedLogs.add(new PlayerLocationLog());
        cachedLogs.add(new TridentsLog());
        cachedLogs.add(new UIClicksLog());

    }

    public static void updateCachedLogsFromConfig() {

        for (Log cachedLog : getCachedLogs()) {
            cachedLog.setEnabled(LottaLogs.getInstance().getConfig().getBoolean("Logging." + cachedLog.getName() + ".Enabled"));
            cachedLog.setDaysOfLogsToKeep(LottaLogs.getInstance().getConfig().getInt("Logging." + cachedLog.getName() + ".DaysOfLogsToKeep"));
        }

    }

    public static void initiate() {

        List<String> directories = new ArrayList<>();
        directories.add("logs");
        directories.add("compressed-logs");
        directories.add("search-output");
        directories.add("temporary-files");

        for (String directory : directories)
            new File(LottaLogs.getInstance().getDataFolder() + File.separator + directory).mkdir();

        CreateAllBlankLogFiles();

        new BukkitRunnable() {
            @Override
            public void run() {
                CheckAndCompress();
                CheckAndDeleteOld();
            }
        }.runTaskAsynchronously(LottaLogs.getInstance());

        BukkitTaskCache.addTask(new BukkitRunnable() {
            @Override
            public void run() {
                CompressIfDateChanged();
            }
        }.runTaskTimerAsynchronously(LottaLogs.getInstance(), 20, 20));

        initializeLogWriting();

        LoggingNoAPI.startPlayerLocationLog();

    }

    static void CreateAllBlankLogFiles() {

        for (Log cachedLog : getCachedLogs()) {
            Logging.addToLogWriteQueue(cachedLog);
        }

    }

    static void CheckAndCompress() {

        String[] logsNames = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "logs").list();
        if (logsNames == null || logsNames.length == 0) {
            return;
        }

        for (String logName : logsNames) {

            File log = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "logs" + File.separator + logName);

            if (log.getName().startsWith(UtilityMethods.getDateStringYYYYMMDD())) continue;

            try {
                if (UtilityMethods.compressFile(log.getAbsolutePath(), log.getAbsolutePath().replace(File.separator + "logs" + File.separator, File.separator + "compressed-logs" + File.separator).concat(".gz"))) {
                    if (!log.delete())
                        LottaLogs.getInstance().getLogger().warning("Something failed during deletion of the file " + log.getAbsolutePath());
                } else {
                    LottaLogs.getInstance().getLogger().warning("Something failed during file compression of the file " + log.getAbsolutePath());
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }

    }

    static void CheckAndDeleteOld() {

        String[] logsNames = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "compressed-logs").list();
        if (logsNames == null || logsNames.length == 0) {
            return;
        }

        for (String logName : logsNames) {

            try {

                File file = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "compressed-logs" + File.separator + logName);

                String fileNameWithoutDate = file.getName().substring(11, file.getName().indexOf(".txt.gz"));

                int numberOfLogsToKeepFromConfig = Logging.getCachedLogFromName(fileNameWithoutDate).getDaysOfLogsToKeep();

                if (numberOfLogsToKeepFromConfig == -1) numberOfLogsToKeepFromConfig = 999999999;

                if (numberOfLogsToKeepFromConfig == 0) throw new Throwable();

                int day = UtilityMethods.getDateValuesFromStringYYYYMMDD(logName.substring(0, 10))[0];
                int month = UtilityMethods.getDateValuesFromStringYYYYMMDD(logName.substring(0, 10))[1];
                int year = UtilityMethods.getDateValuesFromStringYYYYMMDD(logName.substring(0, 10))[2];
                LocalDate fileDateLD = LocalDate.of(year, month, day);

                int epochDayOfFileCreation = Math.toIntExact(fileDateLD.toEpochDay());
                int epochDayRightNow = Math.toIntExact(LocalDate.now().toEpochDay());

                if (epochDayOfFileCreation + numberOfLogsToKeepFromConfig < epochDayRightNow) {

                    if (file.delete())
                        LottaLogs.getInstance().getLogger().info(file.getName() + " deleted.");
                    else
                        LottaLogs.getInstance().getLogger().warning("Something failed during deletion of file " + file.getAbsolutePath());
                }

            } catch (Throwable throwable) {
                LottaLogs.getInstance().getLogger().warning(logName + " has an invalid name. Please set it to yyyy-mm-dd format if you want the plugin to keep track of it and delete it after the specified time.");
            }

        }

    }

    static void CompressIfDateChanged() {

        int dayNow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        if (!(cachedDayOfMonth == dayNow)) {

            CheckAndCompress();
            CheckAndDeleteOld();
            CreateAllBlankLogFiles();

            cachedDayOfMonth = dayNow;

        }

    }

    public static String getBetterLocationString(Location location) {

        String worldName = location.getWorld().getName();

        String dimension = location.getWorld().getEnvironment().toString();

        String X = String.valueOf(location.getBlockX());
        String Y = String.valueOf(location.getBlockY());
        String Z = String.valueOf(location.getBlockZ());

        String message = "";
        message = message.concat("World: ");
        message = message.concat(worldName);
        message = message.concat(" Dimension: ");
        message = message.concat(dimension);
        message = message.concat(" X:");
        message = message.concat(X);
        message = message.concat(" Y:");
        message = message.concat(Y);
        message = message.concat(" Z:");
        message = message.concat(Z);

        return message;

    }

    public static Location getLocationFromBetterLocationString(String string) {

        string = string.substring(7);

        String worldName = string.substring(0, string.indexOf(" "));

        string = string.substring(string.indexOf(" ") + 1);
        string = string.substring(12);

        String dimension = string.substring(0, string.indexOf(" "));

        string = string.substring(string.indexOf(" ") + 1);
        string = string.substring(2);

        String X = string.substring(0, string.indexOf(" "));

        string = string.substring(string.indexOf(" ") + 1);
        string = string.substring(2);

        String Y = string.substring(0, string.indexOf(" "));

        string = string.substring(string.indexOf(" ") + 1);
        string = string.substring(2);

        String Z = string;

        return new Location(Bukkit.getWorld(worldName), Double.parseDouble(X), Double.parseDouble(Y), Double.parseDouble(Z));

    }

    static void initializeLogWriting() {

        BukkitTaskCache.addTask(new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    if (isWritingLogs) return;

                    isWritingLogs = true;

                    while (logQueue.peek() != null) {

                        writeToFile(logQueue.poll());

                    }

                    isWritingLogs = false;

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    isWritingLogs = false;
                }
            }
        }.runTaskTimerAsynchronously(LottaLogs.getInstance(), 1, 1));

    }

    public static void addToLogWriteQueue(Log log) {

        logQueue.add(log);

    }

    public static void writeToFile(Log log) {
        try {

            boolean logArgumentsAreEmpty = log.getArguments().entrySet().iterator().next().getValue().isEmpty();
            String logMessage = log.getLogArgumentsString();

            logMessage = logMessage.replace("\n", "\\n");
            if (!logArgumentsAreEmpty) {
                logMessage = logMessage.concat("\n");
            } else {
                logMessage = "";
            }

            FileWriter writer = new FileWriter(LottaLogs.getInstance().getDataFolder() + log.getPath(), true);
            writer.write(logMessage);
            writer.close();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    static List<String> getArgumentListFromLogName(String logName) {

        LinkedList<String> arguments = new LinkedList<>();
        for (String argument : Logging.getCachedLogFromName(logName).getArguments().keySet().stream().toList()) {
            argument = argument.concat(":");
            arguments.add(argument);
        }

        if (arguments.contains("Location:") || arguments.contains("Area:"))
            arguments.add("-radius:");

        return arguments;

    }

    static List<String> getAdditionalLogNames() {

        List<String> logNames = new ArrayList<>();

        for (String s : LottaLogs.getInstance().getConfig().getStringList("AdditionalLogs")) {

            s = s.substring(s.indexOf("Name:") + 5);
            s = s.substring(0, s.indexOf(" "));

            logNames.add(s);

        }

        return logNames;

    }

}