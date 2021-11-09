package com.daki.lottalogs.logging.listeners;

import com.daki.lottalogs.logging.Logging;
import com.daki.lottalogs.logging.logs.MyPetItemPickupLog;
import de.Keyle.MyPet.api.event.MyPetPickupItemEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Date;

public class LoggingMyPet implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMyPetPickupItemEvent(MyPetPickupItemEvent event) {

        if (!Logging.getCachedLogFromName("MyPetItemPickupLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String pet = event.getPet().toString();

        String owner = event.getOwner().getPlayer().getName();

        String item = event.getItem().getItemStack().toString();

        String location = Logging.getBetterLocationString(event.getPet().getLocation().get());

        MyPetItemPickupLog log = new MyPetItemPickupLog();
        log.addArgumentValue(time);
        log.addArgumentValue(pet);
        log.addArgumentValue(owner);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

}
