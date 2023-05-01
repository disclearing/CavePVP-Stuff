package cc.fyre.proton.util;

import cc.fyre.proton.Proton;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder copyOf(ItemBuilder builder) {
        return new ItemBuilder(builder.build());
    }

    public static ItemBuilder copyOf(ItemStack item) {
        return new ItemBuilder(item);
    }

    private ItemBuilder(Material material, int amount) {
        Preconditions.checkArgument(amount > 0, "Amount cannot be lower than 0.");
        this.item = new ItemStack(material, amount);
    }

    private ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder amount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder data(short data) {
        this.item.setDurability(data);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder unenchant(Enchantment enchantment) {
        this.item.removeEnchantment(enchantment);
        return this;
    }

    public ItemBuilder name(String displayName) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(displayName == null ? null : ChatColor.translateAlternateColorCodes('&', displayName));
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addToLore(String... parts) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta == null) {
            meta = Proton.getInstance().getServer().getItemFactory().getItemMeta(this.item.getType());
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = Lists.newArrayList();
        }

        (lore).addAll(Arrays.stream(parts).map((part) -> ChatColor.translateAlternateColorCodes('&', part)).collect(Collectors.toList()));
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(Collection<String> l) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> lore = l.stream().map((part) -> ChatColor.translateAlternateColorCodes('&', part)).collect(Collectors.toList());
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder color(Color color) {
        ItemMeta meta = this.item.getItemMeta();
        if (!(meta instanceof LeatherArmorMeta)) {
            throw new UnsupportedOperationException("Cannot set color of a non-leather armor item.");
        } else {
            ((LeatherArmorMeta)meta).setColor(color);
            this.item.setItemMeta(meta);
            return this;
        }
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = this.item.getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder skull(String owner) {
        if (!(this.item.getItemMeta() instanceof SkullMeta)) {
            return this;
        }

        final SkullMeta meta = (SkullMeta) this.item.getItemMeta();
        meta.setOwner(owner);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder texture(String value) {
        if (!(this.item.getItemMeta() instanceof SkullMeta)) {
            return this;
        }

        final SkullMeta meta = (SkullMeta) this.item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));

        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder glow() {

        try {
            final Class<?> clazz = Class.forName("net.minecraft.server.v1_7_R4.EnchantmentGlow");

            final Object object = clazz.getConstructor(Integer.class).newInstance(70);

            if (!(object instanceof Enchantment)) {
                return this;
            }

            final ItemMeta itemMeta = item.getItemMeta();

            itemMeta.addEnchant((Enchantment) object, 1, true);

            this.item.setItemMeta(itemMeta);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            this.enchant(Enchantment.DURABILITY, 10);
        }

        return this;
    }

    public ItemStack build() {
        return this.item.clone();
    }
}
