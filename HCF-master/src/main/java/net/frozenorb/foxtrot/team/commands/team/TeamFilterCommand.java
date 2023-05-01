package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.menu.FilterMenu;
import org.bukkit.entity.Player;

public class TeamFilterCommand {
    @Command(names = {"team filter", "t filter", "f filter", "fac filter", "faction filter"}, permission = "")
    public static void execute(Player player) {
        new FilterMenu(Foxtrot.getInstance().getFactionFilterMap().getFilterType(player.getUniqueId())).openMenu(player);
    }

}
