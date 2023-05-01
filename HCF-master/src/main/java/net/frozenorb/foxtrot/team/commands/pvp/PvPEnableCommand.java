package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class PvPEnableCommand {

    @Command(names={ "pvptimer enable", "timer enable", "pvp enable", "pvptimer remove", "timer remove", "pvp remove" }, permission="")
    public static void pvpEnable(Player sender, @Parameter(name="target", defaultValue="self") Player target) {
        if (target != sender && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())) {
            target.removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
            Foxtrot.getInstance().getPvPTimerMap().removeTimer(target.getUniqueId());
            LunarClientListener.updateNametag(target);

            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Your PvP Timer has been removed!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + "'s PvP Timer has been removed!");
            }
        } else {
            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "You do not have a PvP Timer!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " does not have a PvP Timer.");
            }
        }
    }

}