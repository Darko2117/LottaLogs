package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class ItemsDespawnedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Item", "Location"};

    public ItemsDespawnedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawnEvent(ItemDespawnEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String item = event.getEntity().getItemStack().toString();

        String location = Methods.getBetterLocationString(event.getLocation());

        Logging.addToLogWriteQueue(new ItemsDespawnedLog(new String[] {time, item, location}));

    }

}
