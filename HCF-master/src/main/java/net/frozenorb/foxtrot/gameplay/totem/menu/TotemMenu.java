package net.frozenorb.foxtrot.gameplay.totem.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class TotemMenu extends Menu {
    private int tier;

    @Override
    public String getTitle(Player player) {
        return "Select an Effect";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(0, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&6&lStrength Totem");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to get a Strength Totem");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BLAZE_POWDER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                final ItemStack itemStack = ItemBuilder.of(Material.BEACON).name(ChatColor.translate("&6&lTotem"))
                        .addToLore("", ChatColor.GRAY + "Tier: " + ChatColor.WHITE + tier, ChatColor.GRAY + "Effect: " + ChatColor.WHITE + "Strength",
                                "",
                                ChatColor.GREEN + "Place this down to start totem.").build();

                player.getInventory().addItem(itemStack.clone());
                player.sendMessage(ChatColor.GREEN + "You have been given a strength totem");
                player.closeInventory();
            }
        });

        toReturn.put(1, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&6&lResistance Totem");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to get a Resistance Totem");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.IRON_INGOT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                final ItemStack itemStack = ItemBuilder.of(Material.BEACON).name(ChatColor.translate("&6&lTotem"))
                        .addToLore("", ChatColor.GRAY + "Tier: " + ChatColor.WHITE + tier, ChatColor.GRAY + "Effect: " + ChatColor.WHITE + "Resistance",
                                "",
                                ChatColor.GREEN + "Place this down to start totem.").build();

                player.getInventory().addItem(itemStack.clone());
                player.sendMessage(ChatColor.GREEN + "You have been given a Resistance totem");
                player.closeInventory();
            }
        });

        toReturn.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return CC.translate("&6&lRegeneration Totem");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to get a Regeneration Totem");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.GHAST_TEAR;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                final ItemStack itemStack = ItemBuilder.of(Material.BEACON).name(ChatColor.translate("&6&lTotem"))
                        .addToLore("", ChatColor.GRAY + "Tier: " + ChatColor.WHITE + tier, ChatColor.GRAY + "Effect: " + ChatColor.WHITE + "Regeneration",
                                "",
                                ChatColor.GREEN + "Place this down to start totem.").build();

                player.getInventory().addItem(itemStack.clone());
                player.sendMessage(ChatColor.GREEN + "You have been given a Regeneration totem");
                player.closeInventory();
            }
        });

        return toReturn;
    }
}
