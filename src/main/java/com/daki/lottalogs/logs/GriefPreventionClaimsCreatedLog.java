package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;

public class GriefPreventionClaimsCreatedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "LowestY", "Area"};

    public GriefPreventionClaimsCreatedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimCreatedEvent(ClaimCreatedEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getCreator().getName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Methods.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Methods.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        Logging.addToLogWriteQueue(new GriefPreventionClaimsCreatedLog(new String[] {time, player, lowestY, area}));

    }

}
