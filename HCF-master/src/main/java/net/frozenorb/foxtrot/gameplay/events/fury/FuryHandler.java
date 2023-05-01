package net.frozenorb.foxtrot.gameplay.events.fury;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.fury.listener.FuryListener;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FuryHandler {
    private final Foxtrot instance;

    public static String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "FURY" + ChatColor.DARK_GRAY + "]";

    @Getter @Setter private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();
    @Getter private boolean active;
    @Getter private long lastSwitchTime;
    @Getter private FuryCapZone furyCapZone;

    public FuryHandler(Foxtrot instance) {
        this.instance = instance;

        this.active = false;
        this.furyCapZone = FuryCapZone.OVERWORLD;

        this.instance.getServer().getPluginManager().registerEvents(new FuryListener(this.instance, this), this.instance);
    }

    public void start() {
        this.getAllCapZones().forEach(it -> {
            if (!it.isHidden()) {
                it.setHidden(true);
            }

            if (it.getCapTime() != 25) {
                it.setCapTime(25);
            }
        });

        this.active = true;
        this.lastSwitchTime = System.currentTimeMillis();
        this.furyCapZone = FuryCapZone.OVERWORLD;
        this.getActiveCapZone().activate();

        this.instance.getServer().broadcastMessage(ChatColor.translate(PREFIX + " &eFury &6has started!"));
    }

    public void switchCapZone() {
        this.getActiveCapZone().deactivate();

        this.furyCapZone = Arrays.stream(FuryCapZone.values()).filter(it -> it.getOrder() == this.furyCapZone.getOrder()+1).findFirst().orElse(FuryCapZone.OVERWORLD);
        this.lastSwitchTime = System.currentTimeMillis();

        this.getActiveCapZone().activate();

        this.instance.getServer().broadcastMessage(ChatColor.translate(PREFIX + " &6Switched Capzones to " + this.furyCapZone.getChatColor() + this.furyCapZone.getDisplayName() + "&6."));
    }

    public void death(Player player) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null || !this.teamPoints.containsKey(team.getUniqueId())) {
            return;
        }

        this.teamPoints.put(team.getUniqueId(), Math.max(0, this.teamPoints.get(team.getUniqueId()) - 20));
        this.teamPoints = sortByValues(this.teamPoints);

        this.instance.getServer().broadcastMessage(PREFIX + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + " has lost " + ChatColor.YELLOW + "20 points " + ChatColor.GOLD + "because " + ChatColor.YELLOW + player.getName() + ChatColor.GOLD + " died. " + ChatColor.RED + " (" + teamPoints.get(team.getUniqueId()) + "/" + 150 + ")");
    }

    public void endGame(Team winner) {
        this.active = false;
        this.furyCapZone = FuryCapZone.OVERWORLD;
        this.lastSwitchTime = 0;
        this.getTeamPoints().clear();

        this.getAllCapZones().forEach(KOTH::deactivate);

        if (winner == null) {
            Foxtrot.getInstance().getServer().broadcastMessage(PREFIX + " " + ChatColor.GOLD + "Fury has ended.");
            return;
        }

        winner.setFurysCaptured(winner.getFurysCaptured() + 1);

        this.instance.getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!");
    }


    public LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
        LinkedList<Map.Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<ObjectId, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

    public KOTH getKoth(FuryCapZone furyCapZone) {
        return (KOTH) this.instance.getEventHandler().getEvent("Fury_" + furyCapZone.name());
    }

    public List<KOTH> getAllCapZones() {
        return this.instance.getEventHandler().getEvents().stream().filter(it -> it.getType() == EventType.KOTH && it.getName().startsWith("Fury_")).map(it -> (KOTH)it).collect(Collectors.toList());
    }

    public KOTH getActiveCapZone() {
        return this.getKoth(this.furyCapZone);
    }
}
