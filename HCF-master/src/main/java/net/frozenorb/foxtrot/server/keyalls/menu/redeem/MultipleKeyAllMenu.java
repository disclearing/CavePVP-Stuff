package net.frozenorb.foxtrot.server.keyalls.menu.redeem;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MultipleKeyAllMenu extends PaginatedMenu {
    private List<KeyAll> keyAlls;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Key-All";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (KeyAll keyAll : keyAlls) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(keyAll.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.translate("&4&l❙ &fItems: &c" + keyAll.getItems().size()));
                    toReturn.add(ChatColor.translate("&4&l❙ &fRedeemed: &c" + keyAll.getRedeemed().size()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to redeem the key-all items.");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {

                    if (keyAll.getItems().isEmpty()) {
                        return Material.TRIPWIRE_HOOK;
                    }

                    return keyAll.getItems().get(0).getType();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();

                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(ChatColor.RED + "Your inventory is full!");
                        return;
                    }

                    for (ItemStack itemStack : keyAll.getItems()) {

                        if (itemStack == null) {
                            continue;
                        }

                        if (player.getInventory().firstEmpty() == -1) {
                            player.getWorld().dropItemNaturally(player.getLocation(), itemStack.clone());
                            continue;
                        }

                        player.getInventory().addItem(itemStack.clone());
                    }

                    keyAll.getRedeemed().add(player.getUniqueId());

                    player.updateInventory();
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    player.sendMessage(ChatColor.translate("&aYou have redeemed the " + keyAll.getDisplayName() + "&a!"));
                }
            });
        }

        return toReturn;
    }
}
