package net.frozenorb.foxtrot.server.voucher.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class VoucherMainMenu extends Menu {

    private UUID target;

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.GOLD_BLOCK).name(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Pending Vouchers" + CC.translate(" &6(&f" + (int) Foxtrot.getInstance().getVoucherHandler().getCache().stream().filter(it -> !it.isUsed() && it.getTarget().toString().equals(target.toString())).count()) + "&6)").build();
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
                new VoucherPendingMenu(target).openMenu(player);
            }
        });

        toReturn.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.EMERALD_BLOCK).name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Used Vouchers" + CC.translate(" &6(&f" + (int) Foxtrot.getInstance().getVoucherHandler().getCache().stream().filter(it -> it.isUsed() && it.getTarget().toString().equals(target.toString())).count()) + "&6)").build();
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
                new VoucherUsedMenu(target).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public String getTitle(Player player) {
        return Proton.getInstance().getUuidCache().name(target) + "'s Vouchers";
    }
}
