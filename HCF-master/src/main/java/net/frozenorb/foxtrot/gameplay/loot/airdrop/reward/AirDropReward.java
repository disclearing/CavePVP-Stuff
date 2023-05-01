package net.frozenorb.foxtrot.gameplay.loot.airdrop.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class AirDropReward {
    @Getter private ItemStack itemStack;
    @Getter private double chance;
    @Getter private String command;
    @Getter private boolean grantItem;
}
