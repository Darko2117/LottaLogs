package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class DroppedItemsLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item", "Location"};

    public DroppedItemsLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String item = event.getItemDrop().getItemStack().toString();

        String location = Methods.getBetterLocationString(event.getItemDrop().getLocation());

        Logging.addToLogWriteQueue(new DroppedItemsLog(new String[] {time, player, item, location}));

    }

}
