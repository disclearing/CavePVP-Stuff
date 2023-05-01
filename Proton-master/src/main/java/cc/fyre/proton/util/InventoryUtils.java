package cc.fyre.proton.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {

    public static boolean fits(ItemStack item,Inventory target) {

        int leftToAdd = item.getAmount();

        if (target.getMaxStackSize() == 2147483647) {
            return true;
        } else {

            final ItemStack[] contents = target.getContents();

            for (int i = 0; i < contents.length; i++) {

                final ItemStack itemStack = contents[i];

                if (leftToAdd <= 0) {
                    return true;
                }

                if (itemStack != null && itemStack.getType() != Material.AIR) {

                    if (itemStack.isSimilar(item)) {
                        leftToAdd -= item.getMaxStackSize() - itemStack.getAmount();
                    }

                } else {
                    leftToAdd -= item.getMaxStackSize();
                }
            }


            return leftToAdd <= 0;
        }
    }

}
