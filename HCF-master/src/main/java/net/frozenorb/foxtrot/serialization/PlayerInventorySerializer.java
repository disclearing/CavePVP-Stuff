package net.frozenorb.foxtrot.serialization;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerInventorySerializer {

    public static String serialize(final Player player) {
        return Foxtrot.GSON.toJson(new PlayerInventoryWrapper(player));
    }

    public static PlayerInventoryWrapper deserialize(final String json) {
        return Foxtrot.GSON.fromJson(json,PlayerInventoryWrapper.class);
    }

    public static BasicDBObject getInsertableObject(final Player player) {
        return (BasicDBObject) JSON.parse(serialize(player));
    }

    public static class PlayerInventoryWrapper {
        private PotionEffect[] effects;
        private ItemStack[] contents;
        private ItemStack[] armor;
        private int health;
        private int hunger;

        public PlayerInventoryWrapper(final Player player) {
            this.contents = player.getInventory().getContents();
            for (int i = 0; i < this.contents.length; ++i) {
                ItemStack stack = this.contents[i];
                if (stack == null) {
                    this.contents[i] = new ItemStack(Material.AIR, 0, (short) 0);
                }
            }
            this.armor = player.getInventory().getArmorContents();
            for (int i = 0; i < this.armor.length; ++i) {
                ItemStack stack = this.armor[i];
                if (stack == null) {
                    this.armor[i] = new ItemStack(Material.AIR, 0, (short) 0);
                }
            }
            this.effects = player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]);
            this.health = (int) player.getHealth();
            this.hunger = player.getFoodLevel();
        }

        public void apply(final Player player) {
            player.getInventory().setContents(this.contents);
            player.getInventory().setArmorContents(this.armor);
            for (final PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            for (final PotionEffect effect2 : this.effects) {
                player.addPotionEffect(effect2);
            }
        }

        public PotionEffect[] getEffects() {
            return this.effects;
        }

        public ItemStack[] getContents() {
            return this.contents;
        }

        public ItemStack[] getArmor() {
            return this.armor;
        }

        public int getHealth() {
            return this.health;
        }

        public int getHunger() {
            return this.hunger;
        }
    }
}
