package net.frozenorb.foxtrot;

import cc.fyre.neutron.NeutronConstants;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.impl.Brackets;

public final class FoxConstants {

    public static String teamChatFormat(Player player, String message) {
        return (ChatColor.DARK_AQUA + "(Team) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String officerChatFormat(Player player, String message) {
        return (ChatColor.LIGHT_PURPLE + "(Officer) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String teamChatSpyFormat(Team team, Player player, String message) {
        return (ChatColor.GOLD + "[" + ChatColor.DARK_AQUA + "TC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_AQUA + player.getName() + ": " + message);
    }

    public static String allyChatFormat(Player player, String message) {
        return (Team.ALLY_COLOR + "(Ally) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String allyChatSpyFormat(Team team, Player player, String message) {
        return (ChatColor.GOLD + "[" + Team.ALLY_COLOR + "AC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + Team.ALLY_COLOR + player.getName() + ": " + message);
    }

    public static String publicChatFormat(Player player, Team team, String message, String customPrefix) {

        String starting = "";

        if (team != null) {
            starting = ChatColor.GOLD + "[" + Foxtrot.getInstance().getServerHandler().getDefaultRelationColor() + team.getName() + ChatColor.GOLD + "] " + ChatColor.WHITE;
        }

        final Brackets brackets = Profiles.getInstance().getReputationHandler().findBracket(player.getUniqueId(), player.getName());

        String bracketTag = "";

        if (!brackets.equals(Brackets.UNRANKED)) {
            bracketTag = brackets.getChatColor() + "✦";
        }

        return starting + customPrefix + bracketTag + NeutronConstants.formatChatDisplay(player, message);
    }

}