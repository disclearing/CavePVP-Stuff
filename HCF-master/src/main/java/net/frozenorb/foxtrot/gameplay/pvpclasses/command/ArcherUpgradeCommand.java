package net.frozenorb.foxtrot.gameplay.pvpclasses.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.pvpclasses.menu.ArcherUpgradesMenu;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

/**
 * @author xanderume@gmail.com
 */
public class ArcherUpgradeCommand {

    @Command(names = {"archerupgrade", "archerupgrades", "archer"}, permission = "")
    public static void execute(Player player) {

        if (!player.isOp() && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(ChatColor.RED + "No permission");
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You must be in Spawn to run this command!");
            return;
        }

        new ArcherUpgradesMenu().openMenu(player);
    }

}
