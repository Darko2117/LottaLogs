package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class ItemsBrokenLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item", "Location"};

    public ItemsBrokenLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String item = event.getBrokenItem().toString();

        String location = Methods.getBetterLocationString(event.getPlayer().getLocation());

        Logging.addToLogWriteQueue(new ItemsBrokenLog(new String[] {time, player, item, location}));

    }

}
