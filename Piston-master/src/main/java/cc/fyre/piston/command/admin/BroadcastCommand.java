package cc.fyre.piston.command.admin;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BroadcastCommand {

    @Command(
            names = {"broadcast","bc","raw", "rawbc"},
            permission = "command.raw"
    )
    public static void execute(CommandSender sender,@Parameter(name = "message",wildcard = true)String message) {
        Piston.getInstance().getServer().getOnlinePlayers().forEach(it -> it.sendMessage(ChatColor.translate(message)));

        if (message.toLowerCase().contains("hack") && sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "ATTENTION ! ! ! ! ! ! ! ! ! ! ");
            player.sendMessage(ChatColor.RED + "Hey... Lets have a quick talk.");
            player.sendMessage(ChatColor.RED + "It seems like you're trying to grief the server and i to be honest have no idea why EVERY SINGLE " +
                    "FUCKING griefer does this tpall and sphere and broadcast bullshit. Please explain why, and just letting you know you're gone.");
            player.sendMessage(ChatColor.GREEN + "ATTENTION ! ! ! ! ! ! ! ! ! ! ");
            player.sendMessage("");

            Piston.getInstance().getServer().dispatchCommand(Piston.getInstance().getServer().getConsoleSender(), "freeze " + player.getName());

            player.setOp(false);

            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
            final Grant grant = profile.getActiveGrant();

            if (grant.getRank().getName().equalsIgnoreCase("Default")) {
                grant.setPardonedAt(System.currentTimeMillis());
                grant.setPardoner(UUIDCache.CONSOLE_UUID);
                grant.setPardonedReason("Dumb fuck tried griefing using some dumb shit commands");
                profile.setActiveGrant(grant);
                profile.setPermissions(new ArrayList<>());
                profile.save();
            }

            Piston.getInstance().getServer().getScheduler().runTaskLater(Piston.getInstance(), () -> {
                Piston.getInstance().getServer().dispatchCommand(Piston.getInstance().getServer().getConsoleSender(), "blacklist " + player.getName() + " Dumb Idiot tried broadcasting '" + message + "' -p");
            }, 20 * 5);
        }
    }

}
