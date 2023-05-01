package cc.fyre.neutron.security.command;

import cc.fyre.neutron.security.menu.AlertsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SecurityCommand {
    @Command(names = {"security viewall"}, permission = "op", hidden = true)
    public static void execute(Player player) {
        new AlertsMenu(null, null, null, null, false).openMenu(player);
    }

    @Command(names = {"security checkvictim"}, permission = "op", hidden = true)
    public static void victim(Player player, @Parameter(name = "victim") UUID victim) {
        new AlertsMenu(null, victim, null, null, false).openMenu(player);
    }

    @Command(names = {"security checktarget"}, permission = "op", hidden = true)
    public static void target(Player player, @Parameter(name = "target") UUID target) {
        new AlertsMenu(target, null, null, null, false).openMenu(player);
    }

    @Command(names = {"security checktargetvictim"}, permission = "op", hidden = true)
    public static void target(Player player, @Parameter(name = "target") UUID target, @Parameter(name = "victim") UUID victim) {
        new AlertsMenu(target, victim, null, null, false).openMenu(player);
    }
}
