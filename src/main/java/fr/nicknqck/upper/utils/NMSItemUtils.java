package fr.nicknqck.upper.utils;

import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class NMSItemUtils {

    /**
     * Crée un ItemStack NMS directement depuis un Material Bukkit.
     *
     * @param material Le material Bukkit
     * @param amount   Quantité
     * @return ItemStack NMS
     */
    public static ItemStack createNMSItem(Material material, int amount) {
        return createNMSItem(material, amount, (short) 0);
    }

    /**
     * Crée un ItemStack NMS avec data (durabilité / variante).
     *
     * @param material Le material Bukkit
     * @param amount   Quantité
     * @param data     Valeur de data (ex : 5 pour les bûches de chêne foncé)
     * @return ItemStack NMS
     */
    @SuppressWarnings("deprecation")
    public static ItemStack createNMSItem(Material material, int amount, short data) {
        if (material == null) throw new IllegalArgumentException("Material ne peut pas être null !");
        Item item = Item.getById(material.getId());
        return new ItemStack(item, amount, data);
    }

    /**
     * Convertit un ItemStack Bukkit vers un ItemStack NMS.
     *
     * @param bukkitStack ItemStack Bukkit
     * @return ItemStack NMS
     */
    public static ItemStack fromBukkit(org.bukkit.inventory.ItemStack bukkitStack) {
        if (bukkitStack == null) return null;
        return CraftItemStack.asNMSCopy(bukkitStack);
    }

    /**
     * Convertit un ItemStack NMS vers un ItemStack Bukkit.
     *
     * @param nmsStack ItemStack NMS
     * @return ItemStack Bukkit
     */
    @SuppressWarnings("unused")
    public static org.bukkit.inventory.ItemStack toBukkit(ItemStack nmsStack) {
        if (nmsStack == null) return null;
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    /**
     * Crée une potion de manière simplifiée en NMS (1.8.8)
     *
     * @param potionType Le type de potion Bukkit (PotionType)
     * @param extended   True si la durée est allongée (ex : Force II -> Force étendue)
     * @param upgraded   True si la potion est de niveau II
     * @param splash     True si c’est une potion jetable
     * @return L’objet NMS ItemStack correspondant
     */
    public static ItemStack createPotion(PotionType potionType, boolean extended, boolean upgraded, boolean splash) {
        // On utilise le système Bukkit pour générer une potion valide
        org.bukkit.potion.Potion potion = new org.bukkit.potion.Potion(potionType);
        potion.setHasExtendedDuration(extended);
        potion.setLevel(upgraded ? 2 : 1);
        potion.setSplash(splash);

        // Conversion Bukkit -> NMS
        org.bukkit.inventory.ItemStack bukkitItem = potion.toItemStack(1);
        return CraftItemStack.asNMSCopy(bukkitItem);
    }
    public static org.bukkit.inventory.ItemStack createResistancePotion() {
        org.bukkit.inventory.ItemStack potion = new org.bukkit.inventory.ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        final List<String> effets = new ArrayList<>();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 90, 0), true);
        effets.add("§7Résistance (1:30)");
        effets.add("");
        effets.add("§5Si consommée :");
        effets.add("§9+20% de Résistance");
        meta.setDisplayName("§fPotion de résistance");
        meta.setLore(effets);
        potion.setItemMeta(meta);
        return potion;
    }
}