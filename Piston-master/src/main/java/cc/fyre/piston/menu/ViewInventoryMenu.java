package cc.fyre.piston.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class ViewInventoryMenu extends Menu {

    private static final int[] ARMOR_SLOTS = new int[]{18,27,36,45};
    private static final int[] BEDROCK_SLOTS = new int[]{
            9,10,11,12,13,14,15,16,17,
            19,28,37,46
    };
    private static final int[] INVENTORY_SLOTS = new int[]{
            20,21,22,23,24,25,26,
            29,30,31,32,33,34,35,
            38,39,40,41,42,43,44,
            47,48,49,50,51,52,53
    };

    @Getter private Player target;

    @Override
    public int size(Player player) {
        return 6*9;
    }

    @Override
    public String getTitle(Player player) {
        return this.target.getDisplayName();
    }

    @Override
    public boolean isAutoUpdate() {
        return this.target != null;
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        final ItemStack[] armor = this.target.getInventory().getArmorContents();
        final ItemStack[] inventory = this.target.getInventory().getContents();

        int slot = 0;

        for (ItemStack item : inventory) {

            if (toReturn.size() > 8) {
                break;
            }

            if (item != null && item.getType() != Material.AIR) {
                toReturn.put(slot,new ItemButton(item));
            }

            slot++;
        }

        int armorSlot = 0;

        for (ItemStack armorPiece : this.reverse(armor)) {

            if (armorSlot > ARMOR_SLOTS.length) {
                continue;
            }

            if (armorPiece != null && armorPiece.getType() != Material.AIR) {

                final ItemButton button = new ItemButton(armorPiece);

                if (!armorPiece.getEnchantments().isEmpty()) {
                    armorPiece.getEnchantments().forEach((key,value) -> button.itemStack.addUnsafeEnchantment(key,value));
                }

                toReturn.put(ARMOR_SLOTS[armorSlot],button);
            }

            armorSlot++;
        }


        int itemCount = 0;
        int inventorySlot = 0;

        final List<ItemStack> fixedInventory = new ArrayList<>();

        for (ItemStack itemStack : inventory) {

            if (itemCount > 8) {
                fixedInventory.add(itemStack);
            }

            itemCount++;
        }

        for (ItemStack inventoryItem : fixedInventory) {

            if (inventoryItem != null && inventoryItem.getType() != Material.AIR) {

                final ItemButton button = new ItemButton(inventoryItem);

                if (!inventoryItem.getEnchantments().isEmpty()) {
                    inventoryItem.getEnchantments().forEach((key,value) -> button.itemStack.addUnsafeEnchantment(key,value));
                }

                toReturn.put(INVENTORY_SLOTS[inventorySlot],button);
            }

            inventorySlot++;
        }

        for (int bedrockSlot : BEDROCK_SLOTS) {

            toReturn.put(bedrockSlot,new Button() {

                @Override
                public String getName(Player player) {
                    return " ";
                }

                @Override
                public List<String> getDescription(Player player) {
                    return new ArrayList<>();
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.BEDROCK;
                }

            });

        }

        return toReturn;
    }

    @AllArgsConstructor
    class ItemButton extends Button {

        @Getter private ItemStack itemStack;

        @Override
        public String getName(Player player) {
            return ChatColor.WHITE + ItemUtils.getName(this.itemStack);
        }

        @Override
        public List<String> getDescription(Player player) {
            return this.itemStack.getItemMeta().hasLore() ? this.itemStack.getItemMeta().getLore():new ArrayList<>();
        }

        @Override
        public Material getMaterial(Player player) {
            return this.itemStack.getType();
        }

        @Override
        public byte getDamageValue(Player player) {
            return this.itemStack.getData().getData();
        }

        @Override
        public int getAmount(Player player) {
            return this.itemStack.getAmount();
        }

    }

    private ItemStack[] reverse(ItemStack[] array) {

        final List<ItemStack> toReturn = Arrays.asList(array);

        Collections.reverse(toReturn);

        return toReturn.toArray(new ItemStack[array.length]);
    }
}
