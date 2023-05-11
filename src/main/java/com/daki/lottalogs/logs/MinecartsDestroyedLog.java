package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class MinecartsDestroyedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Attacker", "Location"};

    public MinecartsDestroyedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVehicleDestroyEvent(VehicleDestroyEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String attacker = "Not a player";
        try {
            attacker = event.getAttacker().getName();
        } catch (Throwable ignored) {
        }

        String location = Methods.getBetterLocationString(event.getVehicle().getLocation());

        Logging.addToLogWriteQueue(new MinecartsDestroyedLog(new String[] {time, attacker, location}));

    }

}
