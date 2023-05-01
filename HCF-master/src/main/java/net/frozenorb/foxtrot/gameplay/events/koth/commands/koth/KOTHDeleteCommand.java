package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

public class KOTHDeleteCommand {

    @Command(names={ "KOTH Delete", "events delete", "event delete" }, permission="foxtrot.koth.admin")
    public static void kothDelete(Player sender, @Parameter(name="koth") Event koth) {
        Foxtrot.getInstance().getEventHandler().getEvents().remove(koth);
        Foxtrot.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + ".");
    }

}