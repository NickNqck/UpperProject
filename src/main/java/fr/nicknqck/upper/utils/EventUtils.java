package fr.nicknqck.upper.utils;

import fr.nicknqck.upper.Upper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Getter
public class EventUtils implements Listener{

    public EventUtils() {
        registerEvents(this);
    }

    public static void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Upper.getInstance());
    }

    public static void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}