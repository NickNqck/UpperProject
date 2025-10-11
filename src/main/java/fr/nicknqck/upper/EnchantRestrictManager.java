package fr.nicknqck.upper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class EnchantRestrictManager implements Listener {

    @EventHandler
    public void onPrepareAnvilEvent(InventoryClickEvent event) {

        if (!event.getInventory().getType().equals(InventoryType.ANVIL)) return;
        if (event.getSlot() != 2) return;
        ItemStack current = event.getCurrentItem();

        if (current == null) return;
        if (current.getEnchantments().isEmpty()) {
            if (current.getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) current.getItemMeta();
                if (meta != null) {
                    event.setCurrentItem(checkEnchant(meta.getStoredEnchants(),
                            current));
                }
            }
        } else event.setCurrentItem(checkEnchant(current.getEnchantments(),
                current));
    }

    @EventHandler
    public void onItemEnchant(EnchantItemEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Upper.getInstance(), () -> event.getInventory().setItem(0, checkEnchant(event.getEnchantsToAdd(),
                event.getItem())));

    }


    private ItemStack checkEnchant(Map<Enchantment, Integer> enchant, ItemStack item) {

        Map<Enchantment, Integer> tempEnchant = new HashMap<>();
        ItemStack result = new ItemStack(item);

        for (Enchantment e : enchant.keySet()) {

            result.removeEnchantment(e);
            if (e.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) {

                if (item.getType().equals(Material.DIAMOND_BOOTS) ||
                        item.getType().equals(Material.DIAMOND_LEGGINGS) ||
                        item.getType().equals(Material.DIAMOND_HELMET) ||
                        item.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            2));
                } else {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            3));
                }
            } else if (e.equals(Enchantment.DAMAGE_ALL)) {
                if (item.getType().equals(Material.DIAMOND_SWORD)) {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            3));
                } else {
                    tempEnchant.put(e, Math.min(enchant.get(e),
                            Math.min(enchant.get(e), 3)));
                }
            } else if (e.equals(Enchantment.ARROW_KNOCKBACK)) {
                tempEnchant.put(e, Math.min(enchant.get(e), 1));

            } else if (e.equals(Enchantment.ARROW_DAMAGE)) {
                tempEnchant.put(e, Math.min(enchant.get(e), 2));
            } else if (e.equals(Enchantment.DEPTH_STRIDER)) {
                tempEnchant.put(e, Math.min(enchant.get(e), 3));
            } else {
                if (!isEnchantProhibited(e)){
                    tempEnchant.put(e, enchant.get(e));
                }
            }
        }

        if (!result.getType().equals(Material.ENCHANTED_BOOK) && !result.getType().equals(Material.BOOK)) {
            result.addUnsafeEnchantments(tempEnchant);
        } else {
            tempEnchant.entrySet().removeIf(enchantmentIntegerEntry -> enchantmentIntegerEntry.getValue() == 0);
            if (!tempEnchant.isEmpty()) {
                result = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
                if (meta != null) {
                    for (Enchantment e : tempEnchant.keySet())
                        meta.addStoredEnchant(e, tempEnchant.get(e), false);
                    result.setItemMeta(meta);
                }
            } else {
                result = new ItemStack(Material.BOOK);
            }
        }
        return result;
    }
    private boolean isEnchantProhibited(final Enchantment enchantment) {
        return enchantment.equals(Enchantment.ARROW_FIRE) ||
                enchantment.equals(Enchantment.ARROW_INFINITE) ||
                enchantment.equals(Enchantment.ARROW_KNOCKBACK);
    }
}