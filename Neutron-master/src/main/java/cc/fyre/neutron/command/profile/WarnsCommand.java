package cc.fyre.neutron.command.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.menu.WarnsMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WarnsCommand {

    @Command(
            names = {"warns"},
            permission = "neutron.command.warn", async = true
    )
    public static void execute(Player player, @Parameter(name = "player") UUID uuid) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getPunishments().isEmpty()) {
            player.sendMessage(ChatColor.RED + "That player has no punishments!");
            return;
        }

        new WarnsMenu(profile,profile.getPunishments().stream().filter(it -> it.getIType() == IPunishment.Type.NORMAL).map(it -> (Punishment)it).filter(it -> it.getType() == Punishment.Type.WARN).collect(Collectors.toList())).openMenu(player);
    }

}
