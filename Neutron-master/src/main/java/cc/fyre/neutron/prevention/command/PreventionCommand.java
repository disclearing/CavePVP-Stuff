package cc.fyre.neutron.prevention.command;

import cc.fyre.neutron.prevention.gui.PreventionGUI;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class PreventionCommand {
    @Command(names = "unresolvedissues", permission = "neutron.command.unresolvedissues", hidden = true)
    public static void execute(Player player) {
        new PreventionGUI().openMenu(player);
    }
}
