package fr.nicknqck.upper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class MOTDChanger implements Listener {

    @EventHandler
    private void serverPingEvent(final ServerListPingEvent event) {
        event.setMotd("§cFallenKingdom§7, custom by§b @NickNqck");
        event.setMaxPlayers(6);
    }
}
