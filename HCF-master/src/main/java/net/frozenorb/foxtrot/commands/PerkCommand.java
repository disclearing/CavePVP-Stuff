package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PerkCommand {
    public static Map<UUID, UUID> requests = new HashMap<>();

    @Command(names = {"warp end"}, permission = "")
    public static void execute(Player player) {
        if (!player.hasPermission("perk.warp.end")) {
            player.sendMessage(ChatColor.RED + "You do not have the /warp End perk!");
            player.sendMessage(ChatColor.RED + "Purchase this perk at https://store.cavepvp.org/category/perks");
            return;
        }

        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can only warp during SOTW Timer!");
            return;
        }

        player.teleport(Foxtrot.getInstance().getServer().getWorld("world_the_end").getSpawnLocation().clone());
        player.sendMessage(ChatColor.translate("&6You have warped to &5&lThe End&6!"));
    }

    @Command(names = {"warp nether"}, permission = "")
    public static void nether(Player player) {
        if (!player.hasPermission("perk.warp.nether")) {
            player.sendMessage(ChatColor.RED + "You do not have the /warp Nether perk!");
            player.sendMessage(ChatColor.RED + "Purchase this perk at https://store.cavepvp.org/category/perks");
            return;
        }

        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can only warp during SOTW Timer!");
            return;
        }

        player.teleport(Foxtrot.getInstance().getServer().getWorld("world_nether").getSpawnLocation().clone());
        player.sendMessage(ChatColor.translate("&6You have warped to &4&lNether&6!"));
    }

    @Command(names = {"tpa"}, permission = "")
    public static void tpa(Player player, @Parameter(name = "target")Player target) {
        if (!player.hasPermission("perk.warp.tpa")) {
            player.sendMessage(ChatColor.RED + "You do not have the /tpa perk!");
            player.sendMessage(ChatColor.RED + "Purchase this perk at https://store.cavepvp.org/category/perks");
            return;
        }

        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can only teleport during SOTW Timer!");
            return;
        }

        if (requests.containsKey(player.getUniqueId()) && requests.get(player.getUniqueId()).toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "You already have an outgoing response");
            return;
        }

        player.sendMessage(ChatColor.translate("&aSent a teleportation request to " + target.getName() + ", expires in &f30 seconds&a."));

        target.sendMessage(ChatColor.translate(player.getName() + " &6has requested to teleport to you."));
        target.sendMessage(ChatColor.translate("&7Type &f/tpaccept &7to let them accept the teleport request."));

        requests.put(target.getUniqueId(), player.getUniqueId());

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> requests.remove(player.getUniqueId()), 20*30);
    }

    @Command(names = {"tpayes", "tpyes", "tpaccept"}, permission = "")
    public static void accept(Player player) {
        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can only teleport during SOTW Timer!");
            return;
        }

        if (!requests.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have no available teleportation requests!");
            return;
        }

        final Player target = Foxtrot.getInstance().getServer().getPlayer(requests.remove(player.getUniqueId()));

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is no longer online!");
            return;
        }

        target.sendMessage(ChatColor.translate(player.getName() + " &6has teleported to you."));
        player.sendMessage(ChatColor.GREEN + "Teleported to " + target.getName() + ".");
        target.teleport(player.getLocation());
    }

    @Command(names = {"tpdeny", "tpadeny"}, permission = "")
    public static void deny(Player player) {
        if (!requests.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have no available teleportation requests!");
            return;
        }

        requests.remove(player.getUniqueId());

        player.sendMessage(ChatColor.RED + "Denied your recent teleportation request.");
    }
}
