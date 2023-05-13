package com.daki.lottalogs.logs;

import java.util.Date;
import java.util.HashSet;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class ItemsDestroyedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Item", "Location", "Cause"};

    private static final HashSet<Item> damagedItems = new HashSet<>();

    public ItemsDestroyedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageEvent(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Item))
            return;

        Item itemEntity = (Item) event.getEntity();

        damagedItems.add(itemEntity);

        new BukkitRunnable() {
            @Override
            public void run() {

                // Let's say item needs to burn for 5 ticks, on all 5 of these events isDead is false because it
                // becomes true only after the 5th call to this event. On both the 4th and the 5th tick it'll get
                // scheduled and be true when this runnable checks it. So in order to work correctly it needs to be
                // removed from the list on the runnable of the 4th tick so that the runnable of the 5th tick
                // doesn't see it. Order is 4th event tick, 5th event tick (becomes dead after this), 4th runnable
                // tick, 5th runnable tick.

                if (!itemEntity.isDead() || !damagedItems.contains(itemEntity))
                    return;

                damagedItems.remove(itemEntity);

                String time = new Date(System.currentTimeMillis()).toString();

                String item = ((Item) event.getEntity()).getItemStack().toString();

                String location = Methods.getBetterLocationString(event.getEntity().getLocation());

                String cause = event.getCause().toString();

                Logging.addToLogWriteQueue(new ItemsDestroyedLog(new String[] {time, item, location, cause}));

            }
        }.runTaskLater(LottaLogs.getInstance(), 1);

    }

}
