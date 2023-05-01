package cc.fyre.proton.menu;

import cc.fyre.proton.util.EnchantmentGlow;
import cc.fyre.proton.util.ItemBuilder;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public abstract class Button {

    private final String id = this.getClass().getSimpleName();

    /** @deprecated */
    @Deprecated
    public static Button placeholder(Material material, byte data, String... title) {
        return placeholder(material, data, title != null && title.length != 0 ? Joiner.on(" ").join(title) : " ");
    }

    public static Button placeholder(Material material) {
        return placeholder(material, " ");
    }

    public static Button placeholder(Material material, String title) {
        return placeholder(material,(byte)0,title);
    }

    public static Button placeholder(Material material, byte data, String title) {
        return new Button() {

            @Override
            public String getName(Player player) {
                return title;
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of();
            }

            @Override
            public Material getMaterial(Player player) {
                return material;
            }

            @Override
            public byte getDamageValue(Player player) {
                return data;
            }

        };
    }

    public static Button fromItem(ItemStack item) {

        return new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                return item;
            }

            @Override
            public String getName(Player var1) {
                return null;
            }

            @Override
            public List<String> getDescription(Player var1) {
                return null;
            }

            @Override
            public Material getMaterial(Player var1) {
                return null;
            }
        };

    }

    public abstract String getName(Player var1);

    public abstract List<String> getDescription(Player var1);

    public abstract Material getMaterial(Player var1);

    public byte getDamageValue(Player player) {
        return 0;
    }

    public boolean isGlow() {
        return false;
    }

    public String getSkullTexture(Player player) {
        return "";
    }

    public void clicked(Player player, int slot, ClickType clickType) {
    }

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public int getAmount(Player player) {
        return 1;
    }

    public ItemStack getButtonItem(Player player) {
        ItemStack buttonItem = new ItemStack(this.getMaterial(player), this.getAmount(player), (short)this.getDamageValue(player));

        if (!this.getSkullTexture(player).equals("") && this.getMaterial(player).equals(Material.SKULL_ITEM)) {
            buttonItem = ItemBuilder.copyOf(buttonItem).texture(this.getSkullTexture(player)).build();
        }

        ItemMeta meta = buttonItem.getItemMeta();

        meta.setDisplayName(this.getName(player));

        if (this.isGlow()) {
            Enchantment glow = new EnchantmentGlow(80);
            meta.addEnchant(glow,1,true);
        }

        List<String> description = this.getDescription(player);
        if (description != null) {
            meta.setLore(description);
        }

        buttonItem.setItemMeta(meta);
        return buttonItem;
    }

    public static void playFail(Player player) {
        player.playSound(player.getLocation(), Sound.DIG_GRASS, 20.0F, 0.1F);
    }

    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0F, 15.0F);
    }

    public static void playNeutral(Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 20.0F, 1.0F);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Button button = (Button) o;
        return Objects.equals(id, button.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
