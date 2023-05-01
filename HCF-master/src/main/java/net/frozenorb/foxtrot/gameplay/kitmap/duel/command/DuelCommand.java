package net.frozenorb.foxtrot.gameplay.kitmap.duel.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.DuelHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.menu.SelectWagerMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelCommand {

    @Command(names = { "duel" }, permission = "")
    public static void duel(Player sender, @Parameter(name = "player") Player target) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command is only available on KitMap!");
            return;
        }

        if (true) {
            sender.sendMessage(ChatColor.RED + "Duels have been temporarily blocked!");
            return;
        }

        DuelHandler duelHandler = Foxtrot.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canDuel(sender, target)) {
            return;
        }

        new SelectWagerMenu(wager -> {
            sender.closeInventory();
            duelHandler.sendDuelRequest(sender, target, wager);
        }).openMenu(sender);
    }

}
