package net.frozenorb.foxtrot.gameplay.loot.crate.command;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.CrateHandler;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public class CrateCommand {

    @Command(names = {"crates give"}, permission = "command.crates.give", hidden = true)
    public static void execute(CommandSender sender, @Parameter(name = "target") Player target, @Parameter(name = "crate") Crate crate, @Parameter(name = "amount")int amount) {
        if (amount > 1 && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't give more than 1 Crate without OP!");
            return;
        }

        if (amount < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid number!");
            return;
        }

        if (!(sender instanceof ConsoleCommandSender)) {
            Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                    NeutronConstants.MANAGER_PERMISSION,
                    ChatColor.translate(ChatColor.translate(
                            LIGHT_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + (sender instanceof Player ? ((Player) sender).getDisplayName() : DARK_RED + BOLD.toString() + "Command Block")
                                    + " &7has given &f" + target.getName() + "'s &f" + amount + "x " + crate.getDisplayName() + "&7."))));
        }

        sender.sendMessage(ChatColor.translate("&aGave " + target.getName() + " &aa " + crate.getDisplayName() + "&a."));
        target.sendMessage(ChatColor.translate("&aYou have received a " + crate.getDisplayName() + "&a."));

        final ItemStack itemStack = crate.getItemStack().clone();

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);
    }

    @Command(names = {"crates create"}, permission = "command.crates.create", hidden = true)
    public static void give(Player player, @Parameter(name = "id")String id) {
        final ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            player.sendMessage(ChatColor.RED + "You must have an item in your hand with a display name.");
            return;
        }

        final List<String> lore = new ArrayList<>();
        if (itemStack.getItemMeta().getLore() != null) {
            lore.addAll(itemStack.getItemMeta().getLore());
        }

        player.sendMessage(ChatColor.GREEN + "Added a brand new crate!");
        Foxtrot.getInstance().getCrateHandler().getCrates().add(new Crate(id, itemStack.getItemMeta().getDisplayName(), itemStack.getType(), Color.RED, lore, new ArrayList<>()));
    }

    @Command(names = {"crates additem"}, permission = "command.crates.additem", hidden = true)
    public static void addItem(Player player, @Parameter(name = "crate")Crate crate, @Parameter(name = "chance")double chance, @Parameter(name = "broadcast")boolean broadcast) {
        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must have an item in your hand!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Added item to " + crate.getId() + " loot.");

        final CrateItem crateItem = new CrateItem(player.getItemInHand(), chance, null, true, broadcast);

        crate.getItems().add(crateItem);
    }

    @Command(names = {"crates giveall"}, permission = "op", hidden = true)
    public static void giveAll(CommandSender sender, @Parameter(name = "crate")Crate crate, @Parameter(name = "amount")int amount) {
        if (amount > 1 && !sender.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(RED + "No permission.");
            return;
        }

        Foxtrot.getInstance().getServer().getOnlinePlayers().forEach(it -> {
            sender.sendMessage(ChatColor.translate("&aGave " + it.getName() + " &aa " + crate.getDisplayName() + "&a."));
            it.sendMessage(ChatColor.translate("&6You have received a " + crate.getDisplayName() + "&6."));

            final ItemStack itemStack = crate.getItemStack().clone();
            itemStack.setAmount(amount);

            it.getInventory().addItem(itemStack);
            it.updateInventory();
        });

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate(ChatColor.translate(
                        LIGHT_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + (sender instanceof Player ? ((Player) sender).getDisplayName() : DARK_RED + BOLD.toString() + "Console")
                                + " &7has given &f" + sender.getName() + "'s &f" + amount + "x " + crate.getDisplayName() + "&7."))));
    }

    @Command(names = {"crates addcmd"}, permission = "command.crates.addcmd", hidden = true)
    public static void addCmd(Player player, @Parameter(name = "crate")Crate crate, @Parameter(name = "chance")double chance, @Parameter(name = "broadcast")boolean broadcast, @Parameter(name = "command", wildcard = true)String command) {
        player.sendMessage(ChatColor.GREEN + "Added command to " + crate.getId() + " loot.");

        crate.getItems().add(new CrateItem(player.getItemInHand(), chance, command, false, broadcast));
    }

    @Command(names = {"crates reload"}, permission = "op", hidden = true)
    public static void reload(Player player) {
        final CrateHandler crateHandler = Foxtrot.getInstance().getCrateHandler();

        crateHandler.getCrates().clear();
        crateHandler.loadData();
        player.sendMessage(ChatColor.GREEN + "Reloaded Crates.");
    }
}
