package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.scheduler.BukkitRunnable;

import static net.frozenorb.foxtrot.commands.FlagCommand.bitmaskInfo;

public class KOTHActivateCommand {

    @Command(names={ "koth Activate", "koth Active", "events activate", "koth start", "activatekoth"}, permission="foxtrot.command.koth.activate")
    public static void kothActivate(CommandSender sender, @Parameter(name="event") Event koth) {
        // Don't start a KOTH if another one is active.
        for (Event otherKoth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        if ((koth.getName().contains("citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }


        if (koth.getName().equalsIgnoreCase("Citadel") && Foxtrot.getInstance().getTeamHandler().getTeam("Citadel") != null && !Foxtrot.getInstance().getTeamHandler().getTeam("Citadel").hasDTRBitmask(DTRBitmask.CITADEL)) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam("Citadel");

            int dtrInt = (int) team.getDTR();

            dtrInt += DTRBitmask.CITADEL.getBitmask();
            team.setDTR(dtrInt);

            if (sender instanceof Player) {
                bitmaskInfo((Player) sender, team);
            }

            sender.sendMessage(ChatColor.RED + "No Citadel flag for Citadel found, adding...");
        }

        if (koth.getName().contains("NetherCitadel") && Foxtrot.getInstance().getTeamHandler().getTeam("NetherCitadel") != null && !Foxtrot.getInstance().getTeamHandler().getTeam("NetherCitadel").hasDTRBitmask(DTRBitmask.CITADEL)) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam("NetherCitadel");

            int dtrInt = (int) team.getDTR();

            dtrInt += DTRBitmask.CITADEL.getBitmask();
            team.setDTR(dtrInt);

            if (sender instanceof Player) {
                bitmaskInfo((Player) sender, team);
            }

            sender.sendMessage(ChatColor.RED + "No Citadel flag for NetherCitadel found, adding...");
        }

        koth.activate();
        sender.sendMessage(ChatColor.GREEN + "Successfully started " + ChatColor.WHITE + koth.getName() + ChatColor.GREEN + ".");
    }

    @Command(names = {"koth resettime"}, permission="foxtrot.command.koth.activate")
    public static void reset(CommandSender sender, @Parameter(name="event") Event koth) {
        if (!koth.isActive()) {
            sender.sendMessage(ChatColor.RED + "That event is not active!");
            return;
        }

        if (!(koth instanceof KOTH)) {
            sender.sendMessage(ChatColor.RED + "That event is not a KOTH!");
            return;
        }

        final KOTH koth2 = (KOTH) koth;

        koth2.resetCapTime();
        sender.sendMessage(ChatColor.RED + "Reset time! Flagged as a knock muahahahah");
    }
}
