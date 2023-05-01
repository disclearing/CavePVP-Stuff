package net.frozenorb.foxtrot.packetborder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Coordinate;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;

public class PacketBorderThread extends Thread {

    public static final int REGION_DISTANCE = 8;
    public static final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    private static Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<>();

    public PacketBorderThread() {
        super("Foxtrot - Packet Border Thread");
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                try {
                    checkPlayer(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPlayer(Player player) {
        try {
            List<Claim> claims = new LinkedList<>();

            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            final Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            boolean tagged = SpawnTagHandler.isTagged(player);
            boolean hasPvPTimer = Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
            
            if (!CustomTimerCreateCommand.isSOTWTimer() && (!tagged && !hasPvPTimer && !(playerTeam == null || playerTeam.getOnlineMembers().size() < 4))) {
                clearPlayer(player);
                return;
            }

            for (Map.Entry<Claim, Team> regionDataEntry : LandBoard.getInstance().getRegionData(player.getLocation(),
                    REGION_DISTANCE, REGION_DISTANCE, REGION_DISTANCE)) {
                Claim claim = regionDataEntry.getKey();
                Team team = regionDataEntry.getValue();

                // Ignore claims if the player is in them.
                // There might become a time where we need to remove this
                // and make it a per-claim-type check, however for now
                // all checks work fine with this here.
                if (claim.contains(player)) {
                    continue;
                }

                if (team.getOwner() == null) {
                    if (team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && tagged) {
                        // If the team is a SAFE_ZONE (IE spawn), they're not inside of it, and they're spawn tagged
                        claims.add(claim);
                    } else if ((team.hasDTRBitmask(DTRBitmask.KOTH) || team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(DTRBitmask.OUTPOST)) && hasPvPTimer) {
                        // If it's an event zone (KOTH or Citadel) and they have a PvP Timer
                        claims.add(claim);
                    } else if (!Foxtrot.getInstance().getMapHandler().isKitMap() && (team.hasDTRBitmask(DTRBitmask.CITADEL) || team.hasDTRBitmask(DTRBitmask.FURY))) {
                        if (playerTeam == null || playerTeam.getOnlineMembers().size() < 4) {
                            claims.add(claim);
                        }
                    }
                } else {
                    if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()) && !team.isMember(player.getUniqueId())) {
                        // If it's an actual claim and the player has a PvP Timer
                        claims.add(claim);
                    } else if (!team.isMember(player.getUniqueId()) && CustomTimerCreateCommand.isSOTWTimer() && team.isClaimLocked()) {
                        claims.add(claim);
                    }
                }
            }

            if (claims.size() == 0) {
                clearPlayer(player);
            } else {
                if (!sentBlockChanges.containsKey(player.getName())) {
                    sentBlockChanges.put(player.getName(), new HashMap<>());
                }

                Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

                // Remove borders after they 'expire' -- This is used to get rid of block changes the player has walked away from,
                // whose value in the map hasn't been updated recently.
                while (bordersIterator.hasNext()) {
                    Map.Entry<Location, Long> border = bordersIterator.next();

                    if (System.currentTimeMillis() >= border.getValue()) {
                        Location loc = border.getKey();

                        if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                            continue;
                        }

                        Block block = loc.getBlock();
                        player.sendBlockChange(loc, block.getType(), block.getData());
                        bordersIterator.remove();
                    }
                }

                for (Claim claim : claims) {
                    sendClaimToPlayer(player, claim);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendClaimToPlayer(Player player, Claim claim) {
        // This gets us all the coordinates on the outside of the claim.
        // Probably could be made better
        for (Coordinate coordinate : claim) {
            Location onPlayerY = new Location(player.getWorld(), coordinate.getX(), player.getLocation().getY(), coordinate.getZ());

            // Ignore an entire pillar if the block closest to the player is further than the max distance (none of the others will be close enough, either)
            if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);

                if (check.getWorld().isChunkLoaded(check.getBlockX() >> 4, check.getBlockZ() >> 4) && check.getBlock().getType().isTransparent() && check.distanceSquared(onPlayerY) < REGION_DISTANCE_SQUARED) {
                    player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14); // Red stained glass
                    sentBlockChanges.get(player.getName()).put(check, System.currentTimeMillis() + 4000L); // The time the glass will stay for if the player walks away
                }
            }
        }
    }

    private static void clearPlayer(Player player) {
        if (sentBlockChanges.containsKey(player.getName())) {
            for (Location changedLoc : sentBlockChanges.get(player.getName()).keySet()) {
                if (!changedLoc.getWorld().isChunkLoaded(changedLoc.getBlockX() >> 4, changedLoc.getBlockZ() >> 4)) {
                    continue;
                }

                Block block = changedLoc.getBlock();
                player.sendBlockChange(changedLoc, block.getType(), block.getData());
            }

            sentBlockChanges.remove(player.getName());
        }
    }

}