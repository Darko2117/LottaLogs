package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;

public class UIClicksLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "InventoryName", "Item", "Location"};

    public UIClicksLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickEvent(InventoryClickEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getWhoClicked().getName();

        String inventoryName = event.getView().getTitle();

        String item = "";
        if (event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR))
            item = event.getCurrentItem().toString();

        String location = Methods.getBetterLocationString(event.getWhoClicked().getLocation());

        Logging.addToLogWriteQueue(new UIClicksLog(new String[] {time, player, inventoryName, item, location}));

    }

}
