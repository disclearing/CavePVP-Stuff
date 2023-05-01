package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button;

import net.minecraft.util.com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.ItemUtils;
import cc.fyre.proton.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
final class SaveButton extends Button {

    private final Kit kit;

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Save";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.GRAY + "Click to " + ChatColor.GREEN + "save" + ChatColor.GRAY + " your kit!"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return DyeColor.LIME.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        kit.setInventoryContents(player.getInventory().getContents());

        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.closeInventory();

        InventoryUtils.resetInventoryDelayed(player);

        new KitsMenu().openMenu(player);

        ItemStack[] defaultInventory = kit.getOriginal().getInventoryContents();

        int foodInDefault = ItemUtils.countStacksMatching(defaultInventory, v -> v.getType().isEdible());
        int pearlsInDefault = ItemUtils.countStacksMatching(defaultInventory, v -> v.getType() == Material.ENDER_PEARL);

        if (foodInDefault > 0 && kit.countFood() == 0) {
            player.sendMessage(ChatColor.RED + "Your saved kit is missing food.");
        }

        if (pearlsInDefault > 0 && kit.countPearls() == 0) {
            player.sendMessage(ChatColor.RED + "Your saved kit is missing enderpearls.");
        }
    }

}