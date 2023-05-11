package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class EggsThrownLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Location"};

    public EggsThrownLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String location = Methods.getBetterLocationString(event.getEgg().getLocation());

        Logging.addToLogWriteQueue(new EggsThrownLog(new String[] {time, player, location}));

    }

}
