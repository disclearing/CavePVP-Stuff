package cc.fyre.hub.util;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    @Getter
    private ItemMeta meta;
    @Getter
    private ItemStack stack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    public ItemBuilder(ItemStack stack) {
        Preconditions.checkNotNull((Object) stack, "ItemStack cannot be null");
        this.stack = stack;
    }

    public ItemBuilder(ItemMeta meta, ItemStack stack) {
        Preconditions.checkNotNull(stack, "ItemStack cannot be null");
        this.meta = meta;
        this.stack = stack;
    }

    public ItemBuilder(Material material, int amount, byte data) {
        Preconditions.checkNotNull(material, "Material cannot be null");
        Preconditions.checkArgument((amount > 0), "Amount must be positive");

        this.stack = new ItemStack(material, amount, data);
    }

    public ItemBuilder displayName(String name) {

        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }

        this.meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder loreLine(String line) {

        final List<String> lore = this.meta.hasLore() ? this.meta.getLore() : new ArrayList<>();

        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }

        lore.add(!lore.isEmpty() ? lore.size() : 0, line);

        this.lore(line);
        return this;
    }

    public ItemBuilder addLore(String line) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }

        final List<String> lore = this.meta.hasLore() ? this.meta.getLore() : new ArrayList<>();

        lore.add(line);

        this.meta.setLore(lore);
        this.stack.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        if (this.meta == null) {
            this.meta = this.stack.getItemMeta();
        }
        this.meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder enchantNormal(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return this.enchant(enchantment, level, true);
    }

    public ItemBuilder enchant(Enchantment enchantment, int level, boolean unsafe) {
        if (unsafe && level >= enchantment.getMaxLevel()) {
            this.stack.addUnsafeEnchantment(enchantment, level);
        } else {
            this.stack.addEnchantment(enchantment, level);
        }
        return this;
    }

    public ItemBuilder data(short data) {
        this.stack.setDurability(data);
        return this;
    }

    public ItemStack build() {

        if (this.meta != null) {
            this.stack.setItemMeta(this.meta);
        }

        return this.stack;
    }

    public ItemBuilder setStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setMeta(ItemMeta meta) {
        this.meta = meta;
        return this;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this.meta.clone(), this.stack.clone());
    }
}