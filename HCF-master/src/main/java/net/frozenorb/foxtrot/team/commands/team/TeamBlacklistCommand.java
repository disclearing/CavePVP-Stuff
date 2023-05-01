package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

public class TeamBlacklistCommand {

    @Command(names={ "team blacklist", "t blacklist", "f blacklist", "faction blacklist", "fac blacklist" }, permission="foxtrot.command.faction.ban")
    public static void teamBan(Player sender, @Parameter(name="team") final Team team, @Parameter(name="reason", wildcard=true) String reason) {
        for (Player player : team.getOnlineMembers()) {
            sender.chat("/blacklist " + player.getName() + " " + reason + " -p");
        }
    }

}
