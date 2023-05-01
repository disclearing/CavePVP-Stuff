package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RTPCommand {
    public static boolean STATUS = false;

    @Command(names = {"rtp", "wild", "wildtp", "randomtp", "randomteleport"}, permission = "")
    public static void execute(Player player) {
        if (!canRtp(player)) {
            player.sendMessage(ChatColor.RED + "You can't use this command outside of Spawn!");
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&aYou have been teleported 1000 down South Road!"));
        player.sendMessage(ChatColor.translate("&7Type &f/f claim &7and find an open area to claim your base!"));
        player.sendMessage(ChatColor.translate("&7Once you've gotten a claim, type /f sethome to set your claim's home!"));
        player.sendMessage("");

        PlayerUtil.sendTitle(player, "&4&lGet Started", "&fFind an open &7Wilderness &farea and type &c/f claim&f!");

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.teleport(Foxtrot.getInstance().getServer().getWorld("world").getHighestBlockAt(0, 1000).getLocation().add(0, 1, 0));
    }

    @Command(names = {"rtp enable"}, permission = "op")
    public static void enable(Player player) {
        if (STATUS) {
            STATUS = false;
            player.sendMessage(ChatColor.RED + "RTP is now disabled during SOTW Timer");
        } else {
            STATUS = true;
            player.sendMessage(ChatColor.GREEN + "RTP is now enabled during SOTW Timer");
        }
    }

    public static boolean canRtp(Player player) {

        if (CustomTimerCreateCommand.isSOTWTimer() && !STATUS) {
            return false;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            return true;
        }

        return Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
    }

}
