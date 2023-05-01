package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamTopCommand {

    @Command(names={ "team top", "t top", "f top", "faction top", "fac top" }, permission="")
    public static void teamList(final CommandSender sender) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getSortedPointTeams();

                int index = 0;

                sender.sendMessage(Team.DARK_GRAY_LINE);
                sender.sendMessage("§4§lTop Factions Points");
                sender.sendMessage(" ");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    
                    if (teamEntry.getKey().getOwner() == null) {
                        continue;
                    }
                    
                    index++;

                    if (11 <= index) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();

                    Team team = teamEntry.getKey();

                    teamMessage.text(index + ". ").color(ChatColor.RED).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                    .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED).toString() + teamEntry.getKey().getName() + "\n" +
                    ChatColor.RED + "Leader: " + ChatColor.GRAY + Proton.getInstance().getUuidCache().name(teamEntry.getKey().getOwner()) + "\n\n" +
                            ChatColor.RED + "Balance: " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + ((int)Math.round(team.getBalance())) + "\n" +
                    ChatColor.RED + "Kills: " + ChatColor.GRAY + team.getKills() + "\n" +
                            ChatColor.RED + "Deaths: " + ChatColor.GRAY + team.getDeaths() + "\n\n" +
                            ChatColor.RED + "KOTH Captures: " + ChatColor.GRAY + team.getKothCaptures() + "\n" +
                            ChatColor.RED + "Citadel Captures: " + ChatColor.GRAY + team.getCitadelsCapped() + "\n" +
                            ChatColor.RED + "Conquest Captures: " + ChatColor.GRAY + team.getConquestsCapped() + "\n\n" +
                     ChatColor.GREEN + "Click to view faction info").command("/t who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(" - ").color(ChatColor.WHITE).then();
                    teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.WHITE);

                    teamMessage.send(sender);
                }

                sender.sendMessage(Team.DARK_GRAY_LINE);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    @Command(names={ "team top trapping", "t top trapping", "f top trapping", "faction top trapping", "fac top trapping" }, permission="")
    public static void teamTrapping(final CommandSender sender) {

        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            sender.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
            return;
        }

        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getSortedRaidableTeams();

                int index = 0;

                sender.sendMessage(Team.DARK_GRAY_LINE);
                sender.sendMessage("§4§lTop Factions Trapping");
                sender.sendMessage(" ");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

                    if (teamEntry.getKey().getOwner() == null) {
                        continue;
                    }

                    index++;

                    if (11 <= index) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();

                    Team team = teamEntry.getKey();

                    teamMessage.text(index + ". ").color(ChatColor.RED).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                            .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED).toString() + teamEntry.getKey().getName() + "\n" +
                                    ChatColor.RED + "Leader: " + ChatColor.GRAY + Proton.getInstance().getUuidCache().name(teamEntry.getKey().getOwner()) + "\n\n" +
                                    ChatColor.RED + "Balance: " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + ((int)Math.round(team.getBalance())) + "\n" +
                                    ChatColor.RED + "Kills: " + ChatColor.GRAY + team.getKills() + "\n" +
                                    ChatColor.RED + "Deaths: " + ChatColor.GRAY + team.getDeaths() + "\n\n" +
                                    ChatColor.RED + "KOTH Captures: " + ChatColor.GRAY + team.getKothCaptures() + "\n" +
                                    ChatColor.RED + "Citadel Captures: " + ChatColor.GRAY + team.getCitadelsCapped() + "\n" +
                                    ChatColor.RED + "Conquest Captures: " + ChatColor.GRAY + team.getConquestsCapped() + "\n\n" +
                                    ChatColor.GREEN + "Click to view faction info").command("/t who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(" - ").color(ChatColor.WHITE).then();
                    teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.WHITE);

                    teamMessage.send(sender);
                }

                sender.sendMessage(Team.DARK_GRAY_LINE);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }


    @Command(names={ "team top raidable", "t top raidable", "f top raidable", "faction top raidable", "fac top raidable" }, permission="")
    public static void teamRaidable(final CommandSender sender) {

        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            sender.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
            return;
        }

        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getSortedRaidableTeams();

                int index = 0;

                sender.sendMessage(Team.DARK_GRAY_LINE);
                sender.sendMessage("§4§lTop Factions Raidable");
                sender.sendMessage(" ");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {

                    if (teamEntry.getKey().getOwner() == null) {
                        continue;
                    }

                    index++;

                    if (11 <= index) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();

                    Team team = teamEntry.getKey();

                    teamMessage.text(index + ". ").color(ChatColor.RED).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                            .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED).toString() + teamEntry.getKey().getName() + "\n" +
                                    ChatColor.RED + "Leader: " + ChatColor.GRAY + Proton.getInstance().getUuidCache().name(teamEntry.getKey().getOwner()) + "\n\n" +
                                    ChatColor.RED + "Balance: " + ChatColor.DARK_GREEN + "$" + ChatColor.GREEN + ((int)Math.round(team.getBalance())) + "\n" +
                                    ChatColor.RED + "Kills: " + ChatColor.GRAY + team.getKills() + "\n" +
                                    ChatColor.RED + "Deaths: " + ChatColor.GRAY + team.getDeaths() + "\n\n" +
                                    ChatColor.RED + "KOTH Captures: " + ChatColor.GRAY + team.getKothCaptures() + "\n" +
                                    ChatColor.RED + "Citadel Captures: " + ChatColor.GRAY + team.getCitadelsCapped() + "\n" +
                                    ChatColor.RED + "Conquest Captures: " + ChatColor.GRAY + team.getConquestsCapped() + "\n\n" +
                                    ChatColor.GREEN + "Click to view faction info").command("/t who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(" - ").color(ChatColor.WHITE).then();
                    teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.WHITE);

                    teamMessage.send(sender);
                }

                sender.sendMessage(Team.DARK_GRAY_LINE);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static LinkedHashMap<Team, Integer> getSortedPointTeams() {
        Map<Team, Integer> teamPointsCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            teamPointsCount.put(team, team.recalculatePoints());
        }

        return sortByValues(teamPointsCount);
    }

    public static LinkedHashMap<Team, Integer> getSortedRaidableTeams() {
        Map<Team, Integer> teamRaidableCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            teamRaidableCount.put(team, team.getFactionsMadeRaidable());
        }

        return sortByValues(teamRaidableCount);
    }

    public static LinkedHashMap<Team, Integer> getSortedTrappingPoints() {
        Map<Team, Integer> teamRaidableCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            teamRaidableCount.put(team, team.getTrappingPoints());
        }

        return sortByValues(teamRaidableCount);
    }

    public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}
