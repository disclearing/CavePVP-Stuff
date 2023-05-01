package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamChatSpyDelCommand {

    @Command(names={ "team chatspy del", "t chatspy del", "f chatspy del", "faction chatspy del", "fac chatspy del" }, permission="foxtrot.chatspy")
    public static void teamChatSpyDel(Player sender, @Parameter(name="team") Team team) {
        if (!Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()).contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You are not spying on " + team.getName() + ".");
            return;
        }

        List<ObjectId> teams = new ArrayList<>(Foxtrot.getInstance().getChatSpyMap().getChatSpy(sender.getUniqueId()));

        teams.remove(team.getUniqueId());

        Foxtrot.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), teams);
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on the chat of " + ChatColor.YELLOW + team.getName() + ChatColor.GREEN + ".");
    }

}