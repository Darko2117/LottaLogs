package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.daki.lottalogs.other.Logging;
import com.daki.lottalogs.other.Methods;

public class CommandsWithLocationLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Command", "Location"};

    public CommandsWithLocationLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String command = event.getMessage();

        String location = Methods.getBetterLocationString(event.getPlayer().getLocation());

        Logging.addToLogWriteQueue(new CommandsWithLocationLog(new String[] {time, player, command, location}));

    }

}
