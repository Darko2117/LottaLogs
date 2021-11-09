package com.daki.lottalogs.logging.listeners;

import com.daki.lottalogs.logging.Logging;
import com.daki.lottalogs.logging.logs.ClaimsCreatedLog;
import com.daki.lottalogs.logging.logs.ClaimsDeletedLog;
import com.daki.lottalogs.logging.logs.ClaimsExpiredLog;
import com.daki.lottalogs.logging.logs.ClaimsModifiedLog;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimExpirationEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Date;

public class LoggingGriefPrevention implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimCreatedEvent(ClaimCreatedEvent event) {

        if (!Logging.getCachedLogFromName("ClaimsCreatedLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getCreator().getName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Logging.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Logging.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        ClaimsCreatedLog log = new ClaimsCreatedLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(lowestY);
        log.addArgumentValue(area);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimDeletedEvent(ClaimDeletedEvent event) {

        if (!Logging.getCachedLogFromName("ClaimsDeletedLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getClaim().getOwnerName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Logging.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Logging.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        ClaimsDeletedLog log = new ClaimsDeletedLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(lowestY);
        log.addArgumentValue(area);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimModifiedEvent(ClaimModifiedEvent event) {

        if (!Logging.getCachedLogFromName("ClaimsModifiedLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getClaim().getOwnerName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Logging.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Logging.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        ClaimsModifiedLog log = new ClaimsModifiedLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(lowestY);
        log.addArgumentValue(area);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimExpirationEvent(ClaimExpirationEvent event) {

        if (!Logging.getCachedLogFromName("ClaimsExpiredLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getClaim().getOwnerName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Logging.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Logging.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        ClaimsExpiredLog log = new ClaimsExpiredLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(lowestY);
        log.addArgumentValue(area);

        Logging.addToLogWriteQueue(log);

    }

}
