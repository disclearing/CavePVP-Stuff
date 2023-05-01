package net.frozenorb.foxtrot.gameplay.boosters.type;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class FrenzyBooster extends Booster {
    @Override
    public String getId() {
        return "Frenzy";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + ChatColor.BOLD.toString() + "Frenzy Event";
    }

    @Override
    public int getSlot() {
        return 13;
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList("Partner item cooldowns have been reduced", "by 15% and last 20% longer plus no ability limit!");
    }

    @Override
    public ItemStack getItemDisplay() {
        return ItemBuilder.of(Material.RAW_FISH).data((byte)2).name(this.getDisplayName() + " Event " + ChatColor.GRAY + "[1 hour]")
                .addToLore(ChatColor.translate("&7Reduces all partner item"), ChatColor.translate("&7cooldowns by 15%!"))
                .build();
    }
}
