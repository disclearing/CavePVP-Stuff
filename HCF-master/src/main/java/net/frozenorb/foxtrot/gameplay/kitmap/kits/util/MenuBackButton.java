package net.frozenorb.foxtrot.gameplay.kitmap.kits.util;

import net.minecraft.util.com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import cc.fyre.proton.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MenuBackButton extends Button {

    private final Consumer<Player> openPreviousMenuConsumer;

    @Override
    public String getName(Player player) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.RED + "Click here to return to",
            ChatColor.RED + "the previous menu."
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.REDSTONE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        openPreviousMenuConsumer.accept(player);
    }

}