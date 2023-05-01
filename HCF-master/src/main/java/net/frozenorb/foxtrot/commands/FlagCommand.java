package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlagCommand {

    @Command(
            names = {"flag list","flags list"},
            permission = "foxtrot.command.flag.list",
            hidden = true
    )
    public static void bitmaskList(Player sender) {

        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }

    }

    @Command(
            names = {"flag info","flags info"},
            permission = "foxtrot.command.flag.info",
            hidden = true
    )
    public static void bitmaskInfo(Player sender, @Parameter(name="team") Team team) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Bitmask flags of " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + ":");

        for (DTRBitmask bitmaskType : DTRBitmask.values()) {

            if (!team.hasDTRBitmask(bitmaskType)) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }

        sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + team.getDTR());
    }

    @Command(
            names = {"flag add","flags add"},
            permission = "foxtrot.command.flag.add",
            hidden = true
    )
    public static void bitmaskAdd(Player sender, @Parameter(name="target")Team team, @Parameter(name="bitmask")DTRBitmask bitmask) {

        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim already has the bitmask value " + bitmask.getName() + ".");
            return;
        }

        int dtrInt = (int) team.getDTR();

        dtrInt += bitmask.getBitmask();

        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }

    @Command(
            names = {"flag setup", "flags setup", "f setupsysfac", "faction setupsysfac", "team setupsysfac", "t setupsysfac"},
            permission = "foxtrot.command.flag.setup",
            hidden = true
    )
    public static void flagSetup(Player sender) {

        final Team southRoad = Foxtrot.getInstance().getTeamHandler().getTeam("SouthRoad");
        final Team eastRoad = Foxtrot.getInstance().getTeamHandler().getTeam("EastRoad");
        final Team westRoad = Foxtrot.getInstance().getTeamHandler().getTeam("WestRoad");
        final Team northRoad = Foxtrot.getInstance().getTeamHandler().getTeam("NorthRoad");
        final Team netherRoad = Foxtrot.getInstance().getTeamHandler().getTeam("NetherRoad");
        southRoad.setDTR(1024);
        northRoad.setDTR(1024);
        westRoad.setDTR(1024);
        eastRoad.setDTR(1024);
        netherRoad.setDTR(1024);

        sender.sendMessage(ChatColor.GREEN + "Detected All Roads, added flags.");

        Foxtrot.getInstance().getEventHandler().getEvents().stream().filter(it -> it.getType() == EventType.KOTH && !it.isHidden() && Foxtrot.getInstance().getTeamHandler().getTeam(it.getName()) != null).forEach(it -> {
            Team faction = Foxtrot.getInstance().getTeamHandler().getTeam(it.getName());
            faction.setDTR(64);

            sender.sendMessage(ChatColor.GREEN + "Detected " + it.getName() + " Faction, added flags.");
        });

        if (Foxtrot.getInstance().getTeamHandler().getTeam("Citadel") != null) {
            Team citadel = Foxtrot.getInstance().getTeamHandler().getTeam("Citadel");
            citadel.setDTR(416);

            sender.sendMessage(ChatColor.GREEN + "Detected Citadel Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("NetherCitadel") != null) {
            Team citadel = Foxtrot.getInstance().getTeamHandler().getTeam("NetherCitadel");
            citadel.setDTR(416);

            sender.sendMessage(ChatColor.GREEN + "Detected Nether Citadel Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("Conquest") != null) {
            Team conquest = Foxtrot.getInstance().getTeamHandler().getTeam("Conquest");
            conquest.setDTR(2436);

            sender.sendMessage(ChatColor.GREEN + "Detected Conquest Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("Glowstone") != null) {
            Team glowstone = Foxtrot.getInstance().getTeamHandler().getTeam("Glowstone");
            glowstone.setDTR(16384);

            sender.sendMessage(ChatColor.GREEN + "Detected Glowstone Mountain Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("TreasureCove") != null) {
            Team treasureCove = Foxtrot.getInstance().getTeamHandler().getTeam("TreasureCove");
            treasureCove.setDTR(16384);

            sender.sendMessage(ChatColor.GREEN + "Detected Treasure Cove Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("EndPortal") != null) {
            Team endPortal = Foxtrot.getInstance().getTeamHandler().getTeam("EndPortal");
            endPortal.setDTR(8);

            sender.sendMessage(ChatColor.GREEN + "Detected End Portal Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("BufferZone") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("BufferZone");
            bufferZone.setDTR(16384);

            sender.sendMessage(ChatColor.GREEN + "Detected Buffer Zone Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("RoadOutpost") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("RoadOutpost");
            bufferZone.setDTR(DTRBitmask.OUTPOST.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected RoadOutpost Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("EndOutpost") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("EndOutpost");
            bufferZone.setDTR(DTRBitmask.OUTPOST.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected Outpost Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("NetherOutpost") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("NetherOutpost");
            bufferZone.setDTR(DTRBitmask.OUTPOST.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected NetherOutpost Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("Zombie") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("Zombie");
            bufferZone.setDTR(DTRBitmask.MOUNTAIN.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected Zombie Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("Spider") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("Spider");
            bufferZone.setDTR(DTRBitmask.MOUNTAIN.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected Spider Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("CaveSpider") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("CaveSpider");
            bufferZone.setDTR(DTRBitmask.MOUNTAIN.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected CaveSpider Faction, added flags.");
        }

        if (Foxtrot.getInstance().getTeamHandler().getTeam("Skeleton") != null) {
            Team bufferZone = Foxtrot.getInstance().getTeamHandler().getTeam("Skeleton");
            bufferZone.setDTR(DTRBitmask.MOUNTAIN.getBitmask());

            sender.sendMessage(ChatColor.GREEN + "Detected Skeleton Faction, added flags.");
        }

        sender.sendMessage(ChatColor.GREEN + "Completed task for faction bitmask setup.");
    }

    @Command(
            names = {"flag remove","flags remove"},
            permission = "foxtrot.command.flag.remove",
            hidden = true
    )
    public static void bitmaskRemove(Player sender, @Parameter(name="team")Team team, @Parameter(name="bitmask")DTRBitmask bitmask) {

        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (!team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim doesn't have the bitmask value " + bitmask.getName() + ".");
            return;
        }

        int dtrInt = (int) team.getDTR();

        dtrInt -= bitmask.getBitmask();

        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }

}