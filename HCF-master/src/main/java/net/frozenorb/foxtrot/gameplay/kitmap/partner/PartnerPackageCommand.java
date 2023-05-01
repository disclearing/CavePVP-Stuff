package net.frozenorb.foxtrot.gameplay.kitmap.partner;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.EffectUtil;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PartnerPackageCommand {

    @Command(names = {"pp"}, permission = "foxtrot.pp.give")
    public static void ppGive(CommandSender sender, @Parameter(name = "player") Player target, @Parameter(name = "amount") int amount) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        ItemStack item = Foxtrot.getInstance().getPartnerCrateHandler().getCrateItem().clone();
        item.setAmount(amount);
        target.getInventory().addItem(item);
        sender.sendMessage(CC.GREEN + "Gave " + CC.DARK_GREEN + amount + " " + CC.PINK + CC.BOLD +
                item.getItemMeta().getDisplayName() + CC.GREEN + " to " + CC.DARK_GREEN + target.getName() + CC.GREEN + "!");
    }

    @Command(names = {"pp all"}, permission = "foxtrot.pp.admin")
    public static void ppall(CommandSender sender) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        ItemStack crateItem = Foxtrot.getInstance().getPartnerCrateHandler().getCrateItem();

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!InventoryUtils.addAmountToInventory(player.getInventory(), crateItem, 1)) {
                player.getWorld().dropItemNaturally(player.getLocation(), crateItem);
            }

            player.sendMessage(CC.GREEN + "You have received 1 " + crateItem.getItemMeta().getDisplayName());
        }
        sender.sendMessage(CC.GREEN + "Gave one partner package to all players!");
    }

    @Command(names = {"pp setabsorb"}, permission = "foxtrot.pp.admin")
    public static void setAbsorb(Player sender, @Parameter(name = "hearts") float hearts) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        EntityPlayer entity = ((CraftPlayer) sender).getHandle();
        sender.sendMessage(CC.GREEN + "Before: " + CC.DARK_GREEN + entity.getAbsorptionHearts());
        entity.setAbsorptionHearts(hearts);
        entity.triggerHealthUpdate();
        sender.sendMessage(CC.GREEN + "After: " + CC.DARK_GREEN + entity.getAbsorptionHearts());
    }

    @Command(names = {"pp bleed"}, permission = "foxtrot.pp.admin")
    public static void bleed(Player sender, @Parameter(name = "target", defaultValue = "self") Player target) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        EffectUtil.bleed(target);
        sender.sendMessage(CC.GREEN + "Bleeding: " + CC.DARK_GREEN + target.getName());
    }
}
