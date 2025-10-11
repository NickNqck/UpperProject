package fr.nicknqck.upper;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class NoCraftNotchApple implements Listener {

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;

        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;

        // Vérifie si le résultat est une pomme de Notch
        if (result.getType() == Material.GOLDEN_APPLE && result.getDurability() == 1) {
            // Annule le craft en mettant le résultat à "AIR"
            event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

}
