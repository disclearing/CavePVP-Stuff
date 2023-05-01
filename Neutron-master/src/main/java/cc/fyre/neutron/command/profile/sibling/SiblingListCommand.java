package cc.fyre.neutron.command.profile.sibling;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.comparator.ProfileWeightComparator;
import cc.fyre.neutron.profile.menu.SiblingsMenu;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SiblingListCommand {

    @Command(
            names = {"sibling list"},
            permission = "neutron.command.sibling.list", async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Flag(value = "g")boolean gui) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getSiblings().isEmpty()) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have any siblings.");
            return;
        }

        final List<Profile> siblings = profile.getSiblings().stream().map(sibling -> Neutron.getInstance().getProfileHandler().fromUuid(sibling,true)).sorted(new ProfileWeightComparator()).collect(Collectors.toList());

        if (sender instanceof Player && gui) {
            new SiblingsMenu(profile,siblings).openMenu((Player)sender);
            return;
        }

        sender.sendMessage(profile.getFancyName() + ChatColor.GOLD + "'s siblings:");

        for (Profile sibling : siblings) {
            sender.sendMessage(sibling.getFancyName());
        }

    }
}
