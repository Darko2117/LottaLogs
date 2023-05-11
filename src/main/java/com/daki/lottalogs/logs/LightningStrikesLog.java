package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class LightningStrikesLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Cause", "Location"};

    public LightningStrikesLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLightningStrikeEvent(LightningStrikeEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String location = Methods.getBetterLocationString(event.getLightning().getLocation());

        String cause = event.getCause().toString();

        Logging.addToLogWriteQueue(new LightningStrikesLog(new String[] {time, cause, location}));

    }

}
