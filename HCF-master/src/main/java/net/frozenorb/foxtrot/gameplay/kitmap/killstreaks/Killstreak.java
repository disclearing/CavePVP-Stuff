package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.List;

public abstract class Killstreak {

    public abstract String getName();

    public abstract int[] getKills();
    public abstract Material getMaterial();
    public abstract List<String> getDescription();

    public abstract void apply(Player player);

    public void apply(Player player, int kills) {

    }

    public boolean check(Player player, int kills) {
        if (shouldApply(kills)) {
            apply(player);
            apply(player, kills);
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldApply(int kills) {
        for (int k : getKills()) {
            if (k == kills) {
                return true;
            }
        }

        return false;
    }

    public static void give(Player player, ItemStack item) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);

            if (current == null || current.getType() == Material.AIR) {
                player.getInventory().setItem(i, item);
                return;
            }
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);

            if (current != null && current.getType() == Material.POTION && current.getDurability() != 0) {
                Potion potion = Potion.fromItemStack(current);

                if (potion.getType() == PotionType.INSTANT_HEAL && potion.isSplash()) {
                    player.getInventory().setItem(i, item);
                    return;
                }
            }
        }
    }

}
