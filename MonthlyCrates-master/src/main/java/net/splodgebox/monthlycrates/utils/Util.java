package net.splodgebox.monthlycrates.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Util {
    public static ItemStack createItemStack(final Material type, final int amt, final String name, final boolean glow, final int data, final List<String> list) {
        ItemStack stack;

        if (data != -1) {
            stack = new ItemStack(type, amt, (short) data);
        } else {
            stack = new ItemStack(type, amt);
        }

        final ItemMeta im = stack.getItemMeta();

        if (name != null) {
            im.setDisplayName(c(name));
        }

        if (list != null) {
            final ArrayList<String> lore = new ArrayList<>();

            for (final String str : list) {
                lore.add(c(str));
            }

            im.setLore(lore);
        }

        stack.setItemMeta(im);

        if (glow) {
            glow(stack);
        }

        return stack;
    }

    public static ItemStack createItemStackSkull(final String playerName, final int amount, final String skullName, final List<String> lores) {
        final ItemStack stack = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
        final SkullMeta im = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        im.setOwner(playerName);
        im.setDisplayName(c(skullName));
        if (lores != null) {
            final ArrayList<String> lore = new ArrayList<>();
            for (final String str : lores) {
                lore.add(c(str));
            }
            im.setLore(lore);
        }
        stack.setItemMeta(im);
        return stack;
    }

    public static int randInt(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static ItemStack makeGUIPane(final Material glasstype, final DyeColor color, final int amount, final String name, final boolean glow, final List<String> lore) {
        final ItemStack g = new ItemStack(glasstype, amount, color.getWoolData());
        final ItemMeta im = g.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        final ArrayList<String> lorelist = new ArrayList<>();
        if (lore != null) {
            for (String s : lore) {
                lorelist.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            im.setLore(lorelist);
        }
        g.setItemMeta(im);
        if (glow) {
            glow(g);
        }
        return g;
    }

    public static void glow(final ItemStack itemStack) {
        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(meta);
    }

    public static String c(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}

