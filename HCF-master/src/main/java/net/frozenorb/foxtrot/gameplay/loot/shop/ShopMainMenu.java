package net.frozenorb.foxtrot.gameplay.loot.shop;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnerShopMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopMainMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Main Menu";
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(10, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&a&lBuy Shop");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                    toReturn.add(ChatColor.GRAY + "Purchase various potions and");
                    toReturn.add(ChatColor.GRAY + "other items to assist you in Combat!");
                } else {
                    toReturn.add(ChatColor.GRAY + "Purchase various potion materials");
                    toReturn.add(ChatColor.GRAY + "and other items to assist you in playing!");
                }
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to open the Buy Shop");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                    return Material.POTION;
                }

                return Material.NETHER_STALK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ShopMenu(true).openMenu(player);
            }
        });


        toReturn.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&c&lSell Shop");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Sell your valuables for money");
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to open the Sell Shop");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new ShopMenu(false).openMenu(player);
            }
        });


        toReturn.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lBlock Shop");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Purchase blocks to make your base nicer!");
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to open the Block Shop");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WOOD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.chat("/blockshop");
            }
        });

        toReturn.put(16, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.translate("&4&lSpawner Shop");
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Purchase spawners and grind mobs for XP!");
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to open the Spawner Shop");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.MOB_SPAWNER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new SpawnerShopMenu().openMenu(player);
            }
        });


        return toReturn;
    }
}
