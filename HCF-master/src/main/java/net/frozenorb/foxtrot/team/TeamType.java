package net.frozenorb.foxtrot.team;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.util.UUIDUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TeamType implements ParameterType<Team> {

    public Team transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(((Player) sender).getUniqueId());

            if (team == null) {
                sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
                return (null);
            }

            return (team);
        }

        Team byName = Foxtrot.getInstance().getTeamHandler().getTeam(source);

        if (byName != null) {
            return (byName);
        }

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(source);

        if (bukkitPlayer != null) {
            Team byMemberBukkitPlayer = Foxtrot.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());

            if (byMemberBukkitPlayer != null) {
                return (byMemberBukkitPlayer);
            }
        }

        Team byMemberUUID = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(source));

        if (byMemberUUID != null) {
            return (byMemberUUID);
        }

        sender.sendMessage(ChatColor.RED + "No team or member with the name " + source + " found.");
        return (null);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        if (source.isEmpty()) {
            return new ArrayList<>();
        }

        // Teams being included in the completion is ENABLED by default.
        if (!flags.contains("noteams")) {
            for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                if (StringUtils.startsWithIgnoreCase(team.getName(), source)) {
                    completions.add(team.getName());
                }
            }
        }

        // Players being included in the completion is DISABLED by default.
        if (flags.contains("players")) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                    completions.add(player.getName());
                }
            }
        }

        return (completions);
    }

}