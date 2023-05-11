package com.daki.lottalogs.logs;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import com.daki.lottalogs.Logging;
import com.daki.lottalogs.Methods;

public class ChatWithLocationLog extends Log implements Listener {

    private static final String[] argumentKeys = new String[] {"Time", "Player", "Message", "Location"};

    public ChatWithLocationLog(String[] argumentValues) {

        super(argumentKeys, argumentValues);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {

        String time = new Date(System.currentTimeMillis()).toString();

        String player = event.getPlayer().getName();

        String message = event.getMessage();

        String location = Methods.getBetterLocationString(event.getPlayer().getLocation());

        Logging.addToLogWriteQueue(new ChatWithLocationLog(new String[] {time, player, message, location}));

    }

}
