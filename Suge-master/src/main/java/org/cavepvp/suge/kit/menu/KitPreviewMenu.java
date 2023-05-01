package org.cavepvp.suge.kit.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.coinshop.CoinShop;
import org.cavepvp.suge.kit.data.Kit;
import org.cavepvp.suge.kit.menu.kitmap.KitAllMenu;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class KitPreviewMenu extends Menu {
    private Kit kit;
    private Menu originalMenu;

    @Override
    public String getTitle(Player player) {
        return "Previewing " + kit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (ItemStack item : kit.getItems()) {
            toReturn.put(toReturn.size(), Button.fromItem(item.clone()));
        }

        for (int i = 36; i < 46; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));
        }

        int armorSlot = 46;

        final List<ItemStack> armor = kit.getArmor();

        for (ItemStack itemStack : armor) {
            toReturn.put(armorSlot, Button.fromItem(itemStack.clone()));
            armorSlot++;
        }

        int newSlot = 50;

        toReturn.put(newSlot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));
        newSlot++;
        toReturn.put(newSlot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));
        newSlot++;
        toReturn.put(newSlot, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Go Back";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Collections.singletonList(ChatColor.GRAY + "Click to return to the main kit menu.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new KitCategoryMenu(kit.getCategory()).openMenu(player);
            }
        });
        newSlot++;
        toReturn.put(newSlot, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));

        return toReturn;
    }

    @Override
    public void onClose(Player player) {
        CoinShop.getInstance().getServer().getScheduler().runTaskLater(CoinShop.getInstance(), () -> originalMenu.openMenu(player), 1);
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}