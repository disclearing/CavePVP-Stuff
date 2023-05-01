package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.util.PlayerUtil;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

import net.frozenorb.foxtrot.util.CC;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.regex.Pattern;

public class TeamCreateCommand {

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Set<String> disallowedTeamNames = ImmutableSet.of("list", "glowstone", "self", "outpost", "treasure");

    @Command(names = {"team create", "t create", "f create", "faction create", "fac create"}, permission = "")
    public static void teamCreate(Player sender, @Parameter(name = "team") String team) {
        if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
            sender.sendMessage(ChatColor.GRAY + "You're already in a faction!");
            return;
        }

        if (team.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum faction name size is 16 characters!");
            return;
        }

        if (team.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum faction name size is 3 characters!");
            return;
        }

        if (disallowedTeamNames.stream().anyMatch(it -> it.equalsIgnoreCase(team)) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can not create a faction with that name!");
            return;
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam(team) != null) {
            sender.sendMessage(ChatColor.GRAY + "That faction already exists!");
            return;
        }

        if (ALPHA_NUMERIC.matcher(team).find()) {
            sender.sendMessage(ChatColor.RED + "Faction names must be alphanumeric!");
            return;
        }

        if (EOTWCommand.realFFAStarted() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You can't create a faction during FFA.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW() && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No factions!");
            return;
        }

        Team createdTeam = new Team(team);

        TeamActionTracker.logActionAsync(createdTeam, TeamActionType.PLAYER_CREATE_TEAM, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName()
        ));

        createdTeam.setUniqueId(new ObjectId());
        createdTeam.setOwner(sender.getUniqueId());
        createdTeam.setName(team);
        createdTeam.setDTR(1);

        Foxtrot.getInstance().getQuestHandler().completeQuest(sender, "CreateOrJoin");
        Foxtrot.getInstance().getTeamHandler().setupTeam(createdTeam);

        LunarClientListener.updateNametag(sender);

        if (DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            PlayerUtil.sendTitle(sender, "&4&lGet Started", "&fType &c/rtp &fto start your claiming process!");
        } else {
            PlayerUtil.sendTitle(sender, "&4&lGet Started", "&fFind an open &7Wilderness &farea and type &c/f claim&f!");
        }

        Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "sbc &eFaction &9" + createdTeam.getName() + " &ehas been &acreated &eby &f" + Neutron.getInstance().getProfileHandler().fromUuid(sender.getUniqueId()).getFancyName() + "&e.");
    }

}