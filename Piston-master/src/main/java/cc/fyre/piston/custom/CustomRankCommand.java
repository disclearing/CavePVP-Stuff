package cc.fyre.piston.custom;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.piston.custom.menu.CustomRankMainMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CustomRankCommand {

    @Command(names = {"custom", "customrank"}, permission = "", async = true)
    public static void execute(Player player) {
        if (!player.hasPermission("command.customrank")) {
            player.sendMessage(ChatColor.RED + "You must purchase the Custom Rank to use this at https://store.cavepvp.org!");
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        new CustomRankMainMenu(profile).openMenu(player);
    }

    @Command(names = {"resetcustomrank"}, permission = "op", async = true)
    public static void reset(Player player, @Parameter(name = "target")UUID target) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(target, true);

        profile.setCustomPrefix(null);
        profile.save();

        player.sendMessage(ChatColor.GREEN + "Reset " + profile.getName() + "'s Custom prefix");
    }

}
