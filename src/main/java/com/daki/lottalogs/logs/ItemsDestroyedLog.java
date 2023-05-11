package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;

public class ItemsDestroyedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Item", "Location", "Cause"};

    public ItemsDestroyedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageEvent(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Item))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String item = ((Item) event.getEntity()).getItemStack().toString();

        String location = Methods.getBetterLocationString(event.getEntity().getLocation());

        String cause = event.getCause().toString();

        Logging.addToLogWriteQueue(new ItemsDestroyedLog(new String[] {time, item, location, cause}));

    }

}
