package fr.nicknqck.upper;

import fr.nicknqck.upper.utils.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

public class VillagerManager implements Listener {

    private final Villager villager;
    private final Upper plugin;

    public VillagerManager(Villager villager) {
        this.villager = villager;
        this.plugin = Upper.getInstance();
        EventUtils.registerEvents(this);
        new VillagerRunnable(this).runTaskTimerAsynchronously(plugin, 20, 60);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onDamage(final EntityDamageEvent event) {
        if (this.villager == null)return;
        if (!this.villager.getUniqueId().equals(event.getEntity().getUniqueId()))return;
        event.setDamage(0.0);
        event.setCancelled(true);
    }
    /**
     * Quand un joueur clique un villageois, on va figer/forcer les trades à l’ouverture.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onInventoryClose(final InventoryCloseEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.MERCHANT))return;
        if (!villager.getWorld().equals(event.getPlayer().getWorld()))return;
        double distance = villager.getLocation().distance(event.getPlayer().getLocation());
        System.out.println("distance between "+event.getPlayer().getName()+" and the villager is "+distance+", and they are inside the world: "+event.getPlayer().getWorld().getName());
        if (distance > 4)return;
        for (final Entity entity : event.getPlayer().getWorld().getEntities()) {
            if (!(entity instanceof Villager))continue;//on fume tous les villageois
            entity.remove();
        }
        EventUtils.unregisterEvents(this);
        this.plugin.createNewVillager(event.getPlayer().getWorld());
    }

    private static class VillagerRunnable extends BukkitRunnable {

        private final VillagerManager villagerManager;
        private final Location location;

        private VillagerRunnable(VillagerManager villagerManager) {
            this.villagerManager = villagerManager;
            this.location = villagerManager.villager.getLocation();
        }

        @Override
        public void run() {
            if (this.villagerManager.villager.isDead()) {
                System.out.println("cancel");
                cancel();
                return;
            }
            if (this.villagerManager.villager.getLocation().distance(this.location) > 1) {
                System.out.println("tp villager");
                Bukkit.getScheduler().runTask(Upper.getInstance(), () -> this.villagerManager.villager.teleport(this.location));
            }
        }
    }
}