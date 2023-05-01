package net.frozenorb.foxtrot.team.commands.team.chatspy;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamChatSpyClearCommand {

    @Command(names={ "team chatspy clear", "t chatspy clear", "f chatspy clear", "faction chatspy clear", "fac chatspy clear" }, permission="foxtrot.chatspy")
    public static void teamChatSpyClear(Player sender) {
        Foxtrot.getInstance().getChatSpyMap().setChatSpy(sender.getUniqueId(), new ArrayList<ObjectId>());
        sender.sendMessage(ChatColor.GREEN + "You are no longer spying on any teams.");
    }

}