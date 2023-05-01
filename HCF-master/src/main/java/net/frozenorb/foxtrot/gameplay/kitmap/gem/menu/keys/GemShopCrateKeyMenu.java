package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.keys;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.blockshop.menu.BackButton;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.GemShopMenu;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GemShopCrateKeyMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GREEN + CC.BOLD + "Keys";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            buttons.put(i, new GlassButton(i % 2 == 0 ? 5 : 13));
        }

        buttons.put(26, new BackButton((player1, i, clickType) -> new GemShopMenu().openMenu(player)));

        buttons.put(11, new CommandButton(
                ItemBuilder.of(Material.INK_SACK)
                        .name(CC.GREEN + CC.BOLD + "Armory Key")
                        .addToLore(CC.GRAY + "Right click a key chest to obtain rewards!")
                        .addToLore("")
                        .addToLore(CC.GOLD + "Cost: " + CC.GREEN + "30 Gems")
                        .enchant(Enchantment.DURABILITY, 1)
                        .data((short) 10)
                        .build(),
                30,
                "cr givekey {player} Armory 1"
        ));

        buttons.put(12, new CommandButton(
                ItemBuilder.of(Material.INK_SACK)
                        .name(CC.GOLD + CC.BOLD + "Weaponry Key")
                        .addToLore(CC.GRAY + "Right click a key chest to obtain rewards!")
                        .addToLore("")
                        .addToLore(CC.GOLD + "Cost: " + CC.GREEN + "35 Gems")
                        .enchant(Enchantment.DURABILITY, 1)
                        .data((short) 11)
                        .build(),
                35,
                "cr givekey {player} Weaponry 1"
        ));

        buttons.put(13, new CommandButton(
                ItemBuilder.of(Material.ENDER_CHEST)
                        .name(CC.PINK + CC.BOLD + "Partner Package")
                        .addToLore(CC.GRAY + "Right click a key chest to obtain rewards!")
                        .addToLore("")
                        .addToLore(CC.GOLD + "Cost: " + CC.GREEN + "20 Gems")
                        .data((short) 10)
                        .build(),
                20,
                "pp {player} 1"
        ));

        buttons.put(14, new CommandButton(
                ItemBuilder.of(Material.INK_SACK)
                        .name(CC.PINK + CC.BOLD + "Ability Key")
                        .addToLore(CC.GRAY + "Right click a key chest to obtain rewards!")
                        .addToLore("")
                        .addToLore(CC.GOLD + "Cost: " + CC.GREEN + "20 Gems")
                        .enchant(Enchantment.DURABILITY, 1)
                        .data((short) 9)
                        .build(),
                20,
                "cr givekey {player} Ability 1"
        ));

        buttons.put(15, new CommandButton(
                ItemBuilder.of(Material.INK_SACK)
                        .name(CC.DARK_RED + CC.BOLD + "CE Key")
                        .addToLore(CC.GRAY + "Right click a key chest to obtain rewards!")
                        .addToLore("")
                        .addToLore(CC.GOLD + "Cost: " + CC.GREEN + "15 Gems")
                        .enchant(Enchantment.DURABILITY, 1)
                        .data((short) 1)
                        .build(),
                15,
                "cr givekey {player} CE 1"
        ));

        return buttons;
    }

    @AllArgsConstructor
    private static class CommandButton extends Button {

        private final ItemStack itemStack;
        private final int cost;
        private final String command;

        @Override
        public ItemStack getButtonItem(Player player) {
            return itemStack;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(CC.RED + "You do not have enough inventory space for this!");
                return;
            }

            if (Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), cost)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
            } else {
                player.sendMessage(CC.RED + "You do not have enough gems for this!");
            }
        }

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }
    }

    @RequiredArgsConstructor
    private static class GlassButton extends Button {

        private final int glassData;

        @Override
        public ItemStack getButtonItem(Player player) {
            return ItemBuilder.of(Material.STAINED_GLASS_PANE)
                    .name(" ")
                    .data((short) glassData)
                    .build();
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
