package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.kothschedule;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHSetMiniCommand {

    @Command(names={ "KOTH setmini"}, permission="foxtrot.command.koth.location")
    public static void kothLoc(Player sender, @Parameter(name="koth") Event koth) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to set location for a non-KOTH event.");
        } else {
            final KOTH koth2 = (KOTH) koth;

            if (koth2.isMini()) {
                sender.sendMessage(ChatColor.RED + "Set " + koth2.getName() + " to no longer a Mini KOTH");
                koth2.setMini(false);
                Foxtrot.getInstance().getEventHandler().saveEvents();
                return;
            }

            sender.sendMessage(ChatColor.GREEN + "Set " + koth2.getName() + " to a Mini KOTH");
            koth2.setMini(true);
            Foxtrot.getInstance().getEventHandler().saveEvents();
        }
    }

}