package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button;

import net.minecraft.util.com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.util.InventoryUtils;
import cc.fyre.proton.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
final class CancelKitEditButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Cancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                "",
                ChatColor.GRAY + "Click to " + ChatColor.RED + "abort" + ChatColor.GRAY + " editing your",
                ChatColor.GRAY + "kit and return to the kit menu!"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.RED.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        InventoryUtils.resetInventoryDelayed(player);

        new KitsMenu().openMenu(player);
    }

}