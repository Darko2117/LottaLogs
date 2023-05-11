package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;
import me.ryanhamshire.GriefPrevention.events.ClaimResizeEvent;

public class GriefPreventionClaimsResizedLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "LowestY", "Area"};

    public GriefPreventionClaimsResizedLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimResizeEvent(ClaimResizeEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getTo().getOwnerName();

        String lowestY = String.valueOf(event.getTo().getLesserBoundaryCorner().getBlockY());

        String firstCorner = Methods.getBetterLocationString(event.getTo().getLesserBoundaryCorner());
        String secondCorner = Methods.getBetterLocationString(event.getTo().getGreaterBoundaryCorner());
        String area = firstCorner.concat(" - ").concat(secondCorner);

        Logging.addToLogWriteQueue(new GriefPreventionClaimsResizedLog(new String[] {time, player, lowestY, area}));

    }

}
