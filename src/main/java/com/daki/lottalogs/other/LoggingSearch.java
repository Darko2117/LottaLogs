package com.daki.lottalogs.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logs.Log;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoggingSearch implements CommandExecutor, TabCompleter {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || !(args[0].equals("normal") || args[0].equals("special") || args[0].equals("additional"))) {
            Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs <normal/special/additional>.");
            return true;
        }

        if (!sender.hasPermission("lottalogs.searchlogs." + args[0])) {
            Methods.sendMessageAndLog(sender, "&cYou do not have permission to do this.");
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {

                    if (args[0].equals("normal")) {
                        normalSearch(sender, args);
                        return;
                    }
                    if (args[0].equals("special")) {
                        specialSearch(sender, args);
                        return;
                    }
                    if (args[0].equals("additional")) {
                        additionalSearch(sender, args);
                        return;
                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }.runTaskAsynchronously(LottaLogs.getInstance());

        return true;

    }

    static void normalSearch(CommandSender sender, String[] args) {

        long startTime = System.currentTimeMillis();

        File temporaryFilesDirectory = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + sender.getName() + "-" + startTime);
        temporaryFilesDirectory.mkdir();

        boolean shouldDoBossBar = (sender instanceof Player);
        BossBar progressBossBar = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "STARTING SEARCH", BarColor.YELLOW, BarStyle.SEGMENTED_10);
        double bossBarProgress = 0;
        if (shouldDoBossBar) {
            progressBossBar.setProgress(bossBarProgress);
            progressBossBar.addPlayer((Player) sender);
        }

        try {

            if (args.length < 3) {
                Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs normal <numberOfDays> <searchString>.");
                return;
            }

            int days;
            try {
                days = Integer.parseInt(args[1]);
            } catch (Throwable throwable) {
                Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs normal <numberOfDays> <searchString>.");
                return;
            }

            Methods.sendMessageAndLog(sender, "&6Search started.");

            boolean silent = false;
            for (String arg : args) {
                if (arg.equals("-silent")) {
                    silent = true;
                    break;
                }
            }

            String searchString = "";
            {
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 2; i < args.length; i++) {

                    if (args[i].startsWith("-"))
                        continue;

                    if (!searchString.isEmpty())
                        stringBuilder.append(" ");

                    stringBuilder.append(args[i]);

                }
                searchString = stringBuilder.toString().toLowerCase();
            }

            List<String> blacklistedStrings = LottaLogs.getInstance().getConfig().getStringList("SearchLogs.NormalSearchBlacklistedStrings");

            List<File> filesToRead = new ArrayList<>(Arrays.asList(new File(new File("").getAbsolutePath() + File.separator + "logs" + File.separator).listFiles()));

            {
                List<File> filesToRemove = new ArrayList<>();
                for (File file : filesToRead) {
                    try {

                        int day = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[0];
                        int month = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[1];
                        int year = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[2];
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
                filesToRead.sort(Comparator.naturalOrder());
            }

            String serverName = new File("").getAbsolutePath();
            serverName = serverName.substring(serverName.lastIndexOf(File.separator) + 1, serverName.length());

            String outputFilePath = "";
            {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(temporaryFilesDirectory);
                stringBuilder.append(File.separator);
                stringBuilder.append(serverName);
                stringBuilder.append("-");
                stringBuilder.append("normalLogs");
                stringBuilder.append("-");
                stringBuilder.append(sender.getName());
                stringBuilder.append("-");
                stringBuilder.append(startTime);
                if (silent)
                    stringBuilder.append("-silent");
                stringBuilder.append(".txt");

                outputFilePath = stringBuilder.toString();
            }

            File outputFile;
            FileWriter writer;
            try {
                outputFile = new File(outputFilePath);
                writer = new FileWriter(outputFile, true);
                writer.write("");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LottaLogs.getInstance().getLogger().warning("Failed to initialize writer, search cancelled.");
                return;
            }

            for (int i = 0; i < filesToRead.size(); i++) {
                try {

                    File file = filesToRead.get(i);

                    if (shouldDoBossBar) {
                        bossBarProgress = (double) i / (double) (filesToRead.size());
                        progressBossBar.setProgress(bossBarProgress);
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "READING FILE: " + file.getName());
                    }

                    if (file.getName().contains(".gz")) {

                        String outputPath = temporaryFilesDirectory + File.separator + file.getName().replace(".gz", "");

                        if (!Methods.uncompressFileGZIP(file.getAbsolutePath(), outputPath)) {
                            LottaLogs.getInstance().getLogger().warning("Something failed during extraction of the file " + file.getAbsolutePath());
                        }

                        file = new File(outputPath);

                    } else {

                        Methods.copyPasteFile(new File(file.getAbsolutePath()), new File(temporaryFilesDirectory + File.separator + file.getName()));

                        file = new File(temporaryFilesDirectory + File.separator + file.getName());

                    }

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line;

                    lineReader: while ((line = bufferedReader.readLine()) != null) {

                        if (!line.toLowerCase().matches("(.*)" + searchString + "(.*)"))
                            continue lineReader;

                        for (String blacklistedString : blacklistedStrings) {
                            if (line.toLowerCase().contains(blacklistedString)) {
                                writer.write(file.getName() + ":" + "This line contained a blacklisted string. Skipping it." + "\n");
                                continue lineReader;
                            }
                        }

                        writer.write(file.getName() + ":" + line + "\n");

                    }

                    bufferedReader.close();

                    file.delete();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            if (shouldDoBossBar) {
                progressBossBar.setProgress(1);
                progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "CLOSING WRITER");
            }

            try {
                writer.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LottaLogs.getInstance().getLogger().warning("Failed to close writer.");
            }

            int fileSizeUploadLimitMB = LottaLogs.getInstance().getConfig().getInt("SearchLogs.FileSizeUploadLimitMB") * 1000000;

            if (outputFile.length() > fileSizeUploadLimitMB) {
                try {

                    if (shouldDoBossBar) {
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "COMPRESSING RESULT");
                    }

                    Methods.compressFile(outputFile.getAbsolutePath(), outputFile.getAbsolutePath().concat(".gz"));
                    outputFile.delete();
                    outputFile = new File(outputFile.getAbsolutePath().concat(".gz"));

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    LottaLogs.getInstance().getLogger().warning("Failed to compress the output file.");
                }
            }

            String tempOutputFilePathString = outputFile.getAbsolutePath();
            tempOutputFilePathString = tempOutputFilePathString.substring(tempOutputFilePathString.lastIndexOf(File.separator) + 1);
            tempOutputFilePathString = LottaLogs.getInstance().getConfig().getString("SearchLogs.OutputPath") + File.separator + tempOutputFilePathString;

            if (outputFile.length() != 0) {
                try {

                    if (shouldDoBossBar) {
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "MOVING RESULT");
                    }

                    Methods.copyPasteFile(outputFile, (new File(tempOutputFilePathString)));
                    outputFile.delete();
                    outputFile = new File(tempOutputFilePathString);

                    if (!silent) {

                        try {

                            String webhookURL = LottaLogs.getInstance().getConfig().getString("SearchLogs.DiscordChannelWebhook");

                            if (outputFile.length() > fileSizeUploadLimitMB) {

                                Methods.sendMessageAndLog(sender, "&cFile too large to send to discord.");

                            } else if (webhookURL != null && !webhookURL.equals("https://discord.com/api/webhooks/0123456789123456789/So2Me5Ra0Nd4Om2To4Ke5N")) {

                                if (shouldDoBossBar) {
                                    progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "SENDING RESULT TO DISCORD");
                                }

                                new OkHttpClient().newCall(new Request.Builder().url(webhookURL).post(new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", outputFile.getName(), RequestBody.create(outputFile, MediaType.parse("application/octet-stream"))).build()).build()).execute().close();;

                            }

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            LottaLogs.getInstance().getLogger().warning("Failed to send file to discord.");
                        }

                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    LottaLogs.getInstance().getLogger().warning("Failed to copy file to output directory.");
                }
            } else {
                Methods.sendMessageAndLog(sender, "&cThe results file is empty, not sending it.");
            }

            long totalTime = System.currentTimeMillis() - startTime;

            double resultFileSize = outputFile.length() / 1000000d;

            String message = "&aSearch completed, took " + totalTime + "ms. Result file size is " + Math.round(resultFileSize * 100.0) / 100.0 + "MB.";

            Methods.sendMessageAndLog(sender, message);

        } finally {
            Methods.deleteDirectory(temporaryFilesDirectory);
            progressBossBar.removeAll();
        }

    }

    static void specialSearch(CommandSender sender, String[] args) {

        long startTime = System.currentTimeMillis();

        File temporaryFilesDirectory = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + sender.getName() + "-" + startTime);
        temporaryFilesDirectory.mkdir();

        boolean shouldDoBossBar = (sender instanceof Player);
        BossBar progressBossBar = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "STARTING SEARCH", BarColor.YELLOW, BarStyle.SEGMENTED_10);
        double bossBarProgress = 0;
        if (shouldDoBossBar) {
            progressBossBar.setProgress(bossBarProgress);
            progressBossBar.addPlayer((Player) sender);
        }

        try {

            if (args.length < 5) {
                Methods.sendMessageAndLog(sender, "&cWrong usage of this command, honestly just check the wiki on how to use it, I can't explain it in one message...");
                return;
            }

            int days;
            try {
                days = Integer.parseInt(args[2]);
            } catch (Throwable throwable) {
                Methods.sendMessageAndLog(sender, "&cWrong usage of this command, honestly just check the wiki on how to use it, I can't explain it in one message...");
                return;
            }

            HashSet<String> logNames = new HashSet<>(Arrays.asList(args[1].split(",")));
            for (String logName : logNames) {
                if (!Logging.getCachedLogs().containsKey(logName)) {
                    Methods.sendMessageAndLog(sender, "&cWrong usage of this command, honestly just check the wiki on how to use it, I can't explain it in one message...");
                    return;
                }
            }

            Methods.sendMessageAndLog(sender, "&6Search started.");

            boolean silent = false;
            for (String arg : args) {
                if (arg.equals("-silent")) {
                    silent = true;
                    break;
                }
            }

            double playerX = 0;
            double playerZ = 0;
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

                if (args[i].equalsIgnoreCase("-silent"))
                    continue;

                if (!args[i].contains(":"))
                    continue;

                argumentKey = args[i].replace(":", "");

                for (int j = i + 1; j < args.length; j++) {

                    if (args[j].equalsIgnoreCase("-silent"))
                        break;

                    if (args[j].contains(":"))
                        break;

                    if (!argumentValues.isEmpty())
                        argumentValues = argumentValues.concat(" ");
                    argumentValues = argumentValues.concat(args[j]);

                }

                arguments.put(argumentKey, argumentValues);

            }

            List<File> filesToRead = new ArrayList<>();

            {
                List<File> allLogs = new ArrayList<>();
                allLogs.addAll(Arrays.asList(new File(LottaLogs.getInstance().getDataFolder() + File.separator + "logs" + File.separator).listFiles()));
                allLogs.addAll(Arrays.asList(new File(LottaLogs.getInstance().getDataFolder() + File.separator + "compressed-logs" + File.separator).listFiles()));
                for (File file : allLogs) {
                    try {
                        String fileName = file.getName().substring(11, file.getName().indexOf("."));
                        if (logNames.contains(fileName)) {
                            filesToRead.add(file);
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        LottaLogs.getInstance().getLogger().warning("Error with reading the file name: " + file.getName());
                    }
                }
            }

            {
                List<File> filesToRemove = new ArrayList<>();
                for (File file : filesToRead) {
                    try {

                        int day = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[0];
                        int month = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[1];
                        int year = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[2];
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
                filesToRead.sort(Comparator.naturalOrder());
            }

            String serverName = new File("").getAbsolutePath();
            serverName = serverName.substring(serverName.lastIndexOf(File.separator) + 1, serverName.length());

            String logNamesString;
            {
                StringBuilder stringBuilder = new StringBuilder();
                List<String> sortedLogNames = new ArrayList<>(logNames);
                Collections.sort(sortedLogNames, Comparator.naturalOrder());
                for (String logName : sortedLogNames) {
                    if (!stringBuilder.isEmpty())
                        stringBuilder.append("-");
                    stringBuilder.append(logName);
                }
                logNamesString = stringBuilder.toString();
            }

            String outputFilePath = "";
            {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(temporaryFilesDirectory);
                stringBuilder.append(File.separator);
                stringBuilder.append(serverName);
                stringBuilder.append("-");
                stringBuilder.append(logNamesString);
                stringBuilder.append("-");
                stringBuilder.append(sender.getName());
                stringBuilder.append("-");
                stringBuilder.append(startTime);
                if (silent)
                    stringBuilder.append("-silent");
                stringBuilder.append(".txt");

                outputFilePath = stringBuilder.toString();
            }

            File outputFile;
            FileWriter writer;
            try {
                outputFile = new File(outputFilePath);
                writer = new FileWriter(outputFile, true);
                writer.write("");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LottaLogs.getInstance().getLogger().warning("Failed to initialize writer, search cancelled.");
                return;
            }

            for (int i = 0; i < filesToRead.size(); i++) {
                try {

                    File file = filesToRead.get(i);

                    if (shouldDoBossBar) {
                        bossBarProgress = (double) i / (double) (filesToRead.size());
                        progressBossBar.setProgress(bossBarProgress);
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "READING FILE: " + file.getName());
                    }

                    if (file.getName().contains(".gz")) {

                        if (!Methods.uncompressFile(file.getAbsolutePath(), temporaryFilesDirectory.getAbsolutePath())) {
                            LottaLogs.getInstance().getLogger().warning("Something failed during extraction of the file " + file.getAbsolutePath());
                        }

                        file = new File(temporaryFilesDirectory + File.separator + file.getName().replace(".gz", ""));

                    } else {

                        Methods.copyPasteFile(new File(file.getAbsolutePath()), new File(temporaryFilesDirectory + File.separator + file.getName()));

                        file = new File(temporaryFilesDirectory + File.separator + file.getName());

                    }

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line;

                    lineReader: while ((line = bufferedReader.readLine()) != null) {
                        try {

                            String lineCopy = line;

                            if (!lineCopy.startsWith("|") || !lineCopy.endsWith("|"))
                                continue lineReader;

                            HashMap<String, String> lineArguments = new HashMap<>();

                            while (lineCopy.indexOf("|") + 1 != lineCopy.length() && lineCopy.contains(":")) {
                                try {

                                    lineCopy = lineCopy.substring(lineCopy.indexOf("|") + 1);
                                    String argumentName = lineCopy.substring(0, lineCopy.indexOf(":"));
                                    lineCopy = lineCopy.substring(lineCopy.indexOf(":") + 1);
                                    String argumentValue = lineCopy.substring(0, lineCopy.indexOf("|"));
                                    lineCopy = lineCopy.substring(lineCopy.indexOf("|"));

                                    lineArguments.put(argumentName, argumentValue);

                                } catch (Throwable ignored) {
                                }
                            }

                            try {
                                for (Map.Entry<String, String> inputArguments : arguments.entrySet()) {

                                    if (!lineArguments.containsKey(inputArguments.getKey()))
                                        continue lineReader;

                                    if (!lineArguments.get(inputArguments.getKey()).toLowerCase().matches("(.*)" + inputArguments.getValue().toLowerCase() + "(.*)"))
                                        continue lineReader;

                                }
                            } catch (Throwable ignored) {
                            }

                            if (!radiusString.equals("-1")) {

                                if (!(sender instanceof Player))
                                    continue lineReader;

                                Player player = (Player) sender;

                                lineCopy = line;
                                if (lineCopy.contains("Location:")) {

                                    double radiusDouble = Double.parseDouble(radiusString);

                                    lineCopy = lineCopy.substring(lineCopy.indexOf("Location:") + 9);
                                    lineCopy = lineCopy.substring(0, lineCopy.indexOf("|"));

                                    Location location = Methods.getLocationFromBetterLocationString(lineCopy);

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

                                    Location lesserCornerClaim = Methods.getLocationFromBetterLocationString(lineCopy.substring(0, lineCopy.indexOf(" - ")));
                                    Location greaterCornerClaim = Methods.getLocationFromBetterLocationString(lineCopy.substring(lineCopy.indexOf(" - ") + 3));
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

                            writer.write(file.getName() + ":" + line + "\n");

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    bufferedReader.close();

                    file.delete();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            if (shouldDoBossBar) {
                progressBossBar.setProgress(1);
                progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "CLOSING WRITER");
            }

            try {
                writer.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LottaLogs.getInstance().getLogger().warning("Failed to close writer.");
            }

            int fileSizeUploadLimitMB = LottaLogs.getInstance().getConfig().getInt("SearchLogs.FileSizeUploadLimitMB") * 1000000;

            if (outputFile.length() > fileSizeUploadLimitMB) {
                try {

                    if (shouldDoBossBar) {
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "COMPRESSING RESULT");
                    }

                    Methods.compressFile(outputFile.getAbsolutePath(), outputFile.getAbsolutePath().concat(".gz"));
                    outputFile.delete();
                    outputFile = new File(outputFile.getAbsolutePath().concat(".gz"));

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    LottaLogs.getInstance().getLogger().warning("Failed to compress the output file.");
                }
            }

            String tempOutputFilePathString = outputFile.getAbsolutePath();
            tempOutputFilePathString = tempOutputFilePathString.substring(tempOutputFilePathString.lastIndexOf(File.separator) + 1);
            tempOutputFilePathString = LottaLogs.getInstance().getConfig().getString("SearchLogs.OutputPath") + File.separator + tempOutputFilePathString;

            if (outputFile.length() != 0) {
                try {

                    if (shouldDoBossBar) {
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "MOVING RESULT");
                    }

                    Methods.copyPasteFile(outputFile, (new File(tempOutputFilePathString)));
                    outputFile.delete();
                    outputFile = new File(tempOutputFilePathString);

                    if (!silent) {

                        try {

                            String webhookURL = LottaLogs.getInstance().getConfig().getString("SearchLogs.DiscordChannelWebhook");

                            if (outputFile.length() > fileSizeUploadLimitMB) {

                                Methods.sendMessageAndLog(sender, "&cFile too large to send to discord.");

                            } else if (webhookURL != null && !webhookURL.equals("https://discord.com/api/webhooks/0123456789123456789/So2Me5Ra0Nd4Om2To4Ke5N")) {

                                if (shouldDoBossBar) {
                                    progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "SENDING RESULT TO DISCORD");
                                }

                                new OkHttpClient().newCall(new Request.Builder().url(webhookURL).post(new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", outputFile.getName(), RequestBody.create(outputFile, MediaType.parse("application/octet-stream"))).build()).build()).execute().close();;

                            }

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            LottaLogs.getInstance().getLogger().warning("Failed to send file to discord.");
                        }

                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    LottaLogs.getInstance().getLogger().warning("Failed to copy file to output directory.");
                }
            } else {
                Methods.sendMessageAndLog(sender, "&cThe results file is empty, not sending it.");
            }

            long totalTime = System.currentTimeMillis() - startTime;

            double resultFileSize = outputFile.length() / 1000000d;

            String message = "&aSearch completed, took " + totalTime + "ms. Result file size is " + Math.round(resultFileSize * 100.0) / 100.0 + "MB.";

            Methods.sendMessageAndLog(sender, message);

        } finally {
            Methods.deleteDirectory(temporaryFilesDirectory);
            progressBossBar.removeAll();
        }

    }

    static void additionalSearch(CommandSender sender, String[] args) {

        long startTime = System.currentTimeMillis();

        File temporaryFilesDirectory = new File(LottaLogs.getInstance().getDataFolder() + File.separator + "temporary-files" + File.separator + sender.getName() + "-" + startTime);
        temporaryFilesDirectory.mkdir();

        boolean shouldDoBossBar = (sender instanceof Player);
        BossBar progressBossBar = Bukkit.createBossBar(ChatColor.GOLD + "" + ChatColor.BOLD + "STARTING SEARCH", BarColor.YELLOW, BarStyle.SEGMENTED_10);
        double bossBarProgress = 0;
        if (shouldDoBossBar) {
            progressBossBar.setProgress(bossBarProgress);
            progressBossBar.addPlayer((Player) sender);
        }

        try {

            if (args.length < 4) {
                Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs additional <logName> <numberOfDays> <searchString>.");
                return;
            }

            String logName = args[1];

            if (!Logging.getAdditionalLogNames().contains(logName)) {
                Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs additional <logName> <numberOfDays> <searchString>.");
                return;
            }

            int days;
            try {
                days = Integer.parseInt(args[2]);
            } catch (Throwable throwable) {
                Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs additional <logName> <numberOfDays> <searchString>.");
                return;
            }

            Methods.sendMessageAndLog(sender, "&6Search started.");

            boolean silent = false;
            for (String arg : args) {
                if (arg.equals("-silent")) {
                    silent = true;
                    break;
                }
            }

            String searchString = "";
            {
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 3; i < args.length; i++) {

                    if (args[i].startsWith("-"))
                        continue;

                    if (!searchString.isEmpty())
                        stringBuilder.append(" ");

                    stringBuilder.append(args[i]);

                }
                searchString = stringBuilder.toString().toLowerCase();
            }

            String logsDirectoryPath = null;

            for (String string : LottaLogs.getInstance().getConfig().getStringList("AdditionalLogs")) {
                if (string.contains("Name:" + logName)) {
                    logsDirectoryPath = string.substring(string.indexOf("Path:") + 5);
                }
            }

            if (logsDirectoryPath == null) {
                Methods.sendMessageAndLog(sender, "&cUsage of this command is /searchlogs additional <logName> <numberOfDays> <searchString>.");
                return;
            }

            List<File> filesToRead = new ArrayList<>(Arrays.asList(new File(logsDirectoryPath).listFiles()));

            {
                List<File> filesToRemove = new ArrayList<>();
                for (File file : filesToRead) {
                    try {

                        int day = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[0];
                        int month = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[1];
                        int year = Methods.getDateValuesFromStringYYYYMMDD(file.getName().substring(0, 10))[2];
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
                filesToRead.sort(Comparator.naturalOrder());
            }

            String serverName = new File("").getAbsolutePath();
            serverName = serverName.substring(serverName.lastIndexOf(File.separator) + 1, serverName.length());

            String outputFilePath = "";
            {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(temporaryFilesDirectory);
                stringBuilder.append(File.separator);
                stringBuilder.append(serverName);
                stringBuilder.append("-");
                stringBuilder.append("normalLogs");
                stringBuilder.append("-");
                stringBuilder.append(sender.getName());
                stringBuilder.append("-");
                stringBuilder.append(startTime);
                if (silent)
                    stringBuilder.append("-silent");
                stringBuilder.append(".txt");

                outputFilePath = stringBuilder.toString();
            }

            File outputFile;
            FileWriter writer;
            try {
                outputFile = new File(outputFilePath);
                writer = new FileWriter(outputFile, true);
                writer.write("");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LottaLogs.getInstance().getLogger().warning("Failed to initialize writer, search cancelled.");
                return;
            }

            for (int i = 0; i < filesToRead.size(); i++) {
                try {

                    File file = filesToRead.get(i);

                    if (shouldDoBossBar) {
                        bossBarProgress = (double) i / (double) (filesToRead.size());
                        progressBossBar.setProgress(bossBarProgress);
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "READING FILE: " + file.getName());
                    }

                    if (file.getName().contains(".gz")) {

                        String outputPath = temporaryFilesDirectory + File.separator + file.getName().replace(".gz", "");

                        if (!Methods.uncompressFileGZIP(file.getAbsolutePath(), outputPath)) {
                            LottaLogs.getInstance().getLogger().warning("Something failed during extraction of the file " + file.getAbsolutePath());
                        }

                        file = new File(outputPath);

                    } else {

                        Methods.copyPasteFile(new File(file.getAbsolutePath()), new File(temporaryFilesDirectory + File.separator + file.getName()));

                        file = new File(temporaryFilesDirectory + File.separator + file.getName());

                    }

                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line;

                    lineReader: while ((line = bufferedReader.readLine()) != null) {

                        if (!line.toLowerCase().matches("(.*)" + searchString + "(.*)"))
                            continue lineReader;

                        writer.write(file.getName() + ":" + line + "\n");

                    }

                    bufferedReader.close();

                    file.delete();

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            if (shouldDoBossBar) {
                progressBossBar.setProgress(1);
                progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "CLOSING WRITER");
            }

            try {
                writer.close();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                LottaLogs.getInstance().getLogger().warning("Failed to close writer.");
            }

            int fileSizeUploadLimitMB = LottaLogs.getInstance().getConfig().getInt("SearchLogs.FileSizeUploadLimitMB") * 1000000;

            if (outputFile.length() > fileSizeUploadLimitMB) {
                try {

                    if (shouldDoBossBar) {
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "COMPRESSING RESULT");
                    }

                    Methods.compressFile(outputFile.getAbsolutePath(), outputFile.getAbsolutePath().concat(".gz"));
                    outputFile.delete();
                    outputFile = new File(outputFile.getAbsolutePath().concat(".gz"));

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    LottaLogs.getInstance().getLogger().warning("Failed to compress the output file.");
                }
            }

            String tempOutputFilePathString = outputFile.getAbsolutePath();
            tempOutputFilePathString = tempOutputFilePathString.substring(tempOutputFilePathString.lastIndexOf(File.separator) + 1);
            tempOutputFilePathString = LottaLogs.getInstance().getConfig().getString("SearchLogs.OutputPath") + File.separator + tempOutputFilePathString;

            if (outputFile.length() != 0) {
                try {

                    if (shouldDoBossBar) {
                        progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "MOVING RESULT");
                    }

                    Methods.copyPasteFile(outputFile, (new File(tempOutputFilePathString)));
                    outputFile.delete();
                    outputFile = new File(tempOutputFilePathString);

                    if (!silent) {

                        try {

                            String webhookURL = LottaLogs.getInstance().getConfig().getString("SearchLogs.DiscordChannelWebhook");

                            if (outputFile.length() > fileSizeUploadLimitMB) {

                                Methods.sendMessageAndLog(sender, "&cFile too large to send to discord.");

                            } else if (webhookURL != null && !webhookURL.equals("https://discord.com/api/webhooks/0123456789123456789/So2Me5Ra0Nd4Om2To4Ke5N")) {

                                if (shouldDoBossBar) {
                                    progressBossBar.setTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "SENDING RESULT TO DISCORD");
                                }

                                new OkHttpClient().newCall(new Request.Builder().url(webhookURL).post(new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", outputFile.getName(), RequestBody.create(outputFile, MediaType.parse("application/octet-stream"))).build()).build()).execute().close();;

                            }

                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            LottaLogs.getInstance().getLogger().warning("Failed to send file to discord.");
                        }

                    }

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    LottaLogs.getInstance().getLogger().warning("Failed to copy file to output directory.");
                }
            } else {
                Methods.sendMessageAndLog(sender, "&cThe results file is empty, not sending it.");
            }

            long totalTime = System.currentTimeMillis() - startTime;

            double resultFileSize = outputFile.length() / 1000000d;

            String message = "&aSearch completed, took " + totalTime + "ms. Result file size is " + Math.round(resultFileSize * 100.0) / 100.0 + "MB.";

            Methods.sendMessageAndLog(sender, message);

        } finally {
            Methods.deleteDirectory(temporaryFilesDirectory);
            progressBossBar.removeAll();
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        try {

            // The 3 types of logs you can search

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
                for (String choice : choices) {
                    if (choice.startsWith(args[0])) {
                        completions.add(choice);
                    }
                }

                return completions;

            }

            // Returning 0-9 for the days argument in the normal search

            if (sender.hasPermission("lottalogs.searchlogs.normal") && args.length == 2 && args[0].equals("normal") && args[1].isEmpty()) {

                List<String> completions = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    completions.add(String.valueOf(i));
                }

                return completions;

            }

            // Returning 0-9 for the days argument in the special search

            if (sender.hasPermission("lottalogs.searchlogs.special") && args.length == 3 && args[0].equals("special") && args[2].isEmpty()) {

                List<String> completions = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    completions.add(String.valueOf(i));
                }

                return completions;

            }

            // Returning 0-9 for the days argument in the additional search

            if (sender.hasPermission("lottalogs.searchlogs.additional") && args.length == 3 && args[0].equals("additional") && args[2].isEmpty()) {

                List<String> completions = new ArrayList<>();

                for (int i = 0; i < 10; i++) {
                    completions.add(String.valueOf(i));
                }

                return completions;

            }

            // Returning the arguments for the special search

            if (sender.hasPermission("lottalogs.searchlogs.special") && args[0].equals("special")) {

                if (args.length == 2) {

                    String argument = args[1];

                    if (argument.contains(","))
                        argument = argument.substring(argument.lastIndexOf(",") + 1);

                    List<String> logNames = new ArrayList<>();
                    for (Map.Entry<String, Log> entry : Logging.getCachedLogs().entrySet()) {
                        logNames.add(entry.getValue().getName());
                    }

                    List<String> completions = new ArrayList<>();
                    for (String name : logNames) {
                        if (name.toLowerCase().contains(argument.toLowerCase())) {
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

            // Returning the arguments for the additional search

            if (sender.hasPermission("lottalogs.searchlogs.additional") && args[0].equals("additional")) {

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

}
