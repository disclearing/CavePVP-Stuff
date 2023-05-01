package net.frozenorb.foxtrot.gameplay.events.region.cavern.commands;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.Cavern;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;

public class CavernCommand {

    @Command(names = "cavern scan", permission = "op")
    public static void cavernScan(Player sender) {
        if (!Foxtrot.getInstance().getConfig().getBoolean("cavern", false)) {
            sender.sendMessage(RED + "Cavern is currently disabled. Check config.yml to toggle.");
            return;
        }

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());

        // Make sure we have a team
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (" + CavernHandler.getCavernTeamName() + ") and claim it!");
            return;
        }

        // Make sure said team has a claim
        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for '" + CavernHandler.getCavernTeamName() + "' before scanning it!");
            return;
        }

        // We have a claim, and a team, now do we have a glowstone?
        if (!Foxtrot.getInstance().getCavernHandler().hasCavern()) {
            Foxtrot.getInstance().getCavernHandler().setCavern(new Cavern());
        }

        // We have a glowstone now, we're gonna scan and save the area
        Foxtrot.getInstance().getCavernHandler().getCavern().scan();
        Foxtrot.getInstance().getCavernHandler().save(); // save to file :D

        sender.sendMessage(GREEN + "[Cavern] Scanned all ores and saved Cavern to file!");
    }

    @Command(names = "cavern reset", permission = "op")
    public static void cavernReset(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());

        // Make sure we have a team, claims, and a mountain!
        if (team == null || team.getClaims().isEmpty() || !Foxtrot.getInstance().getCavernHandler().hasCavern()) {
            sender.sendMessage(RED + "Create the team '" + CavernHandler.getCavernTeamName() + "', then make a claim for it, finally scan it! (/cavern scan)");
            return;
        }

        // Check, check, check, LIFT OFF! (reset the mountain)
        Foxtrot.getInstance().getCavernHandler().getCavern().reset();

        Bukkit.broadcastMessage(AQUA + "[Cavern]" + GREEN + " All ores have been reset!");
    }
}