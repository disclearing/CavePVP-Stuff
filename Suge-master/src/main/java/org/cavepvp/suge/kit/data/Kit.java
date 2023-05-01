package org.cavepvp.suge.kit.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.event.KitUseEvent;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    @Getter @Setter private String name;
    @Getter @Setter private int slot;

    @Getter @Setter private String displayName;
    @Getter @Setter private Material material;
    @Getter @Setter private int damage;
    @Getter @Setter private List<String> lore;
    @Getter @Setter private List<ItemStack> armor;
    @Getter @Setter private List<ItemStack> items;
    @Getter @Setter private Category category = Category.NONE;
    @Getter @Setter private long cooldown;

    public boolean equip(Player player) {

        final KitUseEvent kitUseEvent = new KitUseEvent(player, this);

        Suge.getInstance().getServer().getPluginManager().callEvent(kitUseEvent);

        if (kitUseEvent.isCancelled()) {
            return false;
        }

        Suge.getInstance().getKitHandler().getKitUses().putIfAbsent(this, 0);
        Suge.getInstance().getKitHandler().getKitUses().replace(this, Suge.getInstance().getKitHandler().getKitUses().get(this)+1);

        if (!player.isOp()) {
            Suge.getInstance().getKitHandler().setCooldown(player, this);
        }

        this.apply(player);
        return true;
    }

    public void apply(Player player) {

        for (ItemStack itemStack : this.armor) {

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType().name().contains("HELMET") && player.getInventory().getHelmet() == null) {
                player.getInventory().setHelmet(itemStack.clone());
            } else if (itemStack.getType().name().contains("CHESTPLATE") && player.getInventory().getChestplate() == null) {
                player.getInventory().setChestplate(itemStack.clone());
            } else if (itemStack.getType().name().contains("LEGGINGS") && player.getInventory().getLeggings() == null) {
                player.getInventory().setLeggings(itemStack.clone());
            } else if (itemStack.getType().name().contains("BOOTS") && player.getInventory().getBoots() == null) {
                player.getInventory().setBoots(itemStack.clone());
            } else {

                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack.clone());
                    continue;
                }

                player.getInventory().addItem(itemStack.clone());
            }

        }

        final List<ItemStack> newList = new ArrayList<>();

        this.items.forEach(it -> newList.add(it.clone()));

        for (ItemStack itemStack : newList) {

            if (itemStack == null) {
                continue;
            }

            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack.clone());
                continue;
            }

            player.getInventory().addItem(itemStack.clone());
        }
    }

}
