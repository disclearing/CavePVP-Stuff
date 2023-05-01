package net.frozenorb.foxtrot.server;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerShopMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Spawner Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < (9*3); i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        int i = 0;

        for (SpawnerType value : SpawnerType.values()) {
            toReturn.put(10 + (i*2), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName() + " Spawner";
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();
                    toReturn.add(ChatColor.GRAY + "Purchase various spawners for");
                    toReturn.add(ChatColor.GRAY + "your base off the network here!");
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fCost: &2$&a40,000"));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to purchase this Spawner");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.MOB_SPAWNER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()) < 25000) {
                        player.sendMessage(ChatColor.RED + "Insufficient funds!");
                        return;
                    }

                    Foxtrot.getInstance().getEconomyHandler().withdraw(player.getUniqueId(), 25000);

                    if (value == SpawnerType.ZOMBIE) {
                        player.getInventory().addItem(InventoryUtils.ZOMBIE_SPAWNER.clone());
                    }
                    if (value == SpawnerType.SKELETON) {
                        player.getInventory().addItem(InventoryUtils.SKELETON_SPAWNER.clone());
                    }
                    if (value == SpawnerType.SPIDER) {
                        player.getInventory().addItem(InventoryUtils.SPIDER_SPAWNER.clone());
                    }
                    if (value == SpawnerType.CAVE_SPIDER) {
                        player.getInventory().addItem(InventoryUtils.CAVE_SPIDER_SPAWNER.clone());
                    }
                }
            });
            i++;
        }

        return toReturn;
    }
}
