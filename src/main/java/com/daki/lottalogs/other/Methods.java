package com.daki.lottalogs.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import com.daki.lottalogs.LottaLogs;

public final class Methods {

    private Methods() {}

    public static void sendMessageAndLog(CommandSender receiver, String message) {

        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        if (!(receiver instanceof ConsoleCommandSender)) {
            LottaLogs.getInstance().getLogger().info(message.replaceAll("&.", ""));
        }

    }

    public static String getDateStringYYYYMMDD() {

        Integer day = LocalDate.now().getDayOfMonth();
        Integer month = LocalDate.now().getMonthValue();
        Integer year = LocalDate.now().getYear();

        String date = "";

        date = date.concat(year.toString());
        date = date.concat("-");

        if (month < 10) {
            date = date.concat("0");
        }
        date = date.concat(month.toString());
        date = date.concat("-");

        if (day < 10) {
            date = date.concat("0");
        }
        date = date.concat(day.toString());

        return date;

    }

    public static int[] getDateValuesFromStringYYYYMMDD(String dateString) {

        int[] values = new int[3];

        values[0] = Integer.valueOf(dateString.substring(8, 10)); // day
        values[1] = Integer.valueOf(dateString.substring(5, 7)); // month
        values[2] = Integer.valueOf(dateString.substring(0, 4)); // year

        return values;

    }

    public static boolean compressFile(String inputPath, String outputPath) throws Throwable {

        FileOutputStream fos = new FileOutputStream(outputPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(inputPath);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();

        return true;

    }

    public static boolean uncompressFile(String inputPath, String outputPath) throws Throwable {

        File destDir = new File(outputPath);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputPath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                File destFile = new File(destDir, zipEntry.getName());

                String destDirPath = destDir.getCanonicalPath();
                String destFilePath = destFile.getCanonicalPath();

                if (!destFilePath.startsWith(destDirPath + File.separator)) {
                    throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
                }

                FileOutputStream fos = new FileOutputStream(destFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }
        return true;

    }

    public static boolean uncompressFileGZIP(String inputPath, String outputPath) throws Throwable {

        byte[] buffer = new byte[1024];

        FileInputStream fileIn = new FileInputStream(inputPath);

        GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);

        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);

        int bytes_read;

        while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {

            fileOutputStream.write(buffer, 0, bytes_read);
        }

        gZIPInputStream.close();
        fileOutputStream.close();

        return true;

    }

    public static void copyPasteFile(File file, File destination) throws Throwable {

        try (InputStream is = new FileInputStream(file); OutputStream os = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }

    }

    public static Location getLocationFromBetterLocationString(String string) {

        string = string.substring(7);

        String worldName = string.substring(0, string.indexOf(" "));

        string = string.substring(string.indexOf(" ") + 1);
        string = string.substring(12);

        // String dimension = string.substring(0, string.indexOf(" "));

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

    public static void deleteDirectory(File directory) {

        if (directory.isDirectory()) {

            File[] files = directory.listFiles();

            if (files != null) {

                for (File file : files) {

                    deleteDirectory(file);

                }

            }

        }

        directory.delete();

    }

}
