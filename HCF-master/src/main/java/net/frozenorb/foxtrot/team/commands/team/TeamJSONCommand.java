package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.command.CommandSender;

public class TeamJSONCommand {

    @Command(names={ "team json", "t json", "f json", "faction json", "fac json" }, permission="op")
    public static void teamJSON(CommandSender sender, @Parameter(name="team", defaultValue="self") Team team) {
        sender.sendMessage(team.toJSON().toString());
    }

}