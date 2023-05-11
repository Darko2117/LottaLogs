package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;
import me.filoghost.farmlimiter.api.FarmLimitEvent;

public class FarmLimiterLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Entity", "Location"};

    public FarmLimiterLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFarmLimitEvent(FarmLimitEvent event) {

        for (Entity entityToRemove : event.getEntitiesToRemove()) {

            String time = new Date(System.currentTimeMillis()).toString();

            String entity = entityToRemove.getType().toString();

            String location = Methods.getBetterLocationString(entityToRemove.getLocation());

            Logging.addToLogWriteQueue(new FarmLimiterLog(new String[] {time, entity, location}));

        }

    }

}
