package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;
import de.Keyle.MyPet.api.event.MyPetPickupItemEvent;

public class MyPetItemPickupLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Pet", "Player", "Item", "Location"};

    public MyPetItemPickupLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMyPetPickupItemEvent(MyPetPickupItemEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getOwner().getPlayer().getName();

        String pet = event.getPet().toString();

        String item = event.getItem().getItemStack().toString();

        String location = Methods.getBetterLocationString(event.getPet().getLocation().get());

        Logging.addToLogWriteQueue(new MyPetItemPickupLog(new String[] {time, player, pet, item, location}));

    }

}
