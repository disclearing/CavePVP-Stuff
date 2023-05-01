package cc.fyre.neutron.command.profile.grant;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.menu.GrantsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantsCommand {

    @Command(
            names = {"grants"},
            permission = "neutron.command.grants", async = true
    )
    public static void execute(Player player, @Parameter(name = "player")String name) {

        final Player target = Neutron.getInstance().getServer().getPlayer(name);

        Profile profile;

        if (target != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(target.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromName(name,true,true);
        }

        if (profile == null) {
            player.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
            return;
        }

        new GrantsMenu(profile).openMenu(player);
    }

}
