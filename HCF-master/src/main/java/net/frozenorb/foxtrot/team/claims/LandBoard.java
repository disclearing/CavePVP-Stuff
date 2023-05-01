package net.frozenorb.foxtrot.team.claims;

import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.util.com.google.common.collect.HashMultimap;
import net.minecraft.util.com.google.common.collect.Multimap;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class LandBoard implements Listener {

    private static LandBoard instance;
    @Getter @Setter private boolean claimsEnabled = true;
    private final Map<String, Multimap<CoordinateSet, Map.Entry<Claim, Team>>> buckets = new ConcurrentHashMap<>();

    public LandBoard() {
        for (World world : Foxtrot.getInstance().getServer().getWorlds()) {
            buckets.put(world.getName(), HashMultimap.create());
        }

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public void loadFromTeams() {
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            for (Claim claim : team.getClaims()) {
                setTeamAt(claim, team);
            }
        }
    }

    public Set<Map.Entry<Claim, Team>> getRegionData(Location center, int xDistance, int yDistance, int zDistance) {
        Location loc1 = new Location(center.getWorld(), center.getBlockX() - xDistance, center.getBlockY() - yDistance, center.getBlockZ() - zDistance);
        Location loc2 = new Location(center.getWorld(), center.getBlockX() + xDistance, center.getBlockY() + yDistance, center.getBlockZ() + zDistance);

        return (getRegionData(loc1, loc2));
    }

    public Set<Map.Entry<Claim, Team>> getRegionData(Location min, Location max) {
        if (!claimsEnabled) { return (new HashSet<>()); }

        Set<Map.Entry<Claim, Team>> regions = new HashSet<>();
        int step = 1 << CoordinateSet.BITS;

        for (int x = min.getBlockX(); x < max.getBlockX() + step; x += step) {
            for (int z = min.getBlockZ(); z < max.getBlockZ() + step; z += step) {
                CoordinateSet coordinateSet = new CoordinateSet(x, z);

                for (Map.Entry<Claim, Team> regionEntry : buckets.get(min.getWorld().getName()).get(coordinateSet)) {
                    if (!regions.contains(regionEntry)) {
                        if ((max.getBlockX() >= regionEntry.getKey().getX1())
                                && (min.getBlockX() <= regionEntry.getKey().getX2())
                                && (max.getBlockZ() >= regionEntry.getKey().getZ1())
                                && (min.getBlockZ() <= regionEntry.getKey().getZ2())
                                && (max.getBlockY() >= regionEntry.getKey().getY1())
                                && (min.getBlockY() <= regionEntry.getKey().getY2())) {
                            regions.add(regionEntry);
                        }
                    }
                }
            }
        }

        return (regions);
    }

    public Map.Entry<Claim, Team> getRegionData(Location location) {
        if (!claimsEnabled) { return (null); }

        Map.Entry<Claim, Team> keepInMind = null;

        for (Map.Entry<Claim, Team> data : buckets.get(location.getWorld().getName()).get(new CoordinateSet(location.getBlockX(), location.getBlockZ()))) {
            if (data.getKey().contains(location)) {
                if (data.getValue().hasDTRBitmask(DTRBitmask.ROAD)) {
                    keepInMind = data;
                    continue;
                }

                keepInMind = data;
                break;
            }
        }

        return keepInMind;
    }

    public Claim getClaim(Location location) {
        Map.Entry<Claim, Team> regionData = getRegionData(location);
        return (regionData == null ? null : regionData.getKey());
    }

    public Team getTeam(Location location) {
        Map.Entry<Claim, Team> regionData = getRegionData(location);
        return regionData == null ? null : regionData.getValue();
    }

    public void setTeamAt(Claim claim, Team team) {

        Map.Entry<Claim, Team> regionData = new AbstractMap.SimpleEntry<>(claim, team);
        int step = 1 << CoordinateSet.BITS;

        for (int x = regionData.getKey().getX1(); x < regionData.getKey().getX2() + step; x += step) {
            for (int z = regionData.getKey().getZ1(); z < regionData.getKey().getZ2() + step; z += step) {
                Multimap<CoordinateSet, Map.Entry<Claim, Team>> worldMap = buckets.get(regionData.getKey().getWorld());

                if (worldMap == null) {
                    continue;
                }

                if (regionData.getValue() == null) {
                    CoordinateSet coordinateSet = new CoordinateSet(x, z);
                    Iterator<Map.Entry<Claim, Team>> claimIterator = worldMap.get(coordinateSet).iterator();

                    while (claimIterator.hasNext()) {
                        Map.Entry<Claim, Team> entry = claimIterator.next();

                        if (entry.getKey().equals(regionData.getKey())) {
                            claimIterator.remove();
                        }
                    }
                } else {
                    worldMap.put(new CoordinateSet(x, z), regionData);
                }
            }
        }

        updateClaim(claim);
    }

    public void updateClaim(Claim modified) {
        ArrayList<VisualClaim> visualClaims = new ArrayList<>();
        visualClaims.addAll(VisualClaim.getCurrentMaps().values());

        for (VisualClaim visualClaim : visualClaims) {
            if (modified.isWithin(visualClaim.getPlayer().getLocation().getBlockX(), visualClaim.getPlayer().getLocation().getBlockZ(), VisualClaim.MAP_RADIUS, modified.getWorld())) {
                visualClaim.draw(true);
                visualClaim.draw(true);
            }
        }
    }

    public void updateSubclaim(Subclaim modified) {
        ArrayList<VisualClaim> visualClaims = new ArrayList<>();
        visualClaims.addAll(VisualClaim.getCurrentSubclaimMaps().values());

        for (VisualClaim visualClaim : visualClaims) {
            if (modified.getLoc1().distanceSquared(visualClaim.getPlayer().getLocation()) < VisualClaim.MAP_RADIUS * VisualClaim.MAP_RADIUS || modified.getLoc2().distanceSquared(visualClaim.getPlayer().getLocation()) < VisualClaim.MAP_RADIUS * VisualClaim.MAP_RADIUS) {
                visualClaim.draw(true);
                visualClaim.draw(true);
            }
        }
    }

    public void clear(Team team) {
        for (Claim claim : team.getClaims()) {
            setTeamAt(claim, null);
        }
    }

    public static LandBoard getInstance() {
        if (instance == null) {
            instance = new LandBoard();
        }

        return (instance);
    }

    @EventHandler
    public void onWorldLoadEvent(WorldLoadEvent event) {
        buckets.put(event.getWorld().getName(), HashMultimap.create());
    }

}