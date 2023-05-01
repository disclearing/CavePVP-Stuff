package cc.fyre.neutron.command.staff;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.menu.staffhistory.StaffHistoryMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffHistoryCommand {

    @Command(
            names = {"staffhistory"},
            permission = "neutron.command.staffhistory",
            async = true
    )
    public static void execute(Player player, @Parameter(name = "player") UUID uuid) {

        player.sendMessage(ChatColor.GREEN + "Checking database for " + uuid.toString() + " this may take a few seconds.");

        final Profile toView = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        final Map<UUID,Punishment> executedPunishments = new HashMap<>();
        final Map<UUID,RemoveAblePunishment> executedRemoveAblePunishments = new HashMap<>();
        final Map<UUID,RemoveAblePunishment> pardonedRemoveAblePunishments = new HashMap<>();

        for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

            final Profile profile = new Profile(document);

            if (profile.getPunishments().isEmpty()) {
                continue;
            }

            for (IPunishment punishment : profile.getPunishments()) {
                if (punishment.getExecutor().equals(uuid)) {
                    if (punishment.getIType() == IPunishment.Type.NORMAL) {
                        executedPunishments.put(profile.getUuid(),(Punishment)punishment);
                    } else if (punishment.getIType() == IPunishment.Type.REMOVE_ABLE) {
                        executedRemoveAblePunishments.put(profile.getUuid(),(RemoveAblePunishment)punishment);
                    }
                }
                if (punishment instanceof RemoveAblePunishment && ((RemoveAblePunishment)punishment).isPardoned() && ((RemoveAblePunishment)punishment).getPardoner().equals(uuid)) {
                    pardonedRemoveAblePunishments.put(profile.getUuid(),(RemoveAblePunishment)punishment);
                }
            }
        }

        new StaffHistoryMenu(toView,executedPunishments,executedRemoveAblePunishments,pardonedRemoveAblePunishments).openMenu(player);
    }

}
