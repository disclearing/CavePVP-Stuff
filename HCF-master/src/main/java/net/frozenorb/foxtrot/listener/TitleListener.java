package net.frozenorb.foxtrot.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.universe.UniverseAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import cc.fyre.neutron.util.PlayerUtil;

public class TitleListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            PlayerUtil.sendTitle(player, "&4&l" + UniverseAPI.getServerName(), "&fWelcome to " + UniverseAPI.getServerName() + "!");
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                PlayerUtil.sendTitle(player, "&4&lGet Started", "&fCreating a faction by typing /f create [name]");

                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                player.sendMessage("");
                player.sendMessage(ChatColor.translate("&4&lWelcome"));
                player.sendMessage(ChatColor.translate("&fWelcome to CavePvP " + UniverseAPI.getServerName() + "!"));
                player.sendMessage("");
                player.sendMessage(ChatColor.translate("&4&lGet Started"));
                player.sendMessage(ChatColor.translate("&fType &c/f create [name] &fto create a faction!"));
                player.sendMessage("");
            }, 20*8);
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        PlayerUtil.sendTitle(player, "&4&l" + UniverseAPI.getServerName(), "&fWelcome back to " + UniverseAPI.getServerName() + "!");

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (team != null && !team.getClaims().isEmpty()) {
                if (profile.getGrants().stream().noneMatch(it -> it.getRank().getName().equalsIgnoreCase("Iron"))) {
                    PlayerUtil.sendTitle(player, "&4&lFree Rank", "&fClaim your free &7&lIron Rank &fby typing &c/freerank");
                    return;
                } else {
                    PlayerUtil.sendTitle(player, "&4&lVote", "&fGet free items by typing &c/vote&f!");
                }
                return;
            }

            if (team == null) {
                PlayerUtil.sendTitle(player, "&4&lGet Started", "&fCreate a faction by typing /f create [name]");
                return;
            }

            if (team.getClaims().isEmpty()) {
                if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                    PlayerUtil.sendTitle(player, "&4&lGet Started", "&fType &c/rtp &fto start your claiming process!");
                    return;
                }

                PlayerUtil.sendTitle(player, "&4&lGet Started", "&fFind an open &7Wilderness &farea and type &c/f claim&f!");
            }
        }, 20*8);
    }

}
