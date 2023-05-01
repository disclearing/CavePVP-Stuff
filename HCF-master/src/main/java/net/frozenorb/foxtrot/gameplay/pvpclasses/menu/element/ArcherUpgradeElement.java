package net.frozenorb.foxtrot.gameplay.pvpclasses.menu.element;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.pvpclasses.archer.ArcherColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author xanderume@gmail.com
 */
@AllArgsConstructor
public class ArcherUpgradeElement extends Button {

    private ArcherColor color;

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        final List<String> toReturn = new ArrayList<>();
        for (String s : color.getLore()) {
            toReturn.add(ChatColor.GRAY + s);
        }
        toReturn.add("");
        toReturn.add(ChatColor.RED + "Click to apply this archer upgrade");
        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.of(Material.LEATHER_HELMET).color(this.color.getColor()).name(this.color.getChatColor() + ChatColor.BOLD.toString() + this.color.getName()).setLore(this.getDescription(player)).build();
    }

    @Override
    public void clicked(Player player,int i,ClickType clickType) {

        final Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Archer");

        if (kit == null) {
            player.sendMessage(ChatColor.RED + "This kit is currently unavailable!");
            return;
        }

        player.getInventory().clear();

        kit.apply(player);

        final ItemStack[] contents = player.getInventory().getArmorContents();

        Arrays.stream(contents).filter(Objects::nonNull).forEach(it -> it = ItemBuilder.copyOf(it).color(this.color.getColor()).build());

        player.getInventory().setArmorContents(contents);
        player.updateInventory();
        player.closeInventory();
    }

}
