package fr.nicknqck.upper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PapierEnchantManager implements Listener {

    public PapierEnchantManager() {
        start();
    }

    /**
     * Lance la tâche répétée qui gère les coeurs bonus
     */
    public void start() {
        Bukkit.getScheduler().runTaskTimer(Upper.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePlayerHealth(player);
            }
        }, 0L, 20L); // toutes les secondes
    }
    /**
     * Vérifie si le joueur possède le papier custom
     */
    private boolean hasCustomPaper(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() != Material.PAPER) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName() && "§dPapier enchantée".equals(meta.getDisplayName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Met à jour la vie max du joueur en fonction de l'item
     */
    private void updatePlayerHealth(Player player) {
        if (hasCustomPaper(player)) {
            double BONUS_HP = 24.0;
            if (player.getMaxHealth() != BONUS_HP) {
                player.setMaxHealth(BONUS_HP);
                if (player.getHealth() > BONUS_HP) {
                    player.setHealth(BONUS_HP);
                }
            }
        } else {
            double BASE_HP = 20.0;
            if (player.getMaxHealth() != BASE_HP) {
                player.setMaxHealth(BASE_HP);
                if (player.getHealth() > BASE_HP) {
                    player.setHealth(BASE_HP);
                }
            }
        }
    }

}
