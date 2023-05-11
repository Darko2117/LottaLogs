package com.daki.lottalogs.logs;

import java.util.Date;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.daki.lottalogs.Logging;

public class CrazyCratesCratePrizesLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Items", "Commands", "Crate"};

    public CrazyCratesCratePrizesLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPrizeEvent(PlayerPrizeEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        StringBuilder itemsStringBuilder = new StringBuilder();
        List<ItemBuilder> itemsItemBuilder = event.getPrize().getItemBuilders();
        if (itemsItemBuilder.size() != 0) {
            for (ItemBuilder item : itemsItemBuilder) {

                String amount = item.getAmount().toString();
                String itemMaterial = item.getMaterial().toString();
                String itemDisplayName = item.getName();

                if (!itemsStringBuilder.toString().isEmpty())
                    itemsStringBuilder.append(", ");

                itemsStringBuilder.append(amount).append("X").append(" ").append(itemMaterial).append(" (").append(itemDisplayName).append(")");

            }
        }
        if (!event.getPrize().getItems().isEmpty()) {
            for (ItemStack item : event.getPrize().getItems()) {

                String amount = String.valueOf(item.getAmount());
                String itemMaterial = item.getType().toString();
                String itemDisplayName = item.getItemMeta().getDisplayName();

                if (!itemsStringBuilder.toString().isEmpty())
                    itemsStringBuilder.append(", ");

                itemsStringBuilder.append(amount).append("X").append(" ").append(itemMaterial).append(" (").append(itemDisplayName).append(")");

            }
        }

        String items = itemsStringBuilder.toString();

        String commands;
        StringBuilder commandsStringBuilder = new StringBuilder();
        List<String> commandsList = event.getPrize().getCommands();
        if (commandsList.size() != 0) {
            for (String command : commandsList) {

                if (!commandsStringBuilder.toString().isEmpty())
                    commandsStringBuilder.append(", ");

                commandsStringBuilder.append(command);

            }
        }
        commands = commandsStringBuilder.toString();

        String crate = event.getCrateName();

        Logging.addToLogWriteQueue(new CrazyCratesCratePrizesLog(new String[] {time, player, items, commands, crate}));

    }

}
