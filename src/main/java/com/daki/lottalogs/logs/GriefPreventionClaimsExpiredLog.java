package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;
import me.ryanhamshire.GriefPrevention.events.ClaimExpirationEvent;

public class GriefPreventionClaimsExpiredLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "LowestY", "Area"};

    public GriefPreventionClaimsExpiredLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimExpirationEvent(ClaimExpirationEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getClaim().getOwnerName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Methods.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Methods.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        Logging.addToLogWriteQueue(new GriefPreventionClaimsExpiredLog(new String[] {time, player, lowestY, area}));

    }

}
