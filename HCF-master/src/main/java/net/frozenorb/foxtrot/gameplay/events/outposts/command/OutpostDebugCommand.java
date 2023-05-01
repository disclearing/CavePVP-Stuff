package net.frozenorb.foxtrot.gameplay.events.outposts.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

public class OutpostDebugCommand {

    @Command(names = {"outpost attackers"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "outpost")Outpost outpost) {
        for (Team attacker : outpost.findAttackers()) {
            player.sendMessage(attacker.getName() + " Is attacking");
        }
    }

    @Command(names = {"outpost players"}, permission = "op")
    public static void players(Player player, @Parameter(name = "outpost")Outpost outpost) {
        for (Player target : outpost.findPlayers()) {
            player.sendMessage(target.getName() + " Is a player");
        }
    }

}
