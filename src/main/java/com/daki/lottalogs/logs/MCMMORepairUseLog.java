package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import com.daki.lottalogs.Logging;

public class MCMMORepairUseLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Item"};

    public MCMMORepairUseLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if (!event.getHand().equals(EquipmentSlot.HAND))
            return;

        if (!event.getClickedBlock().getType().equals(Material.IRON_BLOCK))
            return;

        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String item = event.getPlayer().getInventory().getItemInMainHand().toString();

        Logging.addToLogWriteQueue(new MCMMORepairUseLog(new String[] {time, player, item}));

    }

}
