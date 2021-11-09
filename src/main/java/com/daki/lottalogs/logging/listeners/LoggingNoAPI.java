package com.daki.lottalogs.logging.listeners;

import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.logging.Logging;
import com.daki.lottalogs.logging.logs.CommandsWithLocationLog;
import com.daki.lottalogs.logging.logs.DroppedItemsLog;
import com.daki.lottalogs.logging.logs.DroppedItemsOnDeathLog;
import com.daki.lottalogs.logging.logs.EggsThrownLog;
import com.daki.lottalogs.logging.logs.ItemsBrokenLog;
import com.daki.lottalogs.logging.logs.ItemsDespawnedLog;
import com.daki.lottalogs.logging.logs.ItemsDestroyedLog;
import com.daki.lottalogs.logging.logs.ItemsPlacedInItemFramesLog;
import com.daki.lottalogs.logging.logs.ItemsTakenOutOfItemFramesLog;
import com.daki.lottalogs.logging.logs.LightningStrikesLog;
import com.daki.lottalogs.logging.logs.MCMMORepairUseLog;
import com.daki.lottalogs.logging.logs.MinecartsDestroyedLog;
import com.daki.lottalogs.logging.logs.PickedUpItemsLog;
import com.daki.lottalogs.logging.logs.PlayerLocationLog;
import com.daki.lottalogs.logging.logs.TridentsLog;
import com.daki.lottalogs.logging.logs.UIClicksLog;
import com.daki.lottalogs.other.APIs;
import com.daki.lottalogs.other.BukkitTaskCache;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;

public class LoggingNoAPI implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerEggThrowEvent(PlayerEggThrowEvent event) {

        if (!Logging.getCachedLogFromName("EggsThrownLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getPlayer().getName();

        String location = Logging.getBetterLocationString(event.getEgg().getLocation());

        String claimOwner = "";
        if (APIs.GriefPreventionFound) {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getEgg().getLocation(), true, null);
            if (claim != null) claimOwner = claim.getOwnerName();
        }

        EggsThrownLog log = new EggsThrownLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(location);
        log.addArgumentValue(claimOwner);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {

        if (!Logging.getCachedLogFromName("DroppedItemsLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getPlayer().getName();

        String item = event.getItemDrop().getItemStack().toString();

        String location = Logging.getBetterLocationString(event.getItemDrop().getLocation());

        DroppedItemsLog log = new DroppedItemsLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

        if (!Logging.getCachedLogFromName("ItemsPlacedInItemFramesLog").isEnabled()) return;

        if (!(event.getRightClicked() instanceof ItemFrame)) return;
        if (!((ItemFrame) event.getRightClicked()).getItem().getType().equals(Material.AIR)) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getPlayer().getName();

        String item = event.getPlayer().getInventory().getItemInMainHand().toString();

        String location = Logging.getBetterLocationString(event.getRightClicked().getLocation());

        ItemsPlacedInItemFramesLog log = new ItemsPlacedInItemFramesLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        if (!Logging.getCachedLogFromName("ItemsTakenOutOfItemFramesLog").isEnabled()) return;

        if (!(event.getEntity() instanceof ItemFrame)) return;
        if (((ItemFrame) event.getEntity()).getItem().getType().equals(Material.AIR)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getDamager().getName();

        String item = ((ItemFrame) event.getEntity()).getItem().toString();

        String location = Logging.getBetterLocationString(event.getEntity().getLocation());

        ItemsTakenOutOfItemFramesLog log = new ItemsTakenOutOfItemFramesLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (!Logging.getCachedLogFromName("MCMMORepairUseLog").isEnabled()) return;

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!event.getClickedBlock().getType().equals(Material.IRON_BLOCK)) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getPlayer().getName();

        String item = event.getPlayer().getInventory().getItemInMainHand().toString();

        MCMMORepairUseLog log = new MCMMORepairUseLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(item);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityPickupItem(EntityPickupItemEvent event) {

        if (!Logging.getCachedLogFromName("PickedUpItemsLog").isEnabled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getEntity().getName();

        String item = event.getItem().getItemStack().toString();

        String location = Logging.getBetterLocationString(event.getItem().getLocation());

        PickedUpItemsLog log = new PickedUpItemsLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (!Logging.getCachedLogFromName("UIClicksLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getWhoClicked().getName();

        String inventoryName = event.getView().getTitle();

        String clickedItem = "";
        if (event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR))
            clickedItem = event.getCurrentItem().toString();

        String location = Logging.getBetterLocationString(event.getWhoClicked().getLocation());

        UIClicksLog log = new UIClicksLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(inventoryName);
        log.addArgumentValue(clickedItem);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemBreakEvent(PlayerItemBreakEvent event) {

        if (!Logging.getCachedLogFromName("ItemsBrokenLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getPlayer().getName();

        String item = event.getBrokenItem().toString();

        String location = Logging.getBetterLocationString(event.getPlayer().getLocation());

        ItemsBrokenLog log = new ItemsBrokenLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawnEvent(ItemDespawnEvent event) {

        if (!Logging.getCachedLogFromName("ItemsDespawnedLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String item = event.getEntity().getItemStack().toString();

        String location = Logging.getBetterLocationString(event.getLocation());

        ItemsDespawnedLog log = new ItemsDespawnedLog();
        log.addArgumentValue(time);
        log.addArgumentValue(item);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageEvent(EntityDamageEvent event) {

        if (!Logging.getCachedLogFromName("ItemsDestroyedLog").isEnabled()) return;

        if (!(event.getEntity() instanceof Item)) return;
        if (((Item) event.getEntity()).getItemStack().getType().equals(Material.CACTUS)) return;
        if (((Item) event.getEntity()).getItemStack().getType().equals(Material.COBBLESTONE)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String item = ((Item) event.getEntity()).getItemStack().toString();

        String location = Logging.getBetterLocationString(event.getEntity().getLocation());

        String cause = event.getCause().toString();

        ItemsDestroyedLog log = new ItemsDestroyedLog();
        log.addArgumentValue(time);
        log.addArgumentValue(item);
        log.addArgumentValue(location);
        log.addArgumentValue(cause);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {

        if (!Logging.getCachedLogFromName("CommandsWithLocationLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getPlayer().getName();

        String command = event.getMessage();

        String location = Logging.getBetterLocationString(event.getPlayer().getLocation());

        CommandsWithLocationLog log = new CommandsWithLocationLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(command);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        if (!Logging.getCachedLogFromName("DroppedItemsOnDeathLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String user = event.getEntity().getName();

        String killer = "Not a player";
        try {
            killer = event.getEntity().getKiller().getName();
        } catch (Throwable ignored) {
        }

        String items = "";
        for (ItemStack item : event.getDrops()) {
            if (!items.isEmpty()) {
                items = items.concat(", ");
            }
            items = items.concat(item.toString());
        }

        String location = Logging.getBetterLocationString(event.getEntity().getLocation());

        DroppedItemsOnDeathLog log = new DroppedItemsOnDeathLog();
        log.addArgumentValue(time);
        log.addArgumentValue(user);
        log.addArgumentValue(killer);
        log.addArgumentValue(items);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVehicleDestroyEvent(VehicleDestroyEvent event) {

        if (!Logging.getCachedLogFromName("MinecartsDestroyedLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String attacker = "Not a player";
        try {
            attacker = event.getAttacker().getName();
        } catch (Throwable ignored) {
        }

        String location = Logging.getBetterLocationString(event.getVehicle().getLocation());

        String claimOwner = "";
        if (APIs.GriefPreventionFound) {
            Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getVehicle().getLocation(), true, null);
            if (claim != null) claimOwner = claim.getOwnerName();
        }

        MinecartsDestroyedLog log = new MinecartsDestroyedLog();
        log.addArgumentValue(time);
        log.addArgumentValue(attacker);
        log.addArgumentValue(location);
        log.addArgumentValue(claimOwner);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLightningStrikeEvent(LightningStrikeEvent event) {

        if (!Logging.getCachedLogFromName("LightningStrikesLog").isEnabled()) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String location = Logging.getBetterLocationString(event.getLightning().getLocation());

        String cause = event.getCause().toString();

        LightningStrikesLog log = new LightningStrikesLog();
        log.addArgumentValue(time);
        log.addArgumentValue(cause);
        log.addArgumentValue(location);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {

        if (!Logging.getCachedLogFromName("TridentsLog").isEnabled()) return;

        if (!event.getEntity().getType().equals(EntityType.TRIDENT)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player;
        if (event.getEntity().getShooter() instanceof Player) {
            player = ((Player) event.getEntity().getShooter()).getName();
        } else {
            player = event.getEntity().getShooter().toString();
        }

        String trident = ((Trident) event.getEntity()).getItem().toString();

        String location = Logging.getBetterLocationString(event.getEntity().getLocation());

        String action = "THROW";

        String target = "";

        TridentsLog log = new TridentsLog();
        log.addArgumentValue(time);
        log.addArgumentValue(player);
        log.addArgumentValue(trident);
        log.addArgumentValue(location);
        log.addArgumentValue(action);
        log.addArgumentValue(target);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupArrowEvent(PlayerPickupArrowEvent event) {

        if (!Logging.getCachedLogFromName("TridentsLog").isEnabled()) return;

        if (!event.getArrow().getType().equals(EntityType.TRIDENT)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String trident = event.getItem().getItemStack().toString();

        String location = Logging.getBetterLocationString(event.getArrow().getLocation());

        String action = "PICKUP";

        String target = "";

        TridentsLog log = new TridentsLog();
        log.addArgumentValue(time);
        log.addArgumentValue(player);
        log.addArgumentValue(trident);
        log.addArgumentValue(location);
        log.addArgumentValue(action);
        log.addArgumentValue(target);

        Logging.addToLogWriteQueue(log);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityEvent1(EntityDamageByEntityEvent event) {

        if (!Logging.getCachedLogFromName("TridentsLog").isEnabled()) return;

        if (!event.getDamager().getType().equals(EntityType.TRIDENT)) return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player;
        if (((Trident) event.getDamager()).getShooter() instanceof Player) {
            player = ((Player) ((Trident) event.getDamager()).getShooter()).getName();
        } else {
            player = ((Trident) event.getDamager()).getShooter().toString();
        }

        String trident = ((Trident) event.getDamager()).getItem().toString();

        String location = Logging.getBetterLocationString(event.getEntity().getLocation());

        String action = "HIT";

        String target;
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().getCustomName() != null) {
                target = event.getEntity().getCustomName();
            } else {
                target = event.getEntity().getName();
            }
        } else {
            target = event.getEntity().getType().toString();
        }

        TridentsLog log = new TridentsLog();
        log.addArgumentValue(time);
        log.addArgumentValue(player);
        log.addArgumentValue(trident);
        log.addArgumentValue(location);
        log.addArgumentValue(action);
        log.addArgumentValue(target);

        Logging.addToLogWriteQueue(log);

    }

    public static void startPlayerLocationLog() {
        BukkitTaskCache.addTask(new BukkitRunnable() {
            @Override
            public void run() {

                if (!Logging.getCachedLogFromName("PlayerLocationLog").isEnabled()) return;

                for (Player player : Bukkit.getOnlinePlayers()) {

                    String time = new Date(System.currentTimeMillis()).toString();

                    String playerName = player.getName();

                    String location = Logging.getBetterLocationString(player.getLocation());

                    PlayerLocationLog log = new PlayerLocationLog();
                    log.addArgumentValue(time);
                    log.addArgumentValue(playerName);
                    log.addArgumentValue(location);

                    Logging.addToLogWriteQueue(log);

                }

            }
        }.runTaskTimer(LottaLogs.getInstance(), 1, 200));
    }

}