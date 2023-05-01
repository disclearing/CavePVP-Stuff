package net.frozenorb.foxtrot.gameplay.kitmap.duel.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.DuelHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.DuelInvite;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AcceptCommand {

    @Command(names = { "accept" }, permission = "")
    public static void accept(Player sender, @Parameter(name = "player") Player target) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command is only available on KitMap!");
            return;
        }

        DuelHandler duelHandler = Foxtrot.getInstance().getMapHandler().getDuelHandler();

        if (!duelHandler.canAccept(sender, target)) {
            return;
        }

        DuelInvite invite = duelHandler.getInvite(target.getUniqueId(), sender.getUniqueId());
        duelHandler.acceptDuelRequest(invite);
    }

}
