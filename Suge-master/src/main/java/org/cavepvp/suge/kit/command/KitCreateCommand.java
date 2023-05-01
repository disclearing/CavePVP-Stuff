package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KitCreateCommand {
    @Command(names = {"kit create"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "name")String kitName) {
        if (Suge.getInstance().getKitHandler().getKits().containsKey(kitName)) {
            player.sendMessage(ChatColor.RED + "That kit already exists!");
            return;
        }

        final Kit kit = new Kit();

        kit.setName(kitName);
        kit.setDisplayName(ChatColor.RED + kitName);
        kit.setMaterial(Material.DIAMOND_SWORD);
        kit.setCooldown(TimeUnit.HOURS.toMillis(24));
        kit.setSlot(5);
        kit.setItems(Arrays.stream(player.getInventory().getContents().clone()).filter(Objects::nonNull).map(ItemStack::clone).collect(Collectors.toList()));
        kit.setArmor(Arrays.stream(player.getInventory().getArmorContents().clone()).filter(Objects::nonNull).map(ItemStack::clone).collect(Collectors.toList()));
        kit.setLore(Arrays.asList("&7You may purchase this kit at &fstore.cavepvp.org", "", "&7Cooldown: &c1 Day", "&7Available in: &c{remaining}", "", "&7(&c!&7) Right click to preview"));

        Suge.getInstance().getKitHandler().getKits().put(kitName, kit);
        player.sendMessage(ChatColor.GREEN + "Created kit named " + ChatColor.WHITE + kitName + ".");
    }
}
