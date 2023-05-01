package net.frozenorb.foxtrot.team.claims;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import cc.fyre.neutron.util.PlayerUtil;
import net.frozenorb.foxtrot.team.commands.team.TeamBaseCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamFallTrapCommand;
import net.frozenorb.foxtrot.team.menu.BaseMenu;
import net.frozenorb.foxtrot.team.menu.FallTrapMenu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim.CuboidDirection;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamResizeCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CuboidRegion;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ItemUtils;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class VisualClaim implements Listener {

    public static final int MAP_RADIUS = 50;
    public static final Material[] MAP_MATERIALS = { Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD,
            Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST,
            Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE,
            Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE,
            Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE };
    public static final BlockFace[] NESW_BLOCKS = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };

    @Getter private static Map<String, VisualClaim> currentMaps = new HashMap<>();
    @Getter private static Map<String, VisualClaim> currentSubclaimMaps = new HashMap<>();
    @Getter private static Map<String, VisualClaim> visualClaims = new HashMap<>();

    @Getter @NonNull private Player player;
    @Getter @NonNull private VisualClaimType type;
    @Getter @NonNull private boolean bypass;

    @Getter private List<Location> blockChanges = new ArrayList<>();

    @Getter @Setter private Claim resizing;
    @Getter @Setter private Location corner1;
    @Getter @Setter private Location corner2;

    private static boolean taskSetup = false;
    private static Map<UUID, Queue<QueuedBlockChange>> queuedBlockChanges = Maps.newHashMap();
    private static void checkTaskSetup() {
        if (taskSetup) {
            return;
        }

        taskSetup = true;
        Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            Iterator<Entry<UUID, Queue<QueuedBlockChange>>> entryIterator = queuedBlockChanges.entrySet().iterator();
            while (entryIterator.hasNext()) {
                Entry<UUID, Queue<QueuedBlockChange>> entry = entryIterator.next();

                Player bukkitPlayer = Bukkit.getPlayer(entry.getKey());
                if (bukkitPlayer == null) {
                    entryIterator.remove();
                    continue;
                }

                Queue<QueuedBlockChange> queue = entry.getValue();
                QueuedBlockChange queuedBlockChange = queue.poll();
                if (queuedBlockChange == null) {
                    entryIterator.remove();
                    continue;
                }

                bukkitPlayer.sendBlockChange(queuedBlockChange.getLocation(), queuedBlockChange.getType(), queuedBlockChange.getData());
            }
        }, 1L, 1L);
    }

    private static void sendBlockChange(Player player, Location location, Material type, byte data) {

        if (true) {
            player.sendBlockChange(location, type, data);
            return;
        }

        if (!queuedBlockChanges.containsKey(player.getUniqueId())) {
            queuedBlockChanges.put(player.getUniqueId(), new ConcurrentLinkedQueue<>());
        }

        queuedBlockChanges.get(player.getUniqueId()).add(new QueuedBlockChange(location, type, data));
    }

    public void draw(boolean silent) {
        checkTaskSetup();
        // If they already have a map open and they're opening another
        if (currentMaps.containsKey(player.getName()) && (type == VisualClaimType.MAP || type == VisualClaimType.SURFACE_MAP)) {
            currentMaps.get(player.getName()).cancel();

            if (!silent) {
                if (type == VisualClaimType.MAP) {
                    player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "The surface map has been hidden!");
                }
            }

            return;
        } else if (currentSubclaimMaps.containsKey(player.getName()) && type == VisualClaimType.SUBCLAIM_MAP) { // If they already have a subclaim map open and they're opening another
            currentSubclaimMaps.get(player.getName()).cancel();

            if (!silent) {
                player.sendMessage(ChatColor.YELLOW + "Subclaims have been hidden!");
            }

            return;
        }

        // If they have a visual claim open and this isn't a map (or subclaim map, or surface map), cancel it.
        if (visualClaims.containsKey(player.getName()) && !(type == VisualClaimType.MAP || type == VisualClaimType.SUBCLAIM_MAP || type == VisualClaimType.SURFACE_MAP)) {
            visualClaims.get(player.getName()).cancel();
        }

        // Put this visual claim into the cache
        switch (type) {
            case MAP:
            case SURFACE_MAP:
                currentMaps.put(player.getName(), this);
                break;
            case SUBCLAIM_MAP:
                currentSubclaimMaps.put(player.getName(), this);
                break;
            default:
                visualClaims.put(player.getName(), this);
                break;
        }

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());

        switch (type) {
            case CREATE:
                player.sendMessage(ChatColor.GRAY + "Faction claiming process has begun.");
                player.sendMessage(ChatColor.WHITE + "Left click at a corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.WHITE + "Right click on the second corner of the land you'd like to claim.");
                player.sendMessage(ChatColor.WHITE + "Crouch left click the air to purchase your claim.");
                break;
            case RESIZE:
                player.sendMessage(ChatColor.GRAY + "Faction land resize started.");
                player.sendMessage(ChatColor.WHITE + "Left click in the claim you'd like to resize.");
                player.sendMessage(ChatColor.WHITE + "Right click on the corner you'd like to resize to.");
                player.sendMessage(ChatColor.WHITE + "Crouch left click the air to confirm your resize.");
                break;
            case MAP:
                int claimIteration = 0;
                Map<Map.Entry<Claim, Team>, Material> sendMaps = new HashMap<>();

                for (Map.Entry<Claim, Team> regionData : LandBoard.getInstance().getRegionData(player.getLocation(), MAP_RADIUS, 256, MAP_RADIUS)) {
                    Material mat = getMaterial(claimIteration);
                    claimIteration++;

                    drawClaim(regionData.getKey(), mat);
                    sendMaps.put(regionData, mat);
                }

                if (sendMaps.isEmpty()) {
                    if (!silent) {
                        player.sendMessage(ChatColor.RED + "There are no claims within " + MAP_RADIUS + " blocks of you!");
                    }

                    cancel();
                }

                if (!silent) {
                    for (Map.Entry<Map.Entry<Claim, Team>, Material> mapEntry : sendMaps.entrySet()) {
                        Team team = mapEntry.getKey().getValue();
                        Claim claim = mapEntry.getKey().getKey();

                        if (team.getOwner() == null) {
                            player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + team.getName(player) + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + team.getName(player));
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Land " + ChatColor.BLUE + claim.getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(mapEntry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + team.getName());
                        }
                    }
                }

                break;
            case SURFACE_MAP:
                for (Map.Entry<Claim, Team> regionData : LandBoard.getInstance().getRegionData(player.getLocation(), MAP_RADIUS, 256, MAP_RADIUS)) {
                    Claim claim = regionData.getKey();
                    Team claimOwner = regionData.getValue();
                    World claimWorld = Foxtrot.getInstance().getServer().getWorld(claim.getWorld());

                    for (Coordinate coordinate : claim) {
                        Block block = claimWorld.getBlockAt(coordinate.getX(), 100, coordinate.getZ());
                        boolean displayCarpet = false;

                        for (BlockFace blockFace : NESW_BLOCKS) {
                            Location relative = block.getRelative(blockFace).getLocation();

                            if (!claimOwner.ownsLocation(relative)) {
                                displayCarpet = true;
                                break;
                            }
                        }

                        if (!displayCarpet) {
                            continue;
                        }

                        while (!block.getType().isSolid() || block.getType() == Material.LEAVES || block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                            block = block.getRelative(BlockFace.DOWN);
                        }

                        DyeColor carpetColor;

                        if (claimOwner.getOwner() != null) {
                            if (claimOwner.isMember(player.getUniqueId())) {
                                carpetColor = DyeColor.GREEN;
                            } else if (claimOwner.isAlly(player.getUniqueId())) {
                                carpetColor = DyeColor.BLUE;
                            } else {
                                carpetColor = DyeColor.RED;
                            }
                        } else if (claimOwner.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                            carpetColor = DyeColor.LIME;
                        } else {
                            carpetColor = DyeColor.BLUE;
                        }

                        sendBlockChange(player, block.getLocation(), Material.WOOL, carpetColor.getWoolData());
                        sendBlockChange(player, block.getRelative(BlockFace.UP).getLocation(), Material.CARPET, carpetColor.getWoolData());
                        blockChanges.add(block.getLocation());
                        blockChanges.add(block.getRelative(BlockFace.UP).getLocation());
                    }
                }

                if (blockChanges.size() == 0) {
                    if (!silent) {
                        player.sendMessage(ChatColor.YELLOW + "There are no claims near you!");
                    }

                    cancel();
                    return;
                }

                if (!silent) {
                    player.sendMessage(ChatColor.YELLOW + "Claims have been shown.");
                }

                break;
            case SUBCLAIM_MAP:
                Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

                if (bypass) {
                    senderTeam = LandBoard.getInstance().getTeam(player.getLocation());
                }

                if (senderTeam == null) {
                    if (!silent) {
                        player.sendMessage(ChatColor.RED + "You must be on a team to view subclaims.");
                    }

                    cancel();
                    return;
                }

                int subclaimIteration = 0;
                Map<Subclaim, Material> subclaimMaterialMap = new HashMap<>();

                for (Subclaim subclaim : senderTeam.getSubclaims()) {
                    if (subclaim.getLoc1().distanceSquared(player.getLocation()) > MAP_RADIUS * MAP_RADIUS && subclaim.getLoc2().distanceSquared(player.getLocation()) > MAP_RADIUS * MAP_RADIUS) {
                        continue;
                    }

                    Material mat = getMaterial(subclaimIteration);
                    subclaimIteration++;

                    subclaimMaterialMap.put(subclaim, mat);
                    drawSubclaim(subclaim, mat);
                }

                if (subclaimIteration == 0) {
                    if (!silent) {
                        player.sendMessage(ChatColor.YELLOW + "There are no subclaims within " + MAP_RADIUS + " blocks of you!");
                    }

                    cancel();
                    return;
                }

                if (!silent) {
                    for (Map.Entry<Subclaim, Material> entry : subclaimMaterialMap.entrySet()) {
                        player.sendMessage(ChatColor.YELLOW + "Subclaim " + ChatColor.BLUE + entry.getKey().getName() + ChatColor.GREEN + "(" + ChatColor.AQUA + ItemUtils.getName(new ItemStack(entry.getValue())) + ChatColor.GREEN + ") " + ChatColor.YELLOW + "is claimed by " + ChatColor.BLUE + senderTeam.getName());
                    }
                }

                break;
        }
    }

    public boolean containsOtherClaim(Claim claim) {
        Location maxPoint = claim.getMaximumPoint();
        Location minPoint = claim.getMinimumPoint();
        Team maxTeam = LandBoard.getInstance().getTeam(maxPoint);

        if (maxTeam != null && (type != VisualClaimType.RESIZE || !maxTeam.isMember(player.getUniqueId()))) {
            return (true);
        }

        Team minTeam = LandBoard.getInstance().getTeam(minPoint);

        if (minTeam != null && (type != VisualClaimType.RESIZE || !minTeam.isMember(player.getUniqueId()))) {
            return (true);
        }

        // A Claim doesn't like being iterated when either its X or Z is 0.
        if (Math.abs(claim.getX1() - claim.getX2()) == 0 || Math.abs(claim.getZ1() - claim.getZ2()) == 0) {
            return (false);
        }

        for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
            for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                Location at = new Location(Foxtrot.getInstance().getServer().getWorld(claim.getWorld()), x, 80, z);
                Team teamAt = LandBoard.getInstance().getTeam(at);

                if (teamAt != null && (type != VisualClaimType.RESIZE || !teamAt.isMember(player.getUniqueId()))) {
                    return (true);
                }
            }
        }

        return (false);
    }

    public Set<Claim> getTouchingClaims(Claim claim) {
        Set<Claim> touchingClaims = new HashSet<>();

        for (Coordinate coordinate : claim.outset(CuboidDirection.Horizontal, 1)) {
            Location loc = new Location(Foxtrot.getInstance().getServer().getWorld(claim.getWorld()), coordinate.getX(), 80, coordinate.getZ());
            Map.Entry<Claim, Team> claimAtLocation = LandBoard.getInstance().getRegionData(loc);

            if (claimAtLocation != null) {
                touchingClaims.add(claimAtLocation.getKey());
            }
        }

        return (touchingClaims);
    }

    public void setLoc(int locationId, Location clicked) {
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to " + type.name().toLowerCase() + " land!");
            cancel();
            return;
        }

        if (type == VisualClaimType.CREATE) {
            if (!bypass && !Foxtrot.getInstance().getServerHandler().isUnclaimed(clicked)) {
                player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
                return;
            }

            if (locationId == 1) {
                if (corner2 != null && isIllegalClaim(new Claim(clicked, corner2), null)) {
                    return;
                }

                clearPillarAt(corner1);
                this.corner1 = clicked;
            } else if (locationId == 2) {
                if (corner1 != null && isIllegalClaim(new Claim(corner1, clicked), null)) {
                    return;
                }

                clearPillarAt(corner2);
                this.corner2 = clicked;
            }

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), new BukkitRunnable() {

                @Override
                public void run() {
                    erectPillar(clicked, Material.EMERALD_BLOCK);
                }

            }, 1L);

            player.sendMessage(ChatColor.YELLOW + "Set claim's location " + ChatColor.LIGHT_PURPLE + locationId + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");

            if (corner1 != null && corner2 != null) {
                int price = Claim.getPrice(new Claim(corner1, corner2), playerTeam, true);

                int x = Math.abs(corner1.getBlockX() - corner2.getBlockX());
                int z = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());

                if (price > playerTeam.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.RED + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Claim cost: " + ChatColor.GREEN + "$" + price + ChatColor.YELLOW + ", Current size: (" + ChatColor.WHITE + x + ", " + z + ChatColor.YELLOW + "), " + ChatColor.WHITE + (x * z) + ChatColor.YELLOW + " blocks");
                }
            }
        } else if (type == VisualClaimType.BASE || type == VisualClaimType.FALL_TRAP) {
            Team base = LandBoard.getInstance().getTeam(clicked);
            if (base == null || !base.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You can only generate your " + (type == VisualClaimType.FALL_TRAP ? "falltrap" : "base") + " in your own claim!");
                return;
            }

            if (type.equals(VisualClaimType.FALL_TRAP) && getCorner1() != null && getCorner2() != null) {

                final Location cloned = getCorner1().clone();

                cloned.setY(0);
                final Location cloned2 = getCorner2().clone();
                cloned2.setY(0);

                int distance = (int) Math.ceil(cloned.distance(cloned2));

                if (distance > 25) {
                    player.sendMessage(ChatColor.RED + "Fallers can only have a distance of 25 blocks! Right now the distance is " + distance);
                    return;
                }
            }

            if (locationId == 1) {
                clearPillarAt(corner1);
                this.corner1 = clicked;
            } else if (locationId == 2) {
                clearPillarAt(corner2);
                this.corner2 = clicked;
            }

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                    erectPillar(clicked, type == VisualClaimType.FALL_TRAP ? Material.BEDROCK : Material.GOLD_BLOCK);
                }
            }, 1L);

            player.sendMessage(ChatColor.YELLOW + "Set " + (type == VisualClaimType.FALL_TRAP ? "falltrap" : "base") + "'s location " + ChatColor.LIGHT_PURPLE + locationId + ChatColor.YELLOW + " to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
        } else if (type == VisualClaimType.RESIZE) {
            Map.Entry<Claim, Team> teamAtLocation = LandBoard.getInstance().getRegionData(clicked);

            if (locationId == 1) {
                if (teamAtLocation == null || !teamAtLocation.getValue().isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "To resize your claim, please left click in the claim you'd like to resize.");
                    return;
                }

                resizing = teamAtLocation.getKey();
                drawClaim(resizing, Material.LAPIS_BLOCK);
            } else if (locationId == 2) {
                if (resizing == null) {
                    player.sendMessage(ChatColor.YELLOW + "Before you set the location you'd like to resize to, first left click in the claim you'd like to resize.");
                    return;
                }

                final Claim claimClone = new Claim(resizing);

                applyResize(claimClone, clicked);

                if (isIllegalClaim(claimClone, Arrays.asList(resizing, claimClone))) {
                    return;
                }

                this.corner2 = clicked;

                new BukkitRunnable() {

                    public void run() {
                        clearAllBlocks();
                        drawClaim(resizing, Material.LAPIS_BLOCK);
                        drawClaim(claimClone, Material.EMERALD_BLOCK);
                    }

                }.runTaskLater(Foxtrot.getInstance(), 1L);
            }

            if (locationId == 1) {
                player.sendMessage(ChatColor.YELLOW + "Selected claim " + ChatColor.LIGHT_PURPLE + teamAtLocation.getKey().getName() + ChatColor.YELLOW + " to resize.");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Set resize location to " + ChatColor.GREEN + "(" + ChatColor.WHITE + clicked.getBlockX() + ", " + clicked.getBlockY() + ", " + clicked.getBlockZ() + ChatColor.GREEN + ")" + ChatColor.YELLOW + ".");
            }

            if (resizing != null && corner2 != null) {
                int oldPrice = Claim.getPrice(resizing, null, false);
                Claim preview = new Claim(resizing);

                applyResize(preview, corner2);

                int newPrice = Claim.getPrice(preview, null, false);
                int cost = newPrice - oldPrice;

                if (cost > playerTeam.getBalance() && !bypass) {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.RED + "$" + cost);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.GREEN + "$" + cost);
                }
            }
        }
    }

    public void cancel() {
        if (type == VisualClaimType.CREATE || type == VisualClaimType.RESIZE || type == VisualClaimType.BASE || type == VisualClaimType.FALL_TRAP) {
            player.getInventory().remove(TeamClaimCommand.SELECTION_WAND);
            player.getInventory().remove(TeamResizeCommand.SELECTION_WAND);
            player.getInventory().remove(TeamBaseCommand.SELECTION_WAND);
            player.getInventory().remove(TeamFallTrapCommand.SELECTION_WAND);
        }

        HandlerList.unregisterAll(this);

        switch (type) {
            case MAP:
            case SURFACE_MAP:
                currentMaps.remove(player.getName());
                break;
            case SUBCLAIM_MAP:
                currentSubclaimMaps.remove(player.getName());
                break;
            default:
                visualClaims.remove(player.getName());
                break;
        }

        clearAllBlocks();
    }

    public void clearAllBlocks() {
        for (Location location : blockChanges) {
            sendBlockChange(player, location, location.getBlock().getType(), location.getBlock().getData());
        }
    }

    public void generateBase() {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to generate base!");
            cancel();
            return;
        }

        if (corner1 != null && corner2 != null) {
            if (!bypass) {
                if (!team.isCaptain(player.getUniqueId()) && !team.isCoLeader(player.getUniqueId()) && !team.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                    return;
                }
            }

            Claim claim = new Claim(corner1, corner2);

            new BaseMenu(team, claim).openMenu(player);

            cancel();

            new BukkitRunnable() {

                @Override
                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(Foxtrot.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your base yet!");
        }
    }

    public void generateFallTrap() {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to generate falltrap!");
            cancel();
            return;
        }

        if (corner1 != null && corner2 != null) {
            if (!bypass) {
                if (!team.isCaptain(player.getUniqueId()) && !team.isCoLeader(player.getUniqueId()) && !team.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                    return;
                }
            }

            new FallTrapMenu(team, new Claim(corner1, corner2)).openMenu(player);

            cancel();

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }
            }.runTaskLater(Foxtrot.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your falltrap yet!");
        }
    }

    public void purchaseClaim() {
        player.chat("/f d all");

        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
            cancel();
            return;
        }

        if (corner1 != null && corner2 != null) {
            int price = Claim.getPrice(new Claim(corner1, corner2), playerTeam, true);

            if (!bypass) {
                if (playerTeam.getClaims().size() >= Team.MAX_CLAIMS) {
                    player.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is " + Team.MAX_CLAIMS + ".");
                    return;
                }

                if (!playerTeam.isCaptain(player.getUniqueId()) && !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                    return;
                }

                if (playerTeam.getBalance() < price) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (playerTeam.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                    return;
                }
            }

            Claim claim = new Claim(corner1, corner2);

            if (isIllegalClaim(claim, null) || Double.isNaN(playerTeam.getBalance())) {
                return;
            }

            claim.setName(playerTeam.getName() + "_" + (100 + ThreadLocalRandom.current().nextInt(800)));
            claim.setY1(0);
            claim.setY2(256);

            LandBoard.getInstance().setTeamAt(claim, playerTeam);
            playerTeam.getClaims().add(claim);
            playerTeam.flagForSave();
            playerTeam.recalculateSpawnersInClaims();

            Foxtrot.getInstance().getQuestHandler().completeQuest(player, "Claim");

            player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your team!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

            PlayerUtil.sendTitle(player, "&a&lClaim Success", "Set your team's home by typing /f sethome!");

            if (!bypass) {
                playerTeam.setBalance(playerTeam.getBalance() - price);
                Foxtrot.getInstance().getLogger().info("Economy Logger: Withdrawing " + price + " from " + playerTeam.getBalance() + "'s account: Claimed land");
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$" + (int) playerTeam.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + price + ")");
            }

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamActionTracker.logActionAsync(playerTeam, TeamActionType.PLAYER_CLAIM_LAND, ImmutableMap.of(
                    "playerId", player.getUniqueId(),
                    "playerName", playerTeam.getName(),
                    "cost", price,
                    "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            cancel();

            new BukkitRunnable() {

                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(Foxtrot.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    public void resizeClaim() {
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (playerTeam == null) {
            player.sendMessage(ChatColor.RED + "You have to be on a team to resize land!");
            cancel();
            return;
        }

        if (resizing != null && corner2 != null) {
            Claim newClaim = new Claim(resizing);
            applyResize(newClaim, corner2);

            int oldPrice = Claim.getPrice(resizing, null, false);
            int newPrice = Claim.getPrice(newClaim, null, false);
            int cost = newPrice - oldPrice;

            if (!bypass) {
                if (!playerTeam.isCaptain(player.getUniqueId()) && !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "Only team captains can resize land.");
                    return;
                }

                if (playerTeam.getBalance() < cost) {
                    player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                    return;
                }

                if (playerTeam.isRaidable()) {
                    player.sendMessage(ChatColor.RED + "You cannot resize land while raidable.");
                    return;
                }
            }

            if (isIllegalClaim(newClaim, null)) {
                return;
            }

            LandBoard.getInstance().setTeamAt(resizing, null);
            LandBoard.getInstance().setTeamAt(newClaim, playerTeam);
            playerTeam.getClaims().remove(resizing);
            playerTeam.getClaims().add(newClaim);
            playerTeam.flagForSave();
            playerTeam.recalculateSpawnersInClaims();

            player.sendMessage(ChatColor.YELLOW + "You have resized this land!");

            if (!bypass) {
                playerTeam.setBalance(playerTeam.getBalance() - cost);
                player.sendMessage(ChatColor.YELLOW + "Your team's new balance is " + ChatColor.WHITE + "$" + (int) playerTeam.getBalance() + ChatColor.LIGHT_PURPLE + " (Price: $" + cost + ")");
            }

            Location minLoc = resizing.getMinimumPoint();
            Location maxLoc = resizing.getMaximumPoint();

            TeamActionTracker.logActionAsync(playerTeam, TeamActionType.PLAYER_RESIZE_LAND, ImmutableMap.of(
                    "playerId", player.getUniqueId(),
                    "playerName", playerTeam.getName(),
                    "cost", cost,
                    "newPoint1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "newPoint2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            cancel();

            new BukkitRunnable() {

                public void run() {
                    if (VisualClaim.getCurrentMaps().containsKey(player.getName())) {
                        VisualClaim.getCurrentMaps().get(player.getName()).cancel();
                    }

                    new VisualClaim(player, VisualClaimType.MAP, false).draw(true);
                }

            }.runTaskLater(Foxtrot.getInstance(), 1L);
        } else {
            player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
        }
    }

    private void drawClaim(Claim claim, Material material) {
        for (Location loc : claim.getCornerLocations()) {
            erectPillar(loc, material);
        }
    }

    private void drawSubclaim(Subclaim subclaim, Material material) {
        CuboidRegion cuboidRegion = new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2());
        int glassIteration = 0;

        for (Location location : cuboidRegion) {
            int matches = 0;

            if (location.getBlockX() == cuboidRegion.getMinimumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockX() == cuboidRegion.getMaximumPoint().getBlockX()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMinimumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockY() == cuboidRegion.getMaximumPoint().getBlockY()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMinimumPoint().getBlockZ()) {
                matches++;
            }

            if (location.getBlockZ() == cuboidRegion.getMaximumPoint().getBlockZ()) {
                matches++;
            }

            if (matches >= 2) {
                if (glassIteration++ % 3 == 0) {
                    sendBlockChange(player, location, material, (byte) 0);
                } else {
                    sendBlockChange(player, location, Material.GLASS, (byte) 0);
                }

                blockChanges.add(location.clone());
            }
        }
    }

    private void erectPillar(Location loc, Material mat) {
        Location set = loc.clone();

        for (int y = 0; y < 256; y++) {
            set.setY(y);

            if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent() || set.getBlock().getType() == Material.WATER || set.getBlock().getType() == Material.STATIONARY_WATER) {
                if (y % 5 == 0) {
                    sendBlockChange(player, set, mat, (byte) 0);
                } else {
                    sendBlockChange(player, set, Material.GLASS, (byte) 0);
                }

                blockChanges.add(set.clone());
            }
        }
    }

    private void clearPillarAt(Location location) {
        if (location == null) {
            return;
        }

        Iterator<Location> blockChangeIterator = blockChanges.iterator();

        while (blockChangeIterator.hasNext()) {
            Location blockChange = blockChangeIterator.next();

            if (blockChange.getBlockX() == location.getBlockX() && blockChange.getBlockZ() == location.getBlockZ()) {
                sendBlockChange(player, blockChange, blockChange.getBlock().getType(), blockChange.getBlock().getData());
                blockChangeIterator.remove();
            }
        }
    }

    public boolean isIllegalClaim(Claim claim, List<Claim> ignoreNearby) {
        if (bypass) {
            return (false);
        }

        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (containsOtherClaim(claim)) {
            player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
            return (true);
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
            return (true);
        }

        Set<Claim> touchingClaims = getTouchingClaims(claim);
        Iterator<Claim> teamClaims = touchingClaims.iterator();
        boolean removedSelfClaims = false;

        while (teamClaims.hasNext()) {
            Claim possibleClaim = teamClaims.next();

            if (ignoreNearby != null && ignoreNearby.contains(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            } else if (playerTeam.ownsClaim(possibleClaim)) {
                removedSelfClaims = true;
                teamClaims.remove();
            }
        }

        if (playerTeam.getClaims().size() != (type == VisualClaimType.RESIZE ? 1 : 0) && !removedSelfClaims) {
            player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
            return (true);
        }

        if (touchingClaims.size() > 1 || (touchingClaims.size() == 1 && !removedSelfClaims)) {
            player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
            return (true);
        }

        int x = Math.abs(claim.getX1() - claim.getX2());
        int z = Math.abs(claim.getZ1() - claim.getZ2());

        int limit = player.hasPermission("challenge.claim") ? 2 : 5;

        if (x < limit || z < limit) {
            player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least " + (limit+1) + " x " + (limit+1) + "!");
            return (true);
        }

        if (x > 3 * z || z > 3 * x) {
            player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
            return (true);
        }

        return (false);
    }

    public void applyResize(Claim claim, Location location) {
        double furthestDistance = 0D;
        Location furthestCorner = null;

        for (Location corner : claim.getCornerLocations()) {
            double distance = location.distanceSquared(corner);

            if (furthestCorner == null || distance > furthestDistance) {
                furthestDistance = distance;
                furthestCorner = corner;
            }
        }

        claim.setLocations(location, furthestCorner);
        claim.setY1(0);
        claim.setY2(256);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == player && player.getItemInHand() != null) {
            if (player.getItemInHand().getType() == TeamClaimCommand.SELECTION_WAND.getType() && type == VisualClaimType.CREATE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the claiming process.");
                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            purchaseClaim();
                        }

                        break;
                }

                event.setCancelled(true);
            } else if (player.getItemInHand().isSimilar(TeamBaseCommand.SELECTION_WAND) && type.equals(VisualClaimType.BASE)) {
                event.setCancelled(true);

                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the base process.");

                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            generateBase();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            generateBase();
                        }
                        break;
                }
            } else if (player.getItemInHand().isSimilar(TeamFallTrapCommand.SELECTION_WAND) && type == VisualClaimType.FALL_TRAP) {
                event.setCancelled(true);

                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the falltrap process.");

                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            generateFallTrap();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            generateFallTrap();
                        }
                        break;
                }
            } else if (player.getItemInHand().getType() == TeamResizeCommand.SELECTION_WAND.getType() && type == VisualClaimType.RESIZE) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                        setLoc(2, event.getClickedBlock().getLocation());
                        break;
                    case RIGHT_CLICK_AIR:
                        cancel();
                        player.sendMessage(ChatColor.RED + "You have cancelled the resizing process.");

                        break;
                    case LEFT_CLICK_BLOCK:
                        if (player.isSneaking()) {
                            resizeClaim();
                        } else {
                            setLoc(1, event.getClickedBlock().getLocation());
                        }

                        break;
                    case LEFT_CLICK_AIR:
                        if (player.isSneaking()) {
                            resizeClaim();
                        }

                        break;
                }

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (player == event.getPlayer()) {
            cancel();
        }
    }

    public Material getMaterial(int iteration) {
        if (iteration == -1) {
            return (Material.IRON_BLOCK);
        }

        while (iteration >= MAP_MATERIALS.length) {
            iteration = iteration - MAP_MATERIALS.length;
        }

        return (MAP_MATERIALS[iteration]);
    }

    public static VisualClaim getVisualClaim(String name) {
        return (visualClaims.get(name));
    }

    @AllArgsConstructor @Getter
    private static class QueuedBlockChange {
        private Location location;
        private Material type;
        private byte data;
    }

}