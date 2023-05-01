package net.frozenorb.foxtrot.gameplay.boosters.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class DoublePoints extends Booster {
    @Override
    public String getId() {
        return "2xPoints";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "2x Points";
    }

    @Override
    public int getSlot() {
        return 15;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("All points gained (excl Citadel)", "are doubled for the next hour!");
    }

    @Override
    public ItemStack getItemDisplay() {
        return ItemBuilder.of(Material.EMERALD).name(this.getDisplayName() + ChatColor.GRAY + " [1 hour]")
                .addToLore(ChatColor.translate("&7Doubles all points gained"), ChatColor.translate("&7for the next hour!"))
                .build();
    }
}
