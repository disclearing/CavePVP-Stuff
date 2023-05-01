package net.frozenorb.foxtrot.nametag.modsuite;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.util.PotionUtil;
import net.frozenorb.foxtrot.util.RomanUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventorySeeHandler implements Listener {

    private static final ItemStack POTION_EFFECTS_ITEM = ItemBuilder.of(Material.BREWING_STAND_ITEM)
            .name(ChatColor.GREEN + "Potion Effects")
            .build();

    private static final ItemStack EDIT_INVENTORY_ITEM = ItemBuilder.of(Material.CHEST)
            .name(ChatColor.GREEN + "Edit Inventory")
            .build();

    private static final ItemStack AIR = new ItemStack(Material.AIR);

    public void openInventory(Player player, Player target) {
        new InventorySeeMenu(target).openMenu(player);
    }

    private static class InventorySeeMenu extends Menu {

        private final Player target;

        public InventorySeeMenu(Player target) {
            this.target = target;

            setAutoUpdate(true);
        }

        @Override
        public String getTitle(Player player) {
            return target.getName() + "'s Inventory";
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> buttonMap = new HashMap<>();

            ItemStack[] contents = target.getInventory().getContents();
            ItemStack[] armor = target.getInventory().getArmorContents();

            for (int i = 0; i < contents.length; i++) {
                buttonMap.put(i, new ItemButton(contents[i] == null ? AIR : contents[i].clone()));
            }

            for (int i = 0; i < armor.length; i++) {
                buttonMap.put(5 * 9 - 4 + i, new ItemButton(armor[i] == null ? AIR : armor[i].clone()));
            }

            buttonMap.put(4 * 9, new PotionEffectButton(target));

            double health = Math.round(target.getHealth() * 10) / 10d;
            double foodLevel = Math.round(target.getFoodLevel() * 10) / 10d;

            buttonMap.put(4 * 9 + 1, new ItemButton(
                    ItemBuilder.of(Material.SPECKLED_MELON)
                            .name(ChatColor.RED + "Health: " + ChatColor.WHITE + health + "/" + (int) target.getMaxHealth())
                            .amount((int) Math.max(1, Math.round(health)))
                            .build()
            ));

            buttonMap.put(4 * 9 + 2, new ItemButton(
                    ItemBuilder.of(Material.COOKED_BEEF)
                            .name(ChatColor.GOLD + "Food Level: " + ChatColor.WHITE + foodLevel + "/20")
                            .amount((int) Math.max(1, Math.round(foodLevel)))
                            .build()
            ));

            if (player.hasPermission("command.invadmin.edit")) {
                buttonMap.put(4 * 9 + 3, new EditInventoryButton(target));
            }

            return buttonMap;
        }
    }

    @AllArgsConstructor
    private static final class ItemButton extends Button {

        private final ItemStack itemStack;

        @Override
        public ItemStack getButtonItem(Player player) {
            return itemStack;
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
    }

    @AllArgsConstructor
    private static final class PotionEffectButton extends Button {

        private final Player target;

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder potion = ItemBuilder.copyOf(POTION_EFFECTS_ITEM.clone());

            if (target.getActivePotionEffects().isEmpty()) {
                potion.addToLore(ChatColor.GREEN + "No active potion effects.");
            } else {
                for (PotionEffect effect : target.getActivePotionEffects()) {
                    String name = PotionUtil.getFancyName(effect.getType());
                    String amplifier = RomanUtil.toRoman(effect.getAmplifier() + 1);
                    String duration = effect.getDuration() > 20 * 60 * 60 * 24 // 24 hours
                            ? "Infinite"
                            : TimeUtils.formatIntoMMSS(effect.getDuration() / 20);
                    potion.addToLore(name + " " + amplifier + " (" + duration + ")");
                }
            }

            return potion.build();
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
    }

    @AllArgsConstructor
    private static final class EditInventoryButton extends Button {

        private final Player target;

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.openInventory(target.getInventory());
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return EDIT_INVENTORY_ITEM.clone();
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
    }
}
