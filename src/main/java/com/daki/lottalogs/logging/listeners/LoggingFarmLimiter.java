package com.daki.lottalogs.logging.listeners;

import com.daki.lottalogs.logging.Logging;
import com.daki.lottalogs.logging.logs.FarmLimiterLog;
import com.daki.lottalogs.other.APIs;
import me.filoghost.farmlimiter.api.FarmLimitEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Date;

public class LoggingFarmLimiter implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onFarmLimitEvent(FarmLimitEvent event) {

        if (!Logging.getCachedLogFromName("FarmLimiterLog").isEnabled()) return;

        for (Entity entityToRemove : event.getEntitiesToRemove()) {

            String time = new Date(System.currentTimeMillis()).toString();

            String entity = entityToRemove.getType().toString();

            String location = Logging.getBetterLocationString(entityToRemove.getLocation());

            String claimOwner = "";
            if (APIs.GriefPreventionFound) {
                Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entityToRemove.getLocation(), true, null);
                if (claim != null) claimOwner = claim.getOwnerName();
            }

            FarmLimiterLog log = new FarmLimiterLog();
            log.addArgumentValue(time);
            log.addArgumentValue(entity);
            log.addArgumentValue(location);
            log.addArgumentValue(claimOwner);

            Logging.addToLogWriteQueue(log);

        }

    }

}
