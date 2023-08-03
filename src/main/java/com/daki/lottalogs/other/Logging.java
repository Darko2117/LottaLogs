package com.daki.lottalogs.other;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.scheduler.BukkitRunnable;
import org.reflections.Reflections;
import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logs.Log;
import com.daki.lottalogs.logs.PlayerLocationLog;
import lombok.Getter;

public class Logging {

    private static int cachedDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    private static final ConcurrentLinkedQueue<Log> logQueue = new ConcurrentLinkedQueue<>();

    private static volatile boolean isWritingLogs = false;

    private static boolean isCompressing = false;

    @Getter
    private static final HashMap<String, Log> cachedLogs = new HashMap<>();

    public static void initiate() {

        for (String directory : new String[] {"logs", "compressed-logs", "search-output", "temporary-files"})
            new File(LottaLogs.getInstance().getDataFolder() + File.separator + directory).mkdir();

        cacheLogs();

        checkAndCompress();
        checkAndDeleteOld();
        createBlankLogFiles(LottaLogs.getInstance().getConfig().getString("Logging.CreatingBlankFiles"));

        startDateCheck();

        initializeLogWriting();

        PlayerLocationLog.startPlayerLocationLog(LottaLogs.getInstance().getConfig().getInt("Logging.PlayerLocationLog.WriteFrequencySeconds"));

    }

    /**
     * @return HashMap with instances of all logs with all argument values empty
     */
    public static HashMap<String, Log> getAllDefaultLogs() {

        HashMap<String, Log> logs = new HashMap<>();

        for (Class<? extends Log> log : new Reflections("com.daki.lottalogs.logs").getSubTypesOf(Log.class)) {
            try {

                Log logTemp = log.getDeclaredConstructor(String[].class).newInstance((Object) new String[] {});
                logs.put(logTemp.getName(), logTemp);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                continue;
            }

        }

        return logs;

    }

    /**
     * Caches all logs with values from the config
     */
    public static void cacheLogs() {

        cachedLogs.clear();

        for (Map.Entry<String, Log> entry : getAllDefaultLogs().entrySet()) {

            entry.getValue().setEnabled(LottaLogs.getInstance().getConfig().getBoolean("Logging." + entry.getValue().getName() + ".Enabled"));
            entry.getValue().setDaysOfLogsToKeep(LottaLogs.getInstance().getConfig().getInt("Logging." + entry.getValue().getName() + ".DaysOfLogsToKeep"));

            for (String blacklistedString : LottaLogs.getInstance().getConfig().getStringList("Logging." + entry.getValue().getName() + ".BlacklistedStrings")) {
                entry.getValue().getBlacklistedStrings().add(blacklistedString.toLowerCase());
            }

            cachedLogs.put(entry.getKey(), entry.getValue());

        }

        return;

    }

    /**
     * 
     * @param mode "all" to create blank files for all logs, "enabled" to create blank files only for
     *        the enabled logs, "none" don't create blank files and let the plugin create them as they
     *        are written to
     */
    public static void createBlankLogFiles(String mode) {

        for (Map.Entry<String, Log> entry : getCachedLogs().entrySet()) {

            if (mode.equals("all") || (mode.equals("enabled")) && entry.getValue().isEnabled()) {

                Logging.addToLogWriteQueue(entry.getValue());

            }

        }

    }

    private static void startDateCheck() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    if (isCompressing)
                        return;

                    int dayNow = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

                    if (cachedDayOfWeek == dayNow)
                        return;

                    isCompressing = true;

                    checkAndCompress();
                    checkAndDeleteOld();
                    createBlankLogFiles(LottaLogs.getInstance().getConfig().getString("Logging.CreatingBlankFiles"));

                    cachedDayOfWeek = dayNow;

                    isCompressing = false;

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    isCompressing = false;
                }
            }
        }.runTaskTimerAsynchronously(LottaLogs.getInstance(), 0, 20);
    }

    /**
     * Compresses the logs and moves them to the compressed-logs directory. Happens when the date
     * changes.
     */
    private static void checkAndCompress() {

        String[] logsNames = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "logs").list();

        if (logsNames == null || logsNames.length == 0) {
            return;
        }

        for (String logName : logsNames) {

            File log = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "logs" + File.separator + logName);

            if (log.getName().startsWith(Methods.getDateStringYYYYMMDD()))
                continue;

            try {

                if (Methods.compressFile(log.getAbsolutePath(), log.getAbsolutePath().replace(File.separator + "logs" + File.separator, File.separator + "compressed-logs" + File.separator).concat(".gz"))) {
                    if (!log.delete()) {
                        LottaLogs.getInstance().getLogger().warning("Something failed during deletion of the file " + log.getAbsolutePath());
                    }

                } else {
                    LottaLogs.getInstance().getLogger().warning("Something failed during file compression of the file " + log.getAbsolutePath());
                }

            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

        }

    }

    /**
     * Checks all of the compressed logs and deletes the ones older than the value set in the config.
     * happens when the date changes.
     */
    private static void checkAndDeleteOld() {

        String[] logsNames = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "compressed-logs").list();

        if (logsNames == null || logsNames.length == 0) {
            return;
        }

        for (String logName : logsNames) {

            try {

                File file = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "compressed-logs" + File.separator + logName);

                String fileNameWithoutDate = file.getName().substring(11, file.getName().indexOf(".txt.gz"));

                int numberOfLogsToKeepFromConfig = Logging.getCachedLogs().get(fileNameWithoutDate).getDaysOfLogsToKeep();

                if (numberOfLogsToKeepFromConfig == -1)
                    continue;

                if (numberOfLogsToKeepFromConfig == 0)
                    throw new Throwable();

                int day = Methods.getDateValuesFromStringYYYYMMDD(logName.substring(0, 10))[0];
                int month = Methods.getDateValuesFromStringYYYYMMDD(logName.substring(0, 10))[1];
                int year = Methods.getDateValuesFromStringYYYYMMDD(logName.substring(0, 10))[2];
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

    private static void initializeLogWriting() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    if (isWritingLogs)
                        return;

                    isWritingLogs = true;

                    while (logQueue.peek() != null)
                        writeToFile(logQueue.poll());

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                } finally {
                    isWritingLogs = false;
                }
            }
        }.runTaskTimerAsynchronously(LottaLogs.getInstance(), 0, 1);
    }

    public static void addToLogWriteQueue(Log log) {

        logQueue.add(log);

    }

    public static void writeToFile(Log log) {
        try {

            FileWriter writer = new FileWriter(LottaLogs.getInstance().getDataFolder() + log.getPath(), true);
            writer.write(log.getStringToWrite());
            writer.close();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static List<String> getArgumentListFromLogName(String logName) {

        LinkedList<String> arguments = new LinkedList<>();
        for (String argument : Logging.getCachedLogs().get(logName).getArguments().keySet().stream().toList()) {
            argument = argument.concat(":");
            arguments.add(argument);
        }

        if (arguments.contains("Location:") || arguments.contains("Area:"))
            arguments.add("-radius:");

        return arguments;

    }

    public static List<String> getAdditionalLogNames() {

        List<String> logNames = new ArrayList<>();

        for (String s : LottaLogs.getInstance().getConfig().getStringList("AdditionalLogs")) {

            s = s.substring(s.indexOf("Name:") + 5);
            s = s.substring(0, s.indexOf(" "));

            logNames.add(s);

        }

        return logNames;

    }

}
