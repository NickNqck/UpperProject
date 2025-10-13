package fr.nicknqck.upper;

import fr.nicknqck.upper.arena.ArenaCommand;
import fr.nicknqck.upper.utils.EventUtils;
import fr.nicknqck.upper.utils.NMSItemUtils;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class Upper extends JavaPlugin {

    @Getter
    private static Upper instance;


    @Override
    public void onEnable() {
        instance = this;
        final World world = Bukkit.getWorlds().get(0);
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player)continue;
            if (!(entity instanceof LivingEntity))continue;
            entity.remove();
        }
        createNewVillager(world);
        EventUtils.registerEvents(new EnchantRestrictManager());
        EventUtils.registerEvents(new NoCraftNotchApple());
        EventUtils.registerEvents(new PapierEnchantManager());
        EventUtils.registerEvents(new MOTDChanger());
        EventUtils.registerEvents(new JoinListener());
        PluginCommand command = getCommand("arena");
        command.setExecutor(new ArenaCommand());
    }

    @Override
    public void onDisable() {
    }
    /**
     * Applique une MerchantRecipeList customisée à un villageois.
     * @param villager Le villageois ciblé
     * @param trades La liste des échanges à lui appliquer
     */
    public void applyTrades(@NonNull Villager villager, @NonNull MerchantRecipeList trades) {
        if (!(villager instanceof CraftVillager)) return;

        EntityVillager nmsVillager = ((CraftVillager) villager).getHandle();
        try {
            Field field = EntityVillager.class.getDeclaredField("br");
            field.setAccessible(true);
            field.set(nmsVillager, trades);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void createNewVillager(@NonNull final World world) {
        final Villager villager = (Villager) world.spawnEntity(new Location(world, 12.7, 82.0, -25.565, 89, 2), EntityType.VILLAGER);
        villager.setProfession(Villager.Profession.BLACKSMITH);
        villager.setMaxHealth(8000);
        villager.setHealth(villager.getMaxHealth());
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setCanPickupItems(false);
        villager.setCustomNameVisible(true);
        villager.setCustomName("§aMarchand des glaces");
        MerchantRecipeList recipes = new MerchantRecipeList();
        ItemStack porc = NMSItemUtils.createNMSItem(Material.PORK, 8);
        ItemStack cuir = NMSItemUtils.createNMSItem(Material.LEATHER, 1);
        ItemStack emeraude4 = NMSItemUtils.createNMSItem(Material.EMERALD, 4);
        ItemStack tnt = NMSItemUtils.createNMSItem(Material.TNT, 1);
        ItemStack fer4 = NMSItemUtils.createNMSItem(Material.IRON_INGOT, 4);
        ItemStack gold1 = NMSItemUtils.createNMSItem(Material.GOLD_INGOT, 1);
        ItemStack gold4 = NMSItemUtils.createNMSItem(Material.GOLD_INGOT, 4);
        ItemStack diamond1 = NMSItemUtils.createNMSItem(Material.DIAMOND, 1);
        ItemStack graine2 = NMSItemUtils.createNMSItem(Material.SEEDS, 2);
        ItemStack ble1 = NMSItemUtils.createNMSItem(Material.WHEAT, 1);
        ItemStack potato1 = NMSItemUtils.createNMSItem(Material.POTATO_ITEM, 2);
        ItemStack pomme1 = NMSItemUtils.createNMSItem(Material.APPLE, 1);
        ItemStack ble4 = NMSItemUtils.createNMSItem(Material.WHEAT, 4);
        ItemStack potion = NMSItemUtils.createPotion(PotionType.SPEED, false, false, false);
        org.bukkit.inventory.ItemStack classicPaper = new org.bukkit.inventory.ItemStack(Material.PAPER);
        ItemMeta itemMeta = classicPaper.getItemMeta();
        itemMeta.setDisplayName("§dPapier enchantée");
        itemMeta.setLore(Arrays.asList("§7§oLe joueur possédant cette objet dans son inventaire","§7§oaura un§c bonus§7§o de§c 2❤ supplémentaires§7."));
        itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.spigot().setUnbreakable(true);
        classicPaper.setItemMeta(itemMeta);
        ItemStack paper = NMSItemUtils.fromBukkit(classicPaper);
        ItemStack dragonEgg = NMSItemUtils.createNMSItem(Material.DRAGON_EGG, 1);

        recipes.add(new MerchantRecipe(porc, null, cuir, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(emeraude4, null, tnt, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(fer4, null, gold1, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(gold4, null, diamond1, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(graine2, ble1, potato1, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(ble4, null, pomme1, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(emeraude4, null, potion, 0, Integer.MAX_VALUE));
        recipes.add(new MerchantRecipe(dragonEgg, null, paper, 0, Integer.MAX_VALUE));

        applyTrades(villager, recipes);
        villager.getEyeLocation().setPitch(90f);
        villager.getEyeLocation().setYaw(1.5f);
        new VillagerManager(villager);
    }
}