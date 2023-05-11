package com.daki.lottalogs.logs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.LottaLogs;
import com.daki.lottalogs.Methods;
import de.Keyle.MyPet.MyPetApi;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.entity.MyPet;
import de.Keyle.MyPet.api.player.MyPetPlayer;
import de.Keyle.MyPet.api.util.inventory.CustomInventory;
import de.Keyle.MyPet.skill.skills.BackpackImpl;

public class DroppedItemsOnDeathLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "DeathMessage", "Item", "Location"};

    public DroppedItemsOnDeathLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getEntity().getName();

        String deathMessage = event.getDeathMessage();

        List<String> items = new ArrayList<>();
        for (ItemStack item : event.getDrops()) {
            items.add(item.toString());
        }
        if (items.isEmpty()) {
            items.add("-");
        }

        String location = Methods.getBetterLocationString(event.getEntity().getLocation());

        for (String item : items) {

            Logging.addToLogWriteQueue(new DroppedItemsOnDeathLog(new String[] {time, player, deathMessage, item, location}));

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeathEvent1(PlayerDeathEvent event) {

        MyPetPlayer myPetPlayer = MyPetApi.getPlayerManager().getMyPetPlayer(event.getEntity());

        if (myPetPlayer == null || !myPetPlayer.hasMyPet())
            return;

        MyPet myPet = myPetPlayer.getMyPet();

        if (!myPetPlayer.getMyPet().getStatus().equals(MyPet.PetState.Here))
            return;

        if (!Configuration.Skilltree.Skill.Backpack.DROP_WHEN_OWNER_DIES)
            return;

        if (!myPet.getSkills().isActive(BackpackImpl.class))
            return;

        CustomInventory customInventory = myPet.getSkills().get(BackpackImpl.class).getInventory();

        List<String> items = new ArrayList<>();
        for (ItemStack item : customInventory.getBukkitInventory().getContents()) {
            if (item == null)
                continue;
            items.add(item.toString());
        }
        if (items.isEmpty()) {
            items.add("-");
        }

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getEntity().getName() + "'s Mypet " + myPet.getPetName();

        String deathMessage = event.getDeathMessage();

        String location = Methods.getBetterLocationString(event.getEntity().getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {

                for (String item : items) {

                    Logging.addToLogWriteQueue(new DroppedItemsOnDeathLog(new String[] {time, player, deathMessage, item, location}));

                }
            }
        }.runTaskLater(LottaLogs.getInstance(), 1);

    }

}
