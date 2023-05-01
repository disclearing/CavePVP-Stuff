package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class EditRedeemedButton extends Button {
    private Menu menu;
    private KeyAll keyAll;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + "List of Redeemed";
    }

    @Override
    public List<String> getDescription(Player player) {
        return Arrays.asList("", ChatColor.translate("&6&l┃ &fCurrent: &e" + keyAll.getRedeemed().size()),
                "", ChatColor.GREEN + "Click to view everyone that has redeemed it");
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.COMMAND;
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new PaginatedMenu() {

            @Override
            public String getPrePaginatedTitle(Player player) {
                return "List";
            }

            @Override
            public Map<Integer, Button> getAllPagesButtons(Player player) {
                final Map<Integer, Button> toReturn = new HashMap<>();

                for (UUID uuid : keyAll.getRedeemed()) {
                    toReturn.put(toReturn.size(), new Button() {
                        @Override
                        public String getName(Player player) {
                            return UUIDUtils.name(uuid);
                        }

                        @Override
                        public List<String> getDescription(Player player) {
                            return null;
                        }

                        @Override
                        public Material getMaterial(Player player) {
                            return Material.SKULL_ITEM;
                        }

                        @Override
                        public byte getDamageValue(Player player) {
                            return (byte) 3;
                        }

                        @Override
                        public ItemStack getButtonItem(Player player) {
                            return ItemBuilder.copyOf(super.getButtonItem(player)).skull(UUIDUtils.name(uuid)).build();
                        }
                    });
                }

                return toReturn;
            }

            @Override
            public void onClose(Player player) {
                Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> getMenu().openMenu(player), 1);
            }
        }.openMenu(player);
    }
}