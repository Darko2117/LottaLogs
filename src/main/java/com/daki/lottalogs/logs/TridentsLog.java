package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;

public class TridentsLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Trident", "Location", "Action", "Target"};

    public TridentsLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {

        if (!event.getEntity().getType().equals(EntityType.TRIDENT))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player;
        if (event.getEntity().getShooter() instanceof Player) {
            player = ((Player) event.getEntity().getShooter()).getName();
        } else {
            player = event.getEntity().getShooter().toString();
        }

        String trident = ((Trident) event.getEntity()).getItem().toString();

        String location = Methods.getBetterLocationString(event.getEntity().getLocation());

        String action = "THROW";

        String target = "";

        Logging.addToLogWriteQueue(new TridentsLog(new String[] {time, player, trident, location, action, target}));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupArrowEvent(PlayerPickupArrowEvent event) {

        if (!event.getArrow().getType().equals(EntityType.TRIDENT))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String trident = ((ThrowableProjectile) event.getArrow()).getItem().toString();

        String location = Methods.getBetterLocationString(event.getArrow().getLocation());

        String action = "PICKUP";

        String target = "";

        Logging.addToLogWriteQueue(new TridentsLog(new String[] {time, player, trident, location, action, target}));

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityEvent1(EntityDamageByEntityEvent event) {

        if (!event.getDamager().getType().equals(EntityType.TRIDENT))
            return;

        String time = new Date(System.currentTimeMillis()).toString();

        String player;
        if (((Trident) event.getDamager()).getShooter() instanceof Player) {
            player = ((Player) ((Trident) event.getDamager()).getShooter()).getName();
        } else {
            player = ((Trident) event.getDamager()).getShooter().toString();
        }

        String trident = ((Trident) event.getDamager()).getItem().toString();

        String location = Methods.getBetterLocationString(event.getEntity().getLocation());

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

        Logging.addToLogWriteQueue(new TridentsLog(new String[] {time, player, trident, location, action, target}));

    }

}
