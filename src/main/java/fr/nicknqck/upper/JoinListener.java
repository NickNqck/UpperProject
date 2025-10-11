package fr.nicknqck.upper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Objects;

public class JoinListener implements Listener {

    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        sendTabTitle(event.getPlayer(), "\n§cFallenKingdom\n","\n§bPlugin by @NickNqck");
    }

    private void sendTabTitle(Player player, String header, String footer) {
        if (header == null)
            header = "";
        header = ChatColor.translateAlternateColorCodes('&', header);
        if (footer == null)
            footer = "";
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        try {
            Object tabHeader = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + header + "\"}");
            Object tabFooter = Objects.requireNonNull(getNMSClass("IChatBaseComponent")).getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, "{\"text\":\"" + footer + "\"}");
            Constructor<?> titleConstructor = Objects.requireNonNull(getNMSClass("PacketPlayOutPlayerListHeaderFooter")).getConstructor();
            Object packet = titleConstructor.newInstance();
            Field aField = packet.getClass().getDeclaredField("a");
            aField.setAccessible(true);
            aField.set(packet, tabHeader);
            Field bField = packet.getClass().getDeclaredField("b");
            bField.setAccessible(true);
            bField.set(packet, tabFooter);
            sendPacket(player, packet);
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
    }
    private Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
