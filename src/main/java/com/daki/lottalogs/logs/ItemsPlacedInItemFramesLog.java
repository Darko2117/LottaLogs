package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class ItemsPlacedInItemFramesLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item", "Location"};

    public ItemsPlacedInItemFramesLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

        if (!(event.getRightClicked() instanceof ItemFrame))
            return;

        if (!((ItemFrame) event.getRightClicked()).getItem().getType().equals(Material.AIR))
            return;

        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String item = event.getPlayer().getInventory().getItemInMainHand().toString();

        String location = Methods.getBetterLocationString(event.getRightClicked().getLocation());

        Logging.addToLogWriteQueue(new ItemsPlacedInItemFramesLog(new String[] {time, player, item, location}));

    }

}
