package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CaveNiteStartCommand {

    @Command(names = {"cavenite start"}, permission = "op")
    public static void execute(CommandSender sender) {
        final CaveNiteHandler caveniteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        if (caveniteHandler.getGameState() != CaveNiteState.INACTIVE) {
            sender.sendMessage(ChatColor.RED + "Its a no for me");
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam("CaveNite");

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "The 'CaveNite' faction doesn't exist!");
            return;
        }

        caveniteHandler.start();
    }

}
