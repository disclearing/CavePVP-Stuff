package org.cavepvp.profiles.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.concurrent.atomic.AtomicInteger;

public class ReputationCommand {

    @Command(names = {"reputation"}, permission = "")
    public static void execute(CommandSender sender, @Parameter(name = "target", defaultValue = "self")Player target) {
        if (UniverseAPI.getServerName().contains("AU")) {
            sender.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(target.getUniqueId(), target.getName());

        sender.sendMessage(ChatColor.translate(target.getName() + " &ahas &f" + playerProfile.getPlayerReputation() + " &areputation!"));
    }

    @Command(names = {"reputation add"}, permission = "command.reputation.add")
    public static void add(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "amount")double amount) {
        if (UniverseAPI.getServerName().contains("AU")) {
            sender.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(target.getUniqueId(), target.getName());

        if (playerProfile == null) {
            sender.sendMessage(ChatColor.RED + "Profile can't be found!");
            return;
        }

        playerProfile.setPlayerReputation(playerProfile.getPlayerReputation()+amount);
        playerProfile.save();

        sender.sendMessage(ChatColor.translate("&aAdded &f" + amount + "x Reputation &ato &f" + playerProfile.getName() + "'s &aprofile!"));
    }

    @Command(names = {"reputation set"}, permission = "command.reputation.set")
    public static void set(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "amount")double amount) {
        if (UniverseAPI.getServerName().contains("AU")) {
            sender.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(target.getUniqueId(), target.getName());

        if (playerProfile == null) {
            sender.sendMessage(ChatColor.RED + "Profile can't be found!");
            return;
        }

        playerProfile.setPlayerReputation(amount);
        playerProfile.save();

        sender.sendMessage(ChatColor.translate("&aSet &f" + playerProfile.getName() + "'s &areputation balance to &f" + amount + "&a."));
    }

    @Command(names = {"reputation take", "reputation remove"}, permission = "command.reputation.remove")
    public static void remove(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "amount")double amount) {
        if (UniverseAPI.getServerName().contains("AU")) {
            sender.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(target.getUniqueId(), target.getName());

        if (playerProfile == null) {
            sender.sendMessage(ChatColor.RED + "Profile can't be found!");
            return;
        }

        playerProfile.setPlayerReputation(playerProfile.getPlayerReputation()-amount);
        playerProfile.save();

        sender.sendMessage(ChatColor.translate("&aTook &f" + amount + "x Reputation &ato &f" + playerProfile.getName() + "'s &aprofile!"));
    }

    @Command(names = {"reputation clearall"}, permission = "op")
    public static void clear(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Starting...");

        Profiles.getInstance().getServer().getScheduler().runTaskAsynchronously(Profiles.getInstance(), () -> {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            AtomicDouble atomicDouble = new AtomicDouble(0);

            Profiles.getInstance().getPlayerProfileHandler().getCollection().find().iterator().forEachRemaining(it -> {
                final PlayerProfile playerProfile = Profiles.GSON.fromJson(it.toJson(), PlayerProfile.class);

                if (playerProfile.getPlayerReputation() > 0) {
                    atomicDouble.addAndGet(playerProfile.getPlayerReputation());
                    playerProfile.setPlayerReputation(0);
                    playerProfile.save();
                    atomicInteger.incrementAndGet();
                }

                if (atomicInteger.get() % 100 == 0) {
                    System.out.println("Scanned " + atomicDouble.get() + " so far.");
                }

            });

            sender.sendMessage(ChatColor.GREEN + "Cleared " + atomicInteger.get() + " reputation worth " + atomicDouble.get() + ".");
        });
    }

}
