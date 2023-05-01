package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.Proton;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.shop.ShopMainMenu;
import net.frozenorb.foxtrot.gameplay.loot.shop.ShopMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

public class BalanceCommand {

    @Command(names = {"Balance", "Econ", "Bal", "$"}, permission = "")
    public static void balance(Player sender, @Parameter(name = "player", defaultValue = "self") UUID player) {
        if (sender.getUniqueId().equals(player)) {
            sender.sendMessage(ChatColor.GOLD + "Balance: " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + NumberFormat.getNumberInstance(Locale.US).format(Foxtrot.getInstance().getEconomyHandler().getBalance(sender.getUniqueId())));
        } else {
            sender.sendMessage(ChatColor.GOLD + "Balance of " + Proton.getInstance().getUuidCache().name(player) + ": " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + NumberFormat.getNumberInstance(Locale.US).format(Foxtrot.getInstance().getEconomyHandler().getBalance(player)));
        }
    }

    @Command(names = {"shop"}, permission = "")
    public static void shop(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation().clone())) {
            player.sendMessage(ChatColor.RED + "You must be in Spawn!");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not access the shop whilst in Combat!");
            return;
        }

        Foxtrot.getInstance().getQuestHandler().completeQuest(player, "Shop");

        new ShopMainMenu().openMenu(player);
    }

    @Command(names = {"buyshop"}, permission = "")
    public static void buyShop(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation().clone())) {
            player.sendMessage(ChatColor.RED + "You must be in Spawn!");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not access the shop whilst in Combat!");
            return;
        }

        new ShopMenu(true).openMenu(player);
    }

    @Command(names = {"sellshop"}, permission = "")
    public static void sellshop(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation().clone())) {
            player.sendMessage(ChatColor.RED + "You must be in Spawn!");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not access the shop whilst in Combat!");
            return;
        }

        new ShopMenu(false).openMenu(player);
    }

    @Command(names={ "SetBal" }, permission="foxtrot.setbal")
    public static void setBal(CommandSender sender, @Parameter(name="player") UUID player, @Parameter(name="amount") float amount) {

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }

        Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(player);
        Foxtrot.getInstance().getEconomyHandler().setBalance(player, amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + player + "§6 set to §e$" + amount);
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + amount + "§a by §4CONSOLE§a.");
        }

        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(player, amount);
    }

    @Command(names={ "addBal" }, permission="foxtrot.addbal")
    public static void addBal(CommandSender sender, @Parameter(name="player") UUID player, @Parameter(name="amount") float amount) {
        if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot add a balance this high. This action has been logged.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }

        Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(player);
        Foxtrot.getInstance().getEconomyHandler().setBalance(player, Foxtrot.getInstance().getEconomyHandler().getBalance(player) + amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§6Balance for §e" + player + "§6 set to §e$" + Foxtrot.getInstance().getEconomyHandler().getBalance(player));
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + Foxtrot.getInstance().getEconomyHandler().getBalance(player) + "§a by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§aYour balance has been set to §6$" + Foxtrot.getInstance().getEconomyHandler().getBalance(player) + "§a by §4CONSOLE§a.");
        }

        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(player, Foxtrot.getInstance().getEconomyHandler().getBalance(player));
    }
}