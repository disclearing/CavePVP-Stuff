package net.frozenorb.foxtrot.gameplay.loot.shop;

import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public class ShopHandler {

    @Getter private Map<Material, Double> sellShop = new HashMap<>();
    @Getter private Map<ItemStack, Double> specificBuyShop = new HashMap<>();

    public ShopHandler(Foxtrot instance) {
        sellShop.put(Material.EMERALD_BLOCK, 50.0);
        sellShop.put(Material.DIAMOND_BLOCK, 50.0);
        sellShop.put(Material.GOLD_BLOCK, 31.25);
        sellShop.put(Material.IRON_BLOCK, 31.25);
        sellShop.put(Material.REDSTONE_BLOCK, 15.62);
        sellShop.put(Material.LAPIS_BLOCK, 15.62);

        specificBuyShop.put(new ItemStack(Material.ENDER_PORTAL_FRAME, 1, (byte) 92), 2500.0);
        specificBuyShop.put(InventoryUtils.CROWBAR.clone(), 10000.0);
        if (instance.getMapHandler().isKitMap()) {
            specificBuyShop.put(new ItemStack(Material.GOLDEN_APPLE, 1), 7500.0);
            specificBuyShop.put(new ItemStack(Material.GOLDEN_APPLE, 1, (byte)1), 2000.0);
            specificBuyShop.put(new Potion(PotionType.INVISIBILITY, 1).toItemStack(1), 2500.0);
            specificBuyShop.put(new Potion(PotionType.POISON, 1).splash().toItemStack(1), 500.0);
            specificBuyShop.put(new Potion(PotionType.SLOWNESS, 1).splash().toItemStack(1), 500.0);
            specificBuyShop.put(new Potion(PotionType.FIRE_RESISTANCE, 1).extend().toItemStack(1), 250.0);
            specificBuyShop.put(new ItemStack(Material.FISHING_ROD, 1), 500.0);
            specificBuyShop.put(new ItemStack(Material.MILK_BUCKET, 1), 500.0);
            specificBuyShop.put(new ItemStack(Material.BREWING_STAND_ITEM, 1), 500.0);
        } else {
            specificBuyShop.put(new ItemStack(Material.MONSTER_EGG, 1, (byte) 92), 1000.0);
            specificBuyShop.put(new ItemStack(Material.SPIDER_EYE, 16), 750.0);
            specificBuyShop.put(new ItemStack(Material.SUGAR_CANE, 16), 750.0);
            specificBuyShop.put(new ItemStack(Material.MELON, 16), 500.0);
            specificBuyShop.put(new ItemStack(Material.BLAZE_ROD, 8), 1250.0);
            specificBuyShop.put(new ItemStack(Material.FERMENTED_SPIDER_EYE, 16), 1000.0);
            specificBuyShop.put(new ItemStack(Material.STRING, 16), 750.0);
            specificBuyShop.put(new ItemStack(Material.NETHER_STALK, 16), 1250.0);
            specificBuyShop.put(new ItemStack(Material.CARROT_ITEM, 16), 750.0);
            specificBuyShop.put(new ItemStack(Material.SLIME_BALL, 16), 750.0);
            specificBuyShop.put(new ItemStack(Material.POTATO_ITEM, 16), 750.0);
            specificBuyShop.put(ItemBuilder.of(Material.NAME_TAG).name(ChatColor.translate("&6&lSelect Totem Effect"))
                    .addToLore("", ChatColor.GRAY + "Tier: " + ChatColor.WHITE + 1, "", ChatColor.GREEN + "Click to select Totem Effect.").build(), 1000.0);
        }
    }

}
