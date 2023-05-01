package net.frozenorb.foxtrot.gameplay.loot.airdrop.tracker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class AirdropTracker {
    @Getter private UUID trackingID;
    @Getter private UUID givenTo;
    @Getter private UUID givenBy;
    @Getter private int initalAmount;
    @Getter private ItemStack itemStack;
    @Getter private boolean airdropAll;
    @Getter private Map<String, Long> actions;
}
