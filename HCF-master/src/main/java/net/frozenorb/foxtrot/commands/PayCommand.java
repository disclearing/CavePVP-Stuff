package net.frozenorb.foxtrot.commands;

import java.text.NumberFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.UUIDUtils;

public class PayCommand {

    @Command(names={ "Pay", "P2P" }, permission="")
    public static void pay(Player sender, @Parameter(name="player") UUID player, @Parameter(name="amount") float amount) {
        double balance = Foxtrot.getInstance().getEconomyHandler().getBalance(sender.getUniqueId());
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        if (sender.equals(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + "You cannot send money to yourself!");
            return;
        }

        if (amount < 5) {
            sender.sendMessage(ChatColor.RED + "You must send at least $5!");
            return;
        }

        if (Double.isNaN(balance)) {
            sender.sendMessage("§cYou can't send money because there was an error with your balance.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (balance < amount) {
            sender.sendMessage(ChatColor.RED + "You do not have $" + amount + "!");
            return;
        }

        Foxtrot.getInstance().getEconomyHandler().deposit(player, amount);
        Foxtrot.getInstance().getEconomyHandler().withdraw(sender.getUniqueId(), amount);
 
        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(player, Foxtrot.getInstance().getEconomyHandler().getBalance(player));
        Foxtrot.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), Foxtrot.getInstance().getEconomyHandler().getBalance(sender.getUniqueId()));

        sender.sendMessage(ChatColor.YELLOW + "You sent " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.YELLOW + " to " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + ".");

        bukkitPlayer.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " sent you " + ChatColor.LIGHT_PURPLE + NumberFormat.getCurrencyInstance().format(amount) + ChatColor.YELLOW + ".");
    }

}