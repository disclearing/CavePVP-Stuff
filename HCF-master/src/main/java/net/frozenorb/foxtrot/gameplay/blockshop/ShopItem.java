package net.frozenorb.foxtrot.gameplay.blockshop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class ShopItem {
    private final ItemStack itemStack;
    private final int price;
    private final int index;
}
