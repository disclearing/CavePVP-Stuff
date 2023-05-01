package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.suge.kit.data.Kit;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class KitSetContentsCommand {

    @Command(names = {"kit setcontents"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "kit") Kit kit) {
        kit.setItems(Arrays.stream(player.getInventory().getContents().clone()).filter(Objects::nonNull).collect(Collectors.toList()));
        kit.setArmor(Arrays.stream(player.getInventory().getArmorContents().clone()).filter(Objects::nonNull).collect(Collectors.toList()));

        player.sendMessage(ChatColor.GREEN + "Set the contents of " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + ".");
    }

    @Command(names = {"kit load", "kit loadcontents"}, permission = "op")
    public static void load(Player player, @Parameter(name = "kit") Kit kit) {
        player.getInventory().clear();
        kit.apply(player);

        player.sendMessage(ChatColor.GREEN + "Applied the contents of kit " + ChatColor.WHITE + kit.getName() + ChatColor.GREEN + ".");
    }

}
