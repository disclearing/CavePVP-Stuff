package cc.fyre.neutron.command.staff;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.menu.GrantHistoryMenu;
import cc.fyre.neutron.profile.menu.staffhistory.StaffHistoryMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class GrantHistoryCommand {

    @Command(
            names = {"granthistory", "grantshistory"},
            permission = "neutron.command.granthistory",
            async = true
    )
    public static void execute(Player player, @Parameter(name = "player") UUID uuid) {

        player.sendMessage(ChatColor.GREEN + "Checking database for " + uuid.toString() + " grants this may take a few seconds.");

        final Profile toView = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        final Map<Grant, UUID> executedPunishments = new HashMap<>();

        for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

            final Profile profile = new Profile(document);

            if (profile.getGrants().isEmpty()) {
                continue;
            }

            for (Grant grant : profile.getGrants()) {
                if (!grant.getExecutor().toString().equalsIgnoreCase(uuid.toString())) {
                    continue;
                }

                executedPunishments.put(grant, profile.getUuid());
            }
        }

        new GrantHistoryMenu(toView,executedPunishments).openMenu(player);
    }

}
