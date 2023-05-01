package net.frozenorb.foxtrot.gameplay.blockshop;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.blockshop.menu.BlockShopMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Not created by vape on 10/30/2020 at 3:40 PM.
 */
public class BlockShopCommand {

    @Command(names = {"blockshop", "bs"}, permission = "")
    public static void blockShop(Player player) {
        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
            return;
        }

        boolean inSpawn = DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation());
        boolean inCombat = SpawnTagHandler.isTagged(player);
        boolean SOTW = CustomTimerCreateCommand.isSOTWTimer();

        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !SOTW && !inSpawn && inCombat) {
            player.sendMessage(ChatColor.RED + "You can't open the Block Shop while combat tagged.");
            return;
        }

        new BlockShopMenu().openMenu(player);
    }
}
