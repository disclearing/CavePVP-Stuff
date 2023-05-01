package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class TeamSubclaimMapCommand {

    @Command(names={ "team subclaim map", "t subclaim map", "f subclaim map", "faction subclaim map", "fac subclaim map", "team sub map", "t sub map", "f sub map", "faction sub map", "fac sub map" }, permission="")
    public static void teamSubclaimMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, false)).draw(false);
    }

}