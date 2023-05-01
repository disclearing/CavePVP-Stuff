package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamChatSpyListCommand {

    @Command(names={ "team chatspy list", "t chatspy list", "f chatspy list", "faction chatspy list", "fac chatspy list" }, permission="foxtrot.chatspy")
    public static void teamChatSpyList(Player sender) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ObjectId team : Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId())) {
            Team teamObj = Foxtrot.getInstance().getTeamHandler().getTeam(team);

            if (teamObj != null) {
                stringBuilder.append(ChatColor.YELLOW).append(teamObj.getName()).append(ChatColor.GOLD).append(", ");
            }
        }

        if (stringBuilder.length() > 2) {
            stringBuilder.setLength(stringBuilder.length() - 2);
        }

        sender.sendMessage(ChatColor.GOLD + "You are currently spying on the team chat of: " + stringBuilder.toString());
    }

}