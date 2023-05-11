package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.Methods;

public class PlayerLocationLog extends Log {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Location"};

    public PlayerLocationLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    public static void startPlayerLocationLog(int writeFrequencySeconds) {
        new BukkitRunnable() {
            @Override
            public void run() {

                startPlayerLocationLog(LottaLogs.getInstance().getConfig().getInt("Logging.PlayerLocationLog.WriteFrequencySeconds"));

                if (!Logging.getCachedLogs().get("PlayerLocationLog").isEnabled())
                    return;

                for (Player player : Bukkit.getOnlinePlayers()) {

                    String time = new Date(System.currentTimeMillis()).toString();

                    String playerName = player.getName();

                    String location = Methods.getBetterLocationString(player.getLocation());

                    Logging.addToLogWriteQueue(new PlayerLocationLog(new String[] {time, playerName, location}));

                }

            }
        }.runTaskLater(LottaLogs.getInstance(), writeFrequencySeconds * 20);
    }

}
