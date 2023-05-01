package net.frozenorb.foxtrot.team.commands.team;

import net.minecraft.util.com.google.common.collect.ImmutableMap;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamForceKickCommand {

    @Command(names = {"team forcekick", "t forcekick", "f forcekick", "faction forcekick", "fac forcekick"}, permission = "")
    public static void teamForceKick(Player sender, @Parameter(name = "player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't in your faction!");
            return;
        }

        if (team.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the faction leader!");
            return;
        }

        if(team.isCoLeader(player) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player) && (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "true"
        ));

        if (team.removeMember(player)) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(player, null);
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            team.setDTR(team.getDTR() - Foxtrot.getInstance().getServerHandler().getDTRLoss(bukkitPlayer));
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " was force kicked by " + sender.getName() + " and your faction lost 1 DTR!");
            long dtrCooldown;
            if (team.isRaidable()) {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
                dtrCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
            } else {
                dtrCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
            }

            team.setDTRCooldown(dtrCooldown);
            DTRHandler.wasOnCooldown.add(team.getUniqueId());
        } else {
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " was force kicked by " + sender.getName() + "!");
        }

        if (bukkitPlayer != null) {
            LunarClientListener.updateNametag(bukkitPlayer);

            final Team teamAt = LandBoard.getInstance().getTeam(bukkitPlayer.getLocation());

            if (!teamAt.getName().equalsIgnoreCase(team.getName())) {
                return;
            }

            for (PotionEffectType purchasedEffect : team.getPurchasedEffects()) {

                if (!team.getPurchasedEffects().contains(purchasedEffect)) {
                    continue;
                }

                final Set<CustomEnchant> customEnchantList = Suge.getInstance().getEnchantHandler().findAllCustomEnchants(bukkitPlayer).keySet();

                if (customEnchantList.stream().anyMatch(it -> it.getEffect() != null && it.getEffect().getName().equalsIgnoreCase(purchasedEffect.getName()))) {
                    continue;
                }

                bukkitPlayer.removePotionEffect(purchasedEffect);
            }
        }
    }

}