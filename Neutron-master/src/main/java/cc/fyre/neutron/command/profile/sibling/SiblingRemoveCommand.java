package cc.fyre.neutron.command.profile.sibling;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SiblingRemoveCommand {

    @Command(
            names = {"sibling remove"},
            permission = "neutron.command.sibling.remove", async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "sibling")UUID sibling) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (!profile.getSiblings().contains(sibling)) {
            sender.sendMessage(ChatColor.RED + "This player is is not a sibling of " + profile.getFancyName() + ChatColor.RED + ".");
            return;
        }

        profile.getSiblings().remove(sibling);
        profile.save();

        final Profile siblingProfile = Neutron.getInstance().getProfileHandler().fromUuid(sibling,true);

        sender.sendMessage(siblingProfile.getFancyName() + ChatColor.GOLD + " is no longer a sibling of " + profile.getFancyName() + ChatColor.GOLD + ".");
    }

}
