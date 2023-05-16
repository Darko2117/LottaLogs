package com.daki.lottalogs.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class InventoryOnJoinLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item", "Location"};

    public InventoryOnJoinLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        PlayerInventory playerInventory = event.getPlayer().getInventory();
        List<String> items = new ArrayList<>();
        for (int i = 0; i <= 40; i++) {
            items.add(playerInventory.getItem(i).toString());
        }
        if (items.isEmpty()) {
            items.add("-");
        }

        String location = Methods.getBetterLocationString(event.getPlayer().getLocation());

        for (String item : items) {

            Logging.addToLogWriteQueue(new InventoryOnJoinLog(new String[] {time, player, item, location}));

        }

    }

}
