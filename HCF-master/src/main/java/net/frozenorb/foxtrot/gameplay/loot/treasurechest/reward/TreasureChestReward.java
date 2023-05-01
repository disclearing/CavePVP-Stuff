package net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class TreasureChestReward {
    @Getter private ItemStack itemStack;
    @Getter @Setter private double chance;
    @Getter private String command;
    @Getter private boolean grantItem;
    @Getter private boolean broadcast;
}
