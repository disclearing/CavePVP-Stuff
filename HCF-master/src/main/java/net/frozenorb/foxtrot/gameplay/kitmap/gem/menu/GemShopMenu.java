package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.keys.GemShopCrateKeyMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades.GemShopUpgradesMenu;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GemShopMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GREEN + CC.BOLD + "Gem Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            buttons.put(i, new GlassButton(i % 2 == 0 ? 5 : 13));
        }

        buttons.put(11, new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.TRIPWIRE_HOOK)
                        .name(CC.RED + CC.BOLD + "Crate Keys")
                        .addToLore(CC.GRAY + "Click here to purchase crate keys.")
//                        .data((short) 1)
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new GemShopCrateKeyMenu().openMenu(player);
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
        });

        buttons.put(15, new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.BOOK)
                        .name(CC.AQUA + CC.BOLD + "Kit Upgrades")
                        .addToLore(CC.GRAY + "Click here to purchase kit upgrades.")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new GemShopUpgradesMenu().openMenu(player);
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
        });

        return buttons;
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
