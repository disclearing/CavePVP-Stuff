package net.frozenorb.foxtrot.gameplay.boosters.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ReducedEnderpearlBooster extends Booster {
    @Override
    public String getId() {
        return "ReducedEnderpearl";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW + ChatColor.BOLD.toString() + "Reduced Pearl Cooldowns";
    }

    @Override
    public int getSlot() {
        return 11;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Enderpearl cooldown is now", "10 seconds for the next hour!");
    }

    @Override
    public ItemStack getItemDisplay() {
        return ItemBuilder.of(Material.ENDER_PEARL).name(this.getDisplayName() + ChatColor.GRAY + " [1 hour]")
                .addToLore(ChatColor.translate("&7Reduces all partner item"), ChatColor.translate("&7cooldowns by an hour!"))
                .build();
    }
}
