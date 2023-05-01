package net.frozenorb.foxtrot.gameplay.loot.crate.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class CrateItem {
    @Getter
    private ItemStack itemStack;
    @Getter
    private double chance;
    @Getter
    private String command;
    @Getter
    private boolean giveItem;
    @Getter
    private boolean broadcast;
}
