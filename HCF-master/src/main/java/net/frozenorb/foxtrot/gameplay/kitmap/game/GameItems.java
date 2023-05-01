package net.frozenorb.foxtrot.gameplay.kitmap.game;

import cc.fyre.proton.util.ItemBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class GameItems {

    public static ItemStack LEAVE_EVENT = ItemBuilder.of(Material.INK_SACK).data(DyeColor.RED.getDyeData()).name(ChatColor.RED.toString() + ChatColor.BOLD + "Leave Event").build();
    public static ItemStack VOTE_FOR_ARENA = ItemBuilder.of(Material.PAPER).name(ChatColor.GRAY.toString() + "» " + ChatColor.GOLD + ChatColor.BOLD + "%MAP%" + ChatColor.GRAY + " «").build();

}
