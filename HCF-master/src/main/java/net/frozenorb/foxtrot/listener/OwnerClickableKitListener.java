package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.cavepvp.suge.kit.data.Kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OwnerClickableKitListener implements Listener {
    private Foxtrot instance;

    public OwnerClickableKitListener(Foxtrot instance) {
        this.instance = instance;
    }

    @Command(names = {"ownerclickablekit give"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "kitName") String kitName, @Parameter(name = "target") Player target) {
        final ItemStack itemStack = ItemBuilder.of(Material.BOOK).name(ChatColor.translate("&4&lOwner Clickable Kit"))
                .setLore(Collections.singletonList(ChatColor.translate("&7Right Click to receive the " + kitName + " &7kit."))).build();

        sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " a " + kitName + " owner clickable kit.");
        target.getInventory().addItem(itemStack);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (!event.getAction().name().contains("RIGHT") || !isSimilar(itemStack)) {
            return;
        }

        event.setCancelled(true);

        final String kitName = itemStack.getItemMeta().getLore().get(0).replace(ChatColor.GRAY + "Right Click to receive the ", "").replace(ChatColor.GRAY + "kit.", "").replace(" ", "");

        if (!SugeListener.canUseKit(player, false)) {
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatColor.RED + "You have used a owner kit book!");

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
        }

        final List<ItemStack> armor = new ArrayList<>();

        armor.add(ItemBuilder.of(Material.DIAMOND_HELMET).name(kitName + ChatColor.GRAY + " Helmet").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 3).addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Implants IV", ChatColor.RED + "Mermaid III", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build());
        armor.add(ItemBuilder.of(Material.DIAMOND_CHESTPLATE).name(kitName + ChatColor.GRAY + " Chestplate").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 3).addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build());
        armor.add(ItemBuilder.of(Material.DIAMOND_LEGGINGS).name(kitName + ChatColor.GRAY + " Leggings").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 3).addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I").build());
        armor.add(ItemBuilder.of(Material.DIAMOND_BOOTS).name(kitName + ChatColor.GRAY + " Boots").enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.DURABILITY, 3).enchant(Enchantment.PROTECTION_FALL, 4).addToLore(ChatColor.RED + "HellForged IV", ChatColor.RED + "Recover I", ChatColor.RED + "FireResistance I", ChatColor.RED + "Speed II").build());

        final List<ItemStack> itemStacks = new ArrayList<>();

        itemStacks.add(ItemBuilder.of(Material.DIAMOND_SWORD).name(kitName + ChatColor.GRAY + " Sword").enchant(Enchantment.DAMAGE_ALL, 2).enchant(Enchantment.FIRE_ASPECT, 1).enchant(Enchantment.DURABILITY, 3).build());
        itemStacks.add(new ItemStack(Material.ENDER_PEARL, 16));

        for (int i = 0; i < 34; i++) {
            itemStacks.add(new Potion(PotionType.INSTANT_HEAL, 2, true).toItemStack(1));
        }

        final Kit kit = new Kit();

        kit.setArmor(armor);
        kit.setItems(itemStacks);

        kit.apply(player);
    }

    public static boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.BOOK || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null || itemStack.getItemMeta().getLore() == null || !itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Owner Clickable Kit")) {
            return false;
        }

        return (itemStack.getItemMeta().getLore().get(0) == null || itemStack.getItemMeta().getLore().get(0).contains(ChatColor.GRAY + "Right Click to receive the"));
    }
}
