package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TeamSetBoosterCommand {
    public static Map<ObjectId, Long> CACHE = new HashMap<>();

    @Command(names={ "team setbooster" }, permission="op")
    public static void setBooster(Player sender, @Parameter(name="team") Team team) {

    }

}
