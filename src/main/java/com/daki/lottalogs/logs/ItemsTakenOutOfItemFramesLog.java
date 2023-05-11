package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class ItemsTakenOutOfItemFramesLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item", "Location"};

    public ItemsTakenOutOfItemFramesLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof ItemFrame))
            return;

        if (((ItemFrame) event.getEntity()).getItem().getType().equals(Material.AIR))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getDamager().getName();

        String item = ((ItemFrame) event.getEntity()).getItem().toString();

        String location = Methods.getBetterLocationString(event.getEntity().getLocation());

        Logging.addToLogWriteQueue(new ItemsTakenOutOfItemFramesLog(new String[] {time, player, item, location}));

    }

}
