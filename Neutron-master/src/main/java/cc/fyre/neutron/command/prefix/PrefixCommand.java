package cc.fyre.neutron.command.prefix;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.menu.PrefixMenu;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

public class PrefixCommand {

    @Command(
            names = {"prefix"},
            permission = ""
     )
    public static void execute(Player player) {
        new PrefixMenu(Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId())).openMenu(player);
    }

}
