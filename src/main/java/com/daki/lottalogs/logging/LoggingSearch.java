package com.daki.lottalogs.logging;

import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logging.logs.Log;
import com.daki.lottalogs.other.UtilityMethods;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LoggingSearch implements CommandExecutor, TabCompleter {

    public static boolean inUse = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || !(args[0].equals("normal") || args[0].equals("special") || args[0].equals("additional"))) {
            UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchLogsCommand");
            return true;
        }

        if (!sender.hasPermission("lottalogs.searchlogs." + args[0])) {
            UtilityMethods.sendConfigMessage(sender, "Messages.NoPermission");
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    if (inUse) {
                        sender.sendMessage(ChatColor.RED + "A search is in progress, this one will start when that one finishes.");
                    }

                    //Waiting until other searches finish. Only one can be active at the time.
                    while (inUse) {
                        LottaLogs.getInstance().getLogger().info("Waiting until the last search finishes to start this one.");
                        Thread.sleep(1000);
                    }

                    if (args[0].equals("normal")) {
                        normalSearch(sender, command, label, args);
                        return;
                    }
                    if (args[0].equals("special")) {
                        specialSearch(sender, command, label, args);
                        return;
                    }
                    if (args[0].equals("additional")) {
                        additionalSearch(sender, command, label, args);
                        return;
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }.runTaskAsynchronously(LottaLogs.getInstance());

        return true;

    }

    static void normalSearch(CommandSender sender, Command command, String label, String[] args) {

        try {

            inUse = true;
            sender.sendMessage(ChatColor.YELLOW + "Search started.");
            LottaLogs.getInstance().getLogger().info("Search started.");
            long startingTime = System.currentTimeMillis();

            //Deleting all the temporary files in case some are left.
            clearTemporaryFiles();

            if (args.length < 3) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchNormalLogsCommand");
                inUse = false;
                return;
            }

            int days;
            try {
                days = Integer.parseInt(args[1]);
            } catch (Throwable throwable) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchNormalLogsCommand");
                inUse = false;
                return;
            }

            String silent = "";
            for (String arg : args) {
                if (arg.equals("-silent")) {
                    silent = "-silent";
                    break;
                }
            }

            String searchString = "";
            for (int i = 2; i < args.length; i++) {
                if (args[i].equals("-silent")) continue;
                if (!searchString.isEmpty())
                    searchString = searchString.concat(" ");
                searchString = searchString.concat(args[i]);
            }
            searchString = searchString.toLowerCase();

            List<String> blacklistedStrings = LottaLogs.getInstance().getConfig().getStringList("SearchLogs.NormalSearchBlacklistedStrings");

            //Getting a list of all the log files.
            String logsDirectoryPath = new File(".").getAbsolutePath() + File.separator + "logs" + File.separator;
            List<File> filesToRead = new ArrayList<>(Arrays.asList(new File(logsDirectoryPath).listFiles()));

            //Removing from that list all the ones that aren't in the defined time limit.
            List<File> filesToRemove = new ArrayList<>();
            for (File file : filesToRead) {
                try {

                    int day = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[0];
                    int month = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[1];
                    int year = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[2];
                    LocalDate fileDateLD = LocalDate.of(year, month, day);

                    int epochDayOfFileCreation = Math.toIntExact(fileDateLD.toEpochDay());
                    int epochDayRightNow = Math.toIntExact(LocalDate.now().toEpochDay());

                    if (epochDayRightNow - days > epochDayOfFileCreation) {
                        filesToRemove.add(file);
                    }

                } catch (Throwable ignored) {
                    if (!file.getName().equals("latest.log"))
                        filesToRemove.add(file);
                }
            }
            filesToRead.removeAll(filesToRemove);

            //Copying all the files that need to be read to /temporary-files/. Uncompressing the compressed ones.
            for (File f : filesToRead) {
                try {

                    if (f.getName().contains(".gz")) {
                        String outputPath = LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + f.getName().replace(".gz", "");
                        if (!UtilityMethods.uncompressFileGZIP(f.getAbsolutePath(), outputPath))
                            LottaLogs.getInstance().getLogger().warning("Something failed during extraction of the file " + f.getAbsolutePath());
                    } else {
                        UtilityMethods.copyPasteFile(new File(f.getAbsolutePath()), new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + f.getName()));
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            //Reloading the list of files that need to be read with the files from /temporary-files/.
            filesToRead.clear();
            filesToRead.addAll(Arrays.asList(new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files").listFiles()));
            filesToRead.sort(Comparator.naturalOrder());

            String serverName = LottaLogs.getInstance().getDataFolder().getAbsolutePath();
            try {
                serverName = serverName.substring(0, serverName.indexOf(File.separator + "plugins"));
                serverName = serverName.substring(serverName.lastIndexOf(File.separator) + 1);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                serverName = "unknownServerName";
            }

            //The file is first written to /temporary-files/. It'll get moved after the whole search is completed.
            String outputFilePath = "";
            outputFilePath = outputFilePath.concat(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator);
            outputFilePath = outputFilePath.concat(File.separator);
            outputFilePath = outputFilePath.concat(serverName);
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat("normalLogs");
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(sender.getName());
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(String.valueOf(System.currentTimeMillis()));
            outputFilePath = outputFilePath.concat(silent);
            outputFilePath = outputFilePath.concat(".txt");

            //Searching through the files for the arguments.
            File outputFile = new File(outputFilePath);
            FileWriter writer = new FileWriter(outputFile, true);
            writer.write("");

            for (File f : filesToRead) {
                try {

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                    String line;

                    lineReader:
                    while ((line = bufferedReader.readLine()) != null) {

                        if (!line.toLowerCase().matches("(.*)" + searchString + "(.*)")) continue lineReader;

                        for (String s : blacklistedStrings) {
                            if (line.toLowerCase().contains(s)) {
                                writer.write(f.getName() + ":" + "This line contained a blacklisted string. Skipping it." + "\n");
                                continue lineReader;
                            }
                        }

                        //Writes only those lines which contain the provided search string, it skips over the rest.
                        writer.write(f.getName() + ":" + line + "\n");

                    }

                    bufferedReader.close();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            writer.close();

            //The maxFileSizeWithoutCompression is in MB so it's multiplied by 1mil to get the size in bytes.
            int maxFileSizeWithoutCompression = LottaLogs.getInstance().getConfig().getInt("SearchLogs.MaxFileSizeWithoutCompression");
            maxFileSizeWithoutCompression *= 1000000;

            //If the file is bigger than the defined limit, it gets compressed.
            if (outputFile.length() > maxFileSizeWithoutCompression) {

                UtilityMethods.compressFile(outputFile.getAbsolutePath(), outputFile.getAbsolutePath().concat(".gz"));

                outputFile = new File(outputFile.getAbsolutePath().concat(".gz"));

            }

            //The file with all the results is copied to the defined output path and all the other files are removed.
            String tempOutputFilePathString = outputFile.getAbsolutePath();
            tempOutputFilePathString = tempOutputFilePathString.substring(tempOutputFilePathString.lastIndexOf(File.separator) + 1);
            tempOutputFilePathString = LottaLogs.getInstance().getConfig().getString("SearchLogs.OutputPath") + File.separator + tempOutputFilePathString;

            if (outputFile.length() != 0)
                UtilityMethods.copyPasteFile(outputFile, (new File(tempOutputFilePathString)));
            else {
                sender.sendMessage(ChatColor.RED + "The results file is empty, not sending it.");
            }

            long endingTime = System.currentTimeMillis();
            long totalTime = endingTime - startingTime;
            int seconds = 0;
            while (totalTime >= 1000) {
                seconds += 1;
                totalTime -= 1000;
            }

            double totalFileSize = 0d;
            for (File file : filesToRead)
                totalFileSize += file.length();
            totalFileSize /= 1000000;

            clearTemporaryFiles();

            sender.sendMessage(ChatColor.GREEN + "Search completed, took " + seconds + "." + totalTime + "s. Scanned " + filesToRead.size() + " files with a total size of " + Math.round(totalFileSize * 100.0) / 100.0 + "MB.");
            LottaLogs.getInstance().getLogger().info("Search completed, took " + seconds + "." + totalTime + "s. Scanned " + filesToRead.size() + " files with a total size of " + Math.round(totalFileSize * 100.0) / 100.0 + "MB.");

            inUse = false;

        } catch (Throwable throwable) {
            UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchNormalLogsCommand");
            throwable.printStackTrace();
            inUse = false;
        }

    }

    static void specialSearch(CommandSender sender, Command command, String label, String[] args) {

        try {

            inUse = true;
            sender.sendMessage(ChatColor.YELLOW + "Search started.");
            LottaLogs.getInstance().getLogger().info("Search started.");
            long startingTime = System.currentTimeMillis();

            //Deleting all the temporary files in case some are left.
            clearTemporaryFiles();

            if (args.length < 5) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchSpecialLogsCommand");
                inUse = false;
                return;
            }

            String[] logNames = args[1].split(",");
            for (String logName : logNames) {
                if (Logging.getCachedLogFromName(logName) == null) {
                    UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchSpecialLogsCommand");
                    inUse = false;
                    return;
                }
            }

            int days;
            try {
                days = Integer.parseInt(args[2]);
            } catch (Throwable throwable) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchSpecialLogsCommand");
                inUse = false;
                return;
            }

            String silent = "";
            for (String arg : args) {
                if (arg.equals("-silent")) {
                    silent = "-silent";
                    break;
                }
            }

            Double playerX = null;
            Double playerZ = null;
            if (sender instanceof Player) {
                playerX = ((Player) sender).getLocation().getX();
                playerZ = ((Player) sender).getLocation().getZ();
            }

            String radiusString = "-1";

            HashMap<String, String> arguments = new HashMap<>();

            for (int i = 3; i < args.length; i++) {

                String argumentKey;
                String argumentValues = "";

                if (args[i].equalsIgnoreCase("-radius:")) {

                    radiusString = args[i + 1];
                    continue;

                }

                if (args[i].equalsIgnoreCase("-silent")) continue;

                if (!args[i].contains(":")) continue;

                argumentKey = args[i].replace(":", "");

                for (int j = i + 1; j < args.length; j++) {

                    if (args[j].equalsIgnoreCase("-silent")) break;
                    if (args[j].contains(":")) break;

                    if (!argumentValues.isEmpty())
                        argumentValues = argumentValues.concat(" ");
                    argumentValues = argumentValues.concat(args[j]);

                }

                arguments.put(argumentKey, argumentValues);

            }

            //Getting a list of all the files with the correct log name from /logs/ and /compressed-logs/.
            List<File> filesToRead = new ArrayList<>();
            for (File file : new File(LottaLogs.getInstance().getDataFolder() + File.separator + "logs" + File.separator).listFiles()) {
                for (String logName : logNames) {
                    if (file.getName().contains(logName)) {
                        filesToRead.add(file);
                        break;
                    }
                }
            }
            for (File file : new File(LottaLogs.getInstance().getDataFolder() + File.separator + "compressed-logs" + File.separator).listFiles()) {
                for (String logName : logNames) {
                    String fileName = file.getName();
                    fileName = fileName.substring(11, fileName.indexOf('.'));
                    if (logName.equals(fileName)) {
                        filesToRead.add(file);
                        break;
                    }
                }
            }

            //Removing from that list all the ones that aren't in the defined time limit.
            List<File> filesToRemove = new ArrayList<>();
            for (File file : filesToRead) {
                try {

                    int day = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[0];
                    int month = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[1];
                    int year = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[2];
                    LocalDate fileDateLD = LocalDate.of(year, month, day);

                    int epochDayOfFileCreation = Math.toIntExact(fileDateLD.toEpochDay());
                    int epochDayRightNow = Math.toIntExact(LocalDate.now().toEpochDay());

                    if (epochDayRightNow - days > epochDayOfFileCreation) {
                        filesToRemove.add(file);
                    }

                } catch (Throwable ignored) {
                    filesToRemove.add(file);
                }
            }
            filesToRead.removeAll(filesToRemove);

            //Copying all the files that need to be read to /temporary-files/. Uncompressing the compressed ones.
            for (File f : filesToRead) {
                try {

                    if (f.getName().contains(".gz")) {
                        String outputPath = LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files";
                        if (!UtilityMethods.uncompressFile(f.getAbsolutePath(), outputPath))
                            LottaLogs.getInstance().getLogger().warning("Something failed during extraction of the file " + f.getAbsolutePath());
                    } else {
                        UtilityMethods.copyPasteFile(new File(f.getAbsolutePath()), new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + f.getName()));
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            //Reloading the list of files that need to be read with the files from /temporary-files/.
            filesToRead.clear();
            filesToRead.addAll(Arrays.asList(new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files").listFiles()));
            filesToRead.sort(Comparator.naturalOrder());

            String serverName = LottaLogs.getInstance().getDataFolder().getAbsolutePath();
            try {
                serverName = serverName.substring(0, serverName.indexOf(File.separator + "plugins"));
                serverName = serverName.substring(serverName.lastIndexOf(File.separator) + 1);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                serverName = "unknownServerName";
            }

            String logNamesString = "";
            for (String logName : logNames) {
                if (!logNamesString.isEmpty())
                    logNamesString = logNamesString.concat("-");
                logNamesString = logNamesString.concat(logName);
            }

            //The file is first written to /temporary-files/. It'll get moved after the whole search is completed.
            String outputFilePath = "";
            outputFilePath = outputFilePath.concat(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator);
            outputFilePath = outputFilePath.concat(File.separator);
            outputFilePath = outputFilePath.concat(serverName);
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(logNamesString);
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat((sender.getName()));
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(String.valueOf(System.currentTimeMillis()));
            outputFilePath = outputFilePath.concat(silent);
            outputFilePath = outputFilePath.concat(".txt");

            //Searching through the files for the arguments.
            File outputFile = new File(outputFilePath);
            FileWriter writer = new FileWriter(outputFile, true);
            writer.write("");

            //Caching the first argument's value so that it can be used to optimize the search
            String firstArgumentValue = null;
            if (!arguments.isEmpty()) {
                Map.Entry<String, String> entry = arguments.entrySet().iterator().next();
                firstArgumentValue = entry.getValue();
            }

            for (File f : filesToRead) {
                try {

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                    String line;

                    lineReader:
                    while ((line = bufferedReader.readLine()) != null) {
                        try {

                            String lineCopy = line;

                            if (firstArgumentValue != null) {
                                if (!lineCopy.toLowerCase().contains(firstArgumentValue.toLowerCase()))
                                    continue lineReader;
                            }

                            if (!lineCopy.startsWith("|") || !lineCopy.endsWith("|"))
                                continue lineReader;

                            HashMap<String, String> lineArguments = new HashMap<>();

                            while (lineCopy.indexOf("|") + 1 != lineCopy.length() && lineCopy.contains(":")) {
                                try {

                                    lineCopy = lineCopy.substring(lineCopy.indexOf("|") + 1);
                                    String argumentName = lineCopy.substring(0, lineCopy.indexOf(":"));
                                    lineCopy = lineCopy.substring(lineCopy.indexOf(":") + 1);
                                    String argument = lineCopy.substring(0, lineCopy.indexOf("|"));
                                    lineCopy = lineCopy.substring(lineCopy.indexOf("|"));

                                    lineArguments.put(argumentName, argument);

                                } catch (Throwable ignored) {
                                }
                            }

                            try {
                                for (Map.Entry<String, String> inputArguments : arguments.entrySet()) {

                                    if (!lineArguments.containsKey(inputArguments.getKey()))
                                        continue lineReader;

                                    if (!lineArguments.get(inputArguments.getKey()).toLowerCase().contains(inputArguments.getValue().toLowerCase()))
                                        continue lineReader;

                                }
                            } catch (Throwable ignored) {
                            }

                            //Writes only those lines which contain all the provided arguments, it skips over the rest. If the radius is provided checks that.
                            if (!radiusString.equals("-1")) {

                                if (!(sender instanceof Player))
                                    continue lineReader;

                                Player player = (Player) sender;

                                lineCopy = line;
                                if (lineCopy.contains("Location:")) {

                                    double radiusDouble = Double.parseDouble(radiusString);

                                    lineCopy = lineCopy.substring(lineCopy.indexOf("Location:") + 9);
                                    lineCopy = lineCopy.substring(0, lineCopy.indexOf("|"));

                                    Location location = Logging.getLocationFromBetterLocationString(lineCopy);

                                    if (!player.getLocation().getWorld().equals(location.getWorld()))
                                        continue lineReader;

                                    double logX = location.getX();
                                    double logZ = location.getZ();

                                    double distance = Math.sqrt(Math.pow((logX - playerX), 2) + Math.pow((logZ - playerZ), 2));

                                    if (distance > radiusDouble)
                                        continue lineReader;

                                } else if (lineCopy.contains("Area:")) {

                                    int radiusInteger = Integer.parseInt(radiusString);

                                    lineCopy = lineCopy.substring(lineCopy.indexOf("Area:") + 5);
                                    lineCopy = lineCopy.substring(0, lineCopy.indexOf("|"));

                                    Location lesserCornerClaim = Logging.getLocationFromBetterLocationString(lineCopy.substring(0, lineCopy.indexOf(" - ")));
                                    Location greaterCornerClaim = Logging.getLocationFromBetterLocationString(lineCopy.substring(lineCopy.indexOf(" - ") + 3));
                                    greaterCornerClaim.setY(255);

                                    Location lesserCornerPlayer = player.getLocation();
                                    lesserCornerPlayer.setX(lesserCornerPlayer.getX() - radiusInteger);
                                    lesserCornerPlayer.setY(lesserCornerPlayer.getY() - radiusInteger);
                                    lesserCornerPlayer.setZ(lesserCornerPlayer.getZ() - radiusInteger);

                                    Location greaterCornerPlayer = player.getLocation();
                                    greaterCornerPlayer.setX(greaterCornerPlayer.getX() + radiusInteger);
                                    greaterCornerPlayer.setY(greaterCornerPlayer.getY() + radiusInteger);
                                    greaterCornerPlayer.setZ(greaterCornerPlayer.getZ() + radiusInteger);

                                    if (!lesserCornerClaim.getWorld().equals(lesserCornerPlayer.getWorld()))
                                        continue lineReader;

                                    boolean isOverlapping = (lesserCornerClaim.getBlockX() <= greaterCornerPlayer.getBlockX() && greaterCornerClaim.getBlockX() >= lesserCornerPlayer.getBlockX()) && (lesserCornerClaim.getBlockY() <= greaterCornerPlayer.getBlockY() && greaterCornerClaim.getBlockY() >= lesserCornerPlayer.getBlockY()) && (lesserCornerClaim.getBlockZ() <= greaterCornerPlayer.getBlockZ() && greaterCornerClaim.getBlockZ() >= lesserCornerPlayer.getBlockZ());

                                    if (!isOverlapping)
                                        continue lineReader;

                                }

                            }

                            writer.write(f.getName() + ":" + line + "\n");

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    bufferedReader.close();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            writer.close();

            //The maxFileSizeWithoutCompression is in MB so it's multiplied by 1mil to get the size in bytes.
            int maxFileSizeWithoutCompression = LottaLogs.getInstance().getConfig().getInt("SearchLogs.MaxFileSizeWithoutCompression");
            maxFileSizeWithoutCompression *= 1000000;

            //If the file is bigger than the defined limit, it gets compressed.
            if (outputFile.length() > maxFileSizeWithoutCompression) {

                UtilityMethods.compressFile(outputFile.getAbsolutePath(), outputFile.getAbsolutePath().concat(".gz"));

                outputFile = new File(outputFile.getAbsolutePath().concat(".gz"));

            }

            //The file with all the results is copied to the defined output path and all the other files are removed.
            String tempOutputFilePathString = outputFile.getAbsolutePath();
            tempOutputFilePathString = tempOutputFilePathString.substring(tempOutputFilePathString.lastIndexOf(File.separator) + 1);
            tempOutputFilePathString = LottaLogs.getInstance().getConfig().getString("SearchLogs.OutputPath") + File.separator + tempOutputFilePathString;

            if (outputFile.length() != 0)
                UtilityMethods.copyPasteFile(outputFile, (new File(tempOutputFilePathString)));
            else {
                sender.sendMessage(ChatColor.RED + "The results file is empty, not sending it.");
            }

            long endingTime = System.currentTimeMillis();
            long totalTime = endingTime - startingTime;
            int seconds = 0;
            while (totalTime >= 1000) {
                seconds += 1;
                totalTime -= 1000;
            }

            double totalFileSize = 0d;
            for (File file : filesToRead)
                totalFileSize += file.length();
            totalFileSize /= 1000000;

            clearTemporaryFiles();

            sender.sendMessage(ChatColor.GREEN + "Search completed, took " + seconds + "." + totalTime + "s. Scanned " + filesToRead.size() + " files with a total size of " + Math.round(totalFileSize * 100.0) / 100.0 + "MB.");
            LottaLogs.getInstance().getLogger().info("Search completed, took " + seconds + "." + totalTime + "s. Scanned " + filesToRead.size() + " files with a total size of " + Math.round(totalFileSize * 100.0) / 100.0 + "MB.");

            inUse = false;

        } catch (
                Throwable throwable) {
            UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchSpecialLogsCommand");
            throwable.printStackTrace();
            inUse = false;
        }

    }

    static void additionalSearch(CommandSender sender, Command command, String label, String[] args) {

        try {

            inUse = true;
            sender.sendMessage(ChatColor.YELLOW + "Search started.");
            LottaLogs.getInstance().getLogger().info("Search started.");
            long startingTime = System.currentTimeMillis();

            //Deleting all the temporary files in case some are left.
            clearTemporaryFiles();

            if (args.length < 4) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchAdditionalLogsCommand");
                inUse = false;
                return;
            }

            String logName = args[1];

            if (!Logging.getAdditionalLogNames().contains(logName)) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchAdditionalLogsCommand");
                inUse = false;
                return;
            }

            int days;
            try {
                days = Integer.parseInt(args[2]);
            } catch (Throwable throwable) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchSpecialLogsCommand");
                inUse = false;
                return;
            }

            String silent = "";
            for (String arg : args) {
                if (arg.equals("-silent")) {
                    silent = "-silent";
                    break;
                }
            }

            String searchString = "";
            for (int i = 3; i < args.length; i++) {
                if (args[i].equals("-silent")) continue;
                if (!searchString.isEmpty())
                    searchString = searchString.concat(" ");
                searchString = searchString.concat(args[i]);
            }
            searchString = searchString.toLowerCase();

            String logsDirectoryPath = null;

            for (String s : LottaLogs.getInstance().getConfig().getStringList("AdditionalLogs")) {
                if (s.contains("Name:" + logName)) {
                    logsDirectoryPath = s.substring(s.indexOf("Path:") + 5);
                }
            }

            if (logsDirectoryPath == null) {
                UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchSpecialLogsCommand");
                inUse = false;
                return;
            }

            //Getting a list of all the log files.
            List<File> filesToRead = new ArrayList<>(Arrays.asList(new File(logsDirectoryPath).listFiles()));

            //Removing from that list all the ones that aren't in the defined time limit.
            List<File> filesToRemove = new ArrayList<>();
            for (File file : filesToRead) {
                try {

                    int day = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[0];
                    int month = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[1];
                    int year = UtilityMethods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[2];
                    LocalDate fileDateLD = LocalDate.of(year, month, day);

                    int epochDayOfFileCreation = Math.toIntExact(fileDateLD.toEpochDay());
                    int epochDayRightNow = Math.toIntExact(LocalDate.now().toEpochDay());

                    if (epochDayRightNow - days > epochDayOfFileCreation) {
                        filesToRemove.add(file);
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
            filesToRead.removeAll(filesToRemove);

            //Copying all the files that need to be read to /temporary-files/.
            for (File f : filesToRead) {
                UtilityMethods.copyPasteFile(new File(f.getAbsolutePath()), new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + f.getName()));
            }

            //Reloading the list of files that need to be read with the files from /temporary-files/.
            filesToRead.clear();
            filesToRead.addAll(Arrays.asList(new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files").listFiles()));
            filesToRead.sort(Comparator.naturalOrder());

            String serverName = LottaLogs.getInstance().getDataFolder().getAbsolutePath();
            try {
                serverName = serverName.substring(0, serverName.indexOf(File.separator + "plugins"));
                serverName = serverName.substring(serverName.lastIndexOf(File.separator) + 1);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                serverName = "unknownServerName";
            }

            //The file is first written to /temporary-files/. It'll get moved after the whole search is completed.
            String outputFilePath = "";
            outputFilePath = outputFilePath.concat(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator);
            outputFilePath = outputFilePath.concat(File.separator);
            outputFilePath = outputFilePath.concat(serverName);
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(logName);
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(sender.getName());
            outputFilePath = outputFilePath.concat("-");
            outputFilePath = outputFilePath.concat(String.valueOf(System.currentTimeMillis()));
            outputFilePath = outputFilePath.concat(silent);
            outputFilePath = outputFilePath.concat(".txt");

            //Searching through the files for the arguments.
            File outputFile = new File(outputFilePath);
            FileWriter writer = new FileWriter(outputFile, true);
            writer.write("");

            for (File f : filesToRead) {
                try {

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                    String line;

                    lineReader:
                    while ((line = bufferedReader.readLine()) != null) {

                        if (!line.toLowerCase().matches("(.*)" + searchString + "(.*)")) continue lineReader;

                        //Writes only those lines which contain the provided search string, it skips over the rest.
                        writer.write(f.getName() + ":" + line + "\n");

                    }

                    bufferedReader.close();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            writer.close();

            //The maxFileSizeWithoutCompression is in MB so it's multiplied by 1mil to get the size in bytes.
            int maxFileSizeWithoutCompression = LottaLogs.getInstance().getConfig().getInt("SearchLogs.MaxFileSizeWithoutCompression");
            maxFileSizeWithoutCompression *= 1000000;

            //If the file is bigger than the defined limit, it gets compressed.
            if (outputFile.length() > maxFileSizeWithoutCompression) {

                UtilityMethods.compressFile(outputFile.getAbsolutePath(), outputFile.getAbsolutePath().concat(".gz"));

                outputFile = new File(outputFile.getAbsolutePath().concat(".gz"));

            }

            //The file with all the results is copied to the defined output path and all the other files are removed.
            String tempOutputFilePathString = outputFile.getAbsolutePath();
            tempOutputFilePathString = tempOutputFilePathString.substring(tempOutputFilePathString.lastIndexOf(File.separator) + 1);
            tempOutputFilePathString = LottaLogs.getInstance().getConfig().getString("SearchLogs.OutputPath") + File.separator + tempOutputFilePathString;

            if (outputFile.length() != 0)
                UtilityMethods.copyPasteFile(outputFile, (new File(tempOutputFilePathString)));
            else {
                sender.sendMessage(ChatColor.RED + "The results file is empty, not sending it.");
            }

            long endingTime = System.currentTimeMillis();
            long totalTime = endingTime - startingTime;
            int seconds = 0;
            while (totalTime >= 1000) {
                seconds += 1;
                totalTime -= 1000;
            }

            double totalFileSize = 0d;
            for (File file : filesToRead)
                totalFileSize += file.length();
            totalFileSize /= 1000000;

            clearTemporaryFiles();

            sender.sendMessage(ChatColor.GREEN + "Search completed, took " + seconds + "." + totalTime + "s. Scanned " + filesToRead.size() + " files with a total size of " + Math.round(totalFileSize * 100.0) / 100.0 + "MB.");
            LottaLogs.getInstance().getLogger().info("Search completed, took " + seconds + "." + totalTime + "s. Scanned " + filesToRead.size() + " files with a total size of " + Math.round(totalFileSize * 100.0) / 100.0 + "MB.");

            inUse = false;

        } catch (Throwable throwable) {
            UtilityMethods.sendConfigMessage(sender, "Messages.IncorrectUsageSearchNormalLogsCommand");
            throwable.printStackTrace();
            inUse = false;
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        try {

            //The 3 types of logs you can search

            if (args.length == 1) {

                List<String> choices = new ArrayList<>();
                if (sender.hasPermission("lottalogs.searchlogs.normal")) {
                    choices.add("normal");
                }
                if (sender.hasPermission("lottalogs.searchlogs.special")) {
                    choices.add("special");
                }
                if (sender.hasPermission("lottalogs.searchlogs.additional")) {
                    choices.add("additional");
                }

                List<String> completions = new ArrayList<>();
                for (String s : choices) {
                    if (s.startsWith(args[0])) {
                        completions.add(s);
                    }
                }

                return completions;

            }

            //Returning 0-9 for the days argument in the normal search

            if (sender.hasPermission("lottalogs.searchlogs.normal") && args.length == 2 && args[0].equals("normal") && args[1].isEmpty()) {

                List<String> completions = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    completions.add(String.valueOf(i));
                }

                return completions;

            }

            //Returning 0-9 for the days argument in the special search

            if (sender.hasPermission("lottalogs.searchlogs.special") && args.length == 3 && args[0].equals("special") && args[2].isEmpty()) {

                List<String> completions = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    completions.add(String.valueOf(i));
                }

                return completions;

            }

            //Returning 0-9 for the days argument in the additional search

            if (sender.hasPermission("lottalogs.searchlogs.additional") && args.length == 3 && args[0].equals("additional") && args[2].isEmpty()) {

                List<String> completions = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    completions.add(String.valueOf(i));
                }

                return completions;

            }

            //Returning the arguments for the special search

            if (sender.hasPermission("lottalogs.searchlogs.special") && args[0].equals("special")) {

                if (args.length == 2) {

                    String argument = args[1];

                    if (argument.contains(",")) argument = argument.substring(argument.lastIndexOf(",") + 1);

                    List<String> logNames = new ArrayList<>();
                    for (Log log : Logging.getCachedLogs()) {
                        logNames.add(log.getName());
                    }

                    List<String> completions = new ArrayList<>();
                    for (String name : logNames) {
                        if (name.toLowerCase().startsWith(argument.toLowerCase())) {
                            completions.add(args[1].substring(0, args[1].lastIndexOf(argument)) + name);
                        }
                    }

                    return completions;

                } else if (args.length > 2 && args.length % 2 == 0) {

                    String logNameArgument = args[1];
                    if (logNameArgument.contains(","))
                        logNameArgument = logNameArgument.substring(logNameArgument.lastIndexOf(",") + 1);

                    LinkedList<String> completions = new LinkedList<>();
                    for (String argument : Logging.getArgumentListFromLogName(logNameArgument)) {
                        if (argument.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                            completions.add(argument);
                        }
                    }

                    return completions;

                }

            }

            //Returning the arguments for the additional search

            if (sender.hasPermission("lottalogs.searchlogs.additional") && args[0].equals("additional")) {

                if (!LottaLogs.getInstance().getConfig().getBoolean("FeatureToggles.SearchAdditionalLogsCommand"))
                    return null;

                if (args.length == 2) {

                    List<String> completions = new ArrayList<>();
                    for (String name : Logging.getAdditionalLogNames()) {
                        if (name.startsWith(args[1])) {
                            completions.add(name);
                        }
                    }

                    return completions;

                }

            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return null;

    }

    public static void clearTemporaryFiles() {

        for (File file : new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files").listFiles())
            if (!file.delete())
                LottaLogs.getInstance().getLogger().warning("Something failed during the deletion of temporary-files file " + file.getAbsolutePath());

    }

}