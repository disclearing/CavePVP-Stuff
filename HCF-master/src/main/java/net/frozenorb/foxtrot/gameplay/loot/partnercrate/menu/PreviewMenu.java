package net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu;

import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewMenu extends Menu {

    private PartnerType partnerType;
    private ItemStack placeholder;

    public PreviewMenu(PartnerType type) {
        this.partnerType = type;
        this.placeholder = ItemBuilder.of(Material.STAINED_GLASS_PANE)
                .name(" ")
                .data(ColorUtil.COLOR_MAP.getOrDefault(this.partnerType.getChatColor(), DyeColor.WHITE).getWoolData())
                .build();

    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(0, Button.fromItem(placeholder));
        toReturn.put(1, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 0, ""));
        toReturn.put(2, Button.fromItem(new ItemStack(ItemBuilder.of(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 4).name(this.partnerType.getChatColor() + ChatColor.BOLD.toString() + this.partnerType.getCrateName() + ChatColor.GRAY + " ┃" + ChatColor.WHITE + " Helmet").addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Implants V", ChatColor.RED + "Mermaid III", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build())));
        toReturn.put(3, Button.fromItem(new ItemStack(ItemBuilder.of(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 4).name(this.partnerType.getChatColor() + ChatColor.BOLD.toString() + this.partnerType.getCrateName() + ChatColor.GRAY + " ┃" + ChatColor.WHITE + " Chestplate").addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build())));
        toReturn.put(4, Button.fromItem(new ItemStack(ItemBuilder.of(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 4).name(this.partnerType.getChatColor() + ChatColor.BOLD.toString() + this.partnerType.getCrateName() + ChatColor.GRAY + " ┃" + ChatColor.WHITE + " Leggings").addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build())));
        toReturn.put(5, Button.fromItem(new ItemStack(ItemBuilder.of(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.PROTECTION_FALL, 4).name(this.partnerType.getChatColor() + ChatColor.BOLD.toString() + this.partnerType.getCrateName() + ChatColor.GRAY + " ┃" + ChatColor.WHITE + " Boots").addToLore(ChatColor.RED + "Speed II", ChatColor.RED + "HellForged IV", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build())));
        toReturn.put(6, Button.fromItem(new ItemStack(ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.LOOT_BONUS_MOBS, 5).name(this.partnerType.getChatColor() + ChatColor.BOLD.toString() + this.partnerType.getCrateName() + ChatColor.GRAY + " ┃" + ChatColor.WHITE + " Sword").build())));
        toReturn.put(7, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 0, ""));
        toReturn.put(8, Button.fromItem(placeholder));
        toReturn.put(9, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 0, ""));
        toReturn.put(10, Button.fromItem(new ItemStack(Material.GOLDEN_APPLE, 32)));
        toReturn.put(11, Button.fromItem(new ItemStack(Material.GOLDEN_APPLE, 1, (byte)1)));
        toReturn.put(12, Button.fromItem(new ItemStack(Material.ENDER_PORTAL_FRAME, 3)));
        toReturn.put(13, Button.fromItem(new ItemStack(ItemBuilder.of(Material.TRIPWIRE_HOOK).amount(3).name(ChatColor.translate("&5&lItems &7Key")).addToLore("", ChatColor.GRAY + "Right click the " + ChatColor.DARK_PURPLE + "Items Crate" + ChatColor.GRAY + " to obtain rewards!").build())));
        toReturn.put(14, Button.fromItem(new ItemStack(ItemBuilder.of(Material.ENDER_CHEST).name(ChatColor.translate("&d&lEaster Lootbox")).addToLore(ChatColor.GRAY + "Unlocked at " + ChatColor.WHITE + "store.cavepvp.org").build())));
        toReturn.put(15, Button.fromItem(new ItemStack(ItemBuilder.copyOf(partnerType.getAbility().hassanStack.clone()).amount(3).build())));
        toReturn.put(16, Button.fromItem(new ItemStack(ItemBuilder.copyOf(partnerType.getAbility().hassanStack.clone()).amount(6).build())));
        toReturn.put(17, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 0, ""));
        toReturn.put(18, Button.fromItem(placeholder));
        toReturn.put(19, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 0, ""));
        toReturn.put(20, Button.fromItem(new ItemStack(Material.EMERALD_BLOCK, 32)));
        toReturn.put(21, Button.fromItem(new ItemStack(Material.DIAMOND_BLOCK, 32)));
        toReturn.put(22, Button.fromItem(new ItemStack(ItemBuilder.of(Material.SKULL_ITEM).data((byte)3).name(ChatColor.translate(this.partnerType.getChatColor() + "&l" + this.partnerType.getCrateName() + " Head")).build())));
        toReturn.put(23, Button.fromItem(new ItemStack(Material.GOLD_BLOCK, 32)));
        toReturn.put(24, Button.fromItem(new ItemStack(Material.IRON_BLOCK, 32)));
        toReturn.put(25, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 0, ""));

        toReturn.put(26, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.REDSTONE).name(ChatColor.RED + ChatColor.BOLD.toString() + "Go Back")
                        .setLore(Collections.singletonList(ChatColor.GRAY + "Click here to return back to the main menu.")).build();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
                new PartnerCrateMenu().openMenu(player);
            }
        });
        
        return toReturn;
    }

    @Override
    public String getTitle(Player player) {
        return partnerType.getChatColor() + ChatColor.BOLD.toString() + partnerType.getCrateName() + ChatColor.GRAY + " Crate";
    }
}
