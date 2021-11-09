package com.daki.lottalogs.other;

import com.daki.lottalogs.LottaLogs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.Yaml;

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

public class UtilityMethods {

    public static boolean checkConfig() {

        try {
            InputStream inputStream = new FileInputStream(LottaLogs.getInstance().getDataFolder() + File.separator + "config.yml");
            Yaml config = new Yaml();
            config.load(inputStream);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }

        return true;

    }

    public static String getServerJarPath() {

        return new File(".").getAbsolutePath().substring(0, new File(".").getAbsolutePath().length() - 1);

    }

    public static String getDateStringYYYYMMDD() {

        StringBuilder date = new StringBuilder();

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        int day = LocalDate.now().getDayOfMonth();

        date.append(year).append("-");

        if (month < 10) {
            date.append("0");
        }
        date.append(month).append("-");

        if (day < 10) {
            date.append("0");
        }
        date.append(day);

        return date.toString();

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

    public static int[] getDateValuesFromStringYYYYMMDD(String dateString) {

        int[] values = new int[3];

        values[0] = Integer.parseInt(dateString.substring(8, 10)); //day
        values[1] = Integer.parseInt(dateString.substring(5, 7)); //month
        values[2] = Integer.parseInt(dateString.substring(0, 4)); //year

        return values;

    }

    public static void sendConfigMessage(CommandSender receiver, String path) {

        receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', LottaLogs.getInstance().getConfig().getString(path)));

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

    public static boolean uncompressFile(String inputPath, String outputPath) throws Throwable {

        File destDir = new File(outputPath);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(inputPath));
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

        return true;

    }

}
