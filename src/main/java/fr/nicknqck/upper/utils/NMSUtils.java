package fr.nicknqck.upper.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class NMSUtils {

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendActionBar(Player player, String message) {
        try{
            Object messageICB = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[]{String.class}).invoke(null, "{\"text\":\"" + message + "\"}");
            Constructor<?> actionBarConstructor = getNMSClass("PacketPlayOutChat").getConstructor();
            Object packet = actionBarConstructor.newInstance();

            Field a = packet.getClass().getDeclaredField("a");
            a.setAccessible(true);
            a.set(packet, messageICB);

            Field b = packet.getClass().getDeclaredField("b");
            b.setAccessible(true);
            b.set(packet, (byte) 2);

            sendPacket(player, packet);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
