package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;

public class GriefPreventionClaimsDeletedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "LowestY", "Area"};

    public GriefPreventionClaimsDeletedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimDeletedEvent(ClaimDeletedEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getClaim().getOwnerName();

        String lowestY = String.valueOf(event.getClaim().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Methods.getBetterLocationString(event.getClaim().getLesserBoundaryCorner());
        String secondCorner = Methods.getBetterLocationString(event.getClaim().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        Logging.addToLogWriteQueue(new GriefPreventionClaimsDeletedLog(new String[] {time, player, lowestY, area}));

    }

}
