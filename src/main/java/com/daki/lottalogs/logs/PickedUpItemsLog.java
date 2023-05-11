package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;

public class PickedUpItemsLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item", "Location"};

    public PickedUpItemsLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityPickupItem(EntityPickupItemEvent event) {

        if (!(event.getEntity() instanceof Player))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getEntity().getName();

        String item = event.getItem().getItemStack().toString();

        String location = Methods.getBetterLocationString(event.getItem().getLocation());

        Logging.addToLogWriteQueue(new PickedUpItemsLog(new String[] {time, player, item, location}));

    }

}
