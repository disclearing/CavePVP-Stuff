package net.frozenorb.foxtrot.team;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.serialization.LocationSerializer;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.EndOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.NetherOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.RoadOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.kitmap.KitmapOutpost;
import net.minecraft.util.com.google.common.collect.ImmutableMap;

import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryCapZone;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.persist.maps.KillsMap;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.team.upgrade.UpgradeType;
import net.frozenorb.foxtrot.util.CuboidRegion;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.material.TrapDoor;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Team {

    // Constants //
    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
    public static final String DARK_GRAY_LINE = ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
    public static final ChatColor ALLY_COLOR = ChatColor.BLUE;
    public static final int MAX_CLAIMS = 2;
    public static final int MAX_FORCE_INVITES = 5;
    @Getter @Setter private LCWaypoint homeWaypoint;
    @Getter @Setter private LCWaypoint focusWaypoint;
    @Getter @Setter private LCWaypoint rallyWaypoint;

    // Internal //
    @Getter private boolean needsSave = false;
    @Getter private boolean loading = false;

    // Persisted //
    @Getter @Setter private ObjectId uniqueId;
    @Getter private String name;
    @Getter private Location HQ;
    @Getter @Setter private ObjectId raidStealTeam;
    @Getter private double balance;
    @Getter @Setter private int baseTokens;
    @Getter @Setter private int fallTrapTokens;
    @Getter private double DTR;
    @Getter @Setter private int trapDoors;
    @Getter private long DTRCooldown;
    @Getter private List<Claim> claims = new ArrayList<>();
    @Getter private List<Subclaim> subclaims = new ArrayList<>();
    @Getter private UUID owner = null;
    @Getter private Set<UUID> members = new HashSet<>();
    @Getter private Set<UUID> captains = new HashSet<>();
    @Getter private Set<UUID> coleaders = new HashSet<>();
    @Getter private Set<UUID> invitations = new HashSet<>();
    @Getter private Map<UUID, Role> roster = new HashMap<>();
    @Getter private Set<ObjectId> allies = new HashSet<>();
    @Getter private Set<ObjectId> requestedAllies = new HashSet<>();
    @Getter private List<PotionEffectType> purchasedEffects = new ArrayList<>();
    @Getter private List<UpgradeType> purchasedUpgrades = new ArrayList<>();
    @Getter private String announcement;
    @Getter private int maxOnline = -1;
    @Getter private boolean powerFaction = false;
    @Getter private int lives = 0;
    @Getter private int kills = 0;
    @Getter @Setter private int addedGems = 0;
    @Getter private int removedGems = 0;
    @Getter private int kothCaptures = 0;
    @Getter private int miniKothCaptures = 0;
    @Getter private int furysCaptured = 0;
    @Getter private int doublePoints = 0;
    @Getter private int doubleKillPoints = 0;
    @Getter private int addedPoints = 0;
    @Getter @Setter private int trappingPoints = 0;
    @Getter @Setter private int playtimeGems = 0;
    @Getter private int points = 0;
    @Getter @Setter private int gems = 0;
    @Getter private int removedPoints = 0;
    @Getter @Setter private int factionsMadeRaidable = 0;
    @Getter private int citadelsCapped = 0;
    @Getter private int conquestsCapped = 0;
    @Getter private int spawnersInClaim = 0;
    @Getter private int diamondsMined = 0;
    @Getter private int deaths = 0;
    @Getter private boolean claimLocked = false;
    @Getter private boolean eotwCapped = false;
    @Getter @Setter private Location rallyPoint = null;

    public void setRally(Location location) {

    }

    @Getter private int forceInvites = MAX_FORCE_INVITES;
    @Getter private Set<UUID> historicalMembers = new HashSet<>(); // this will store all players that were once members

    // Not persisted //
    @Getter @Setter private UUID focused;
    @Getter @Setter private Team focusedTeam;
    @Getter @Setter private long lastRequestReport;
    @Getter @Setter private Player rallyPlayer;
    
    @Getter @Setter private int bards;
    @Getter @Setter private int archers;
    @Getter @Setter private int rogues;

    public Team(String name) {
        this.name = name;
    }

    public int getDeathsTilRaidable() {
        return (int) Math.ceil(getDTR());
    }

    public void setDTR(double newDTR) {
        setDTR(newDTR, null);
    }

    public void setDTR(double newDTR, Player actor) {
        if (DTR == newDTR) {
            return;
        }

        if (DTR <= 0 && newDTR > 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NO_LONGER_RAIDABLE, ImmutableMap.of());
        }

        if (0 < DTR && newDTR <= 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, actor == null ? ImmutableMap.of() : ImmutableMap.of("actor", actor.getName()));
        }

        if (!isLoading()) {
            if (actor != null) {
                Foxtrot.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + ChatColor.STRIKETHROUGH + "--" + ">" + newDTR + ". Actor: " + actor.getName());
            } else {
                Foxtrot.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + ChatColor.STRIKETHROUGH + "--" + ">" + newDTR);
            }
        }

        this.DTR = newDTR;
        flagForSave();

        this.getOnlineMembers().forEach(LunarClientListener::updateNametag);
    }

    public void setName(String name) {
        this.name = name;
        flagForSave();
    }

    public String getName(Player player) {
        if (name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {
            return ChatColor.GOLD + "Glowstone Mountain"; // override team name
        } else if (owner == null) {
            if (hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) && name.equalsIgnoreCase("EndPortal")) {
                return ChatColor.DARK_PURPLE + "End Portal";
            }
            if (getName().equalsIgnoreCase("TreasureCove") && hasDTRBitmask(DTRBitmask.MOUNTAIN)) {
                return ChatColor.GOLD + "Treasure Cove";
            }
            if (hasDTRBitmask(DTRBitmask.OUTPOST)) {
                if (getName().startsWith("Road")) {
                    return (ChatColor.GOLD + "Road Outpost");
                }
                if (getName().startsWith("Nether")) {
                    return (ChatColor.DARK_RED + "Nether Outpost");
                }
                if (getName().startsWith("End")) {
                    return (ChatColor.DARK_PURPLE + "End Outpost");
                }
                return ChatColor.GOLD + "Outpost";
            }
            if (name.startsWith("Fury")) {
                final FuryCapZone furyCapZone = FuryCapZone.valueOf(this.name.replace("Fury", "").toUpperCase());

                if (furyCapZone == null) {
                    return ChatColor.RED + this.name.replace("Fury", " ");
                }

                return furyCapZone.getChatColor() + "Fury " + furyCapZone.getDisplayName();
            }
            if (hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                switch (player.getWorld().getEnvironment()) {
                    case NETHER:
                        return (ChatColor.GREEN + "Nether Spawn");
                    case THE_END:
                        return (ChatColor.GREEN + "The End Safezone");
                }

                if (player.getWorld().getName().equalsIgnoreCase("world") && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
                    return ChatColor.GREEN + "Safe-Zone";
                }

                return (ChatColor.GREEN + "Spawn");
            } else if (name.equalsIgnoreCase("Hell") & hasDTRBitmask(DTRBitmask.KOTH)) {
                return ChatColor.RED + "Hell KOTH";
            } else if (name.equalsIgnoreCase("EOTW") && hasDTRBitmask(DTRBitmask.KOTH)) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "EOTW";
            } else if (name.equalsIgnoreCase("End") && hasDTRBitmask(DTRBitmask.KOTH)) {
                return ChatColor.DARK_PURPLE + "End KOTH";
            } else if (hasDTRBitmask(DTRBitmask.KOTH)) {
                return (ChatColor.BLUE + getName() + " KOTH");
            } else if (hasDTRBitmask(DTRBitmask.CITADEL) && name.equalsIgnoreCase("NetherCitadel")) {
                return ChatColor.DARK_RED + "Nether Citadel";
            } else if (hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (ChatColor.DARK_PURPLE + "Citadel");
            } else if (hasDTRBitmask(DTRBitmask.ROAD)) {
                return (ChatColor.GOLD + getName().replace("Road", " Road"));
            } else if (hasDTRBitmask(DTRBitmask.CONQUEST)) {
                return (ChatColor.BLUE + "Conquest");
            } else if (name.equalsIgnoreCase("warzone")) {
                return (ChatColor.DARK_RED + "WarZone");
            }
        }

        if (isMember(player.getUniqueId())) {
            return (ChatColor.GREEN + getName());
        } else if (isAlly(player.getUniqueId())) {
            return (Team.ALLY_COLOR + getName());
        }



        final Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (targetTeam != null && targetTeam.getFocusedTeam() == this) {
            return ChatColor.LIGHT_PURPLE + getName();
        }

        return ChatColor.RED + getName();
    }

    public void addMember(UUID member) {
        if (members.add(member)) {
            historicalMembers.add(member);

            if (this.loading) return;
            TeamActionTracker.logActionAsync(this, TeamActionType.PLAYER_JOINED, ImmutableMap.of(
                    "playerId", member
            ));

            flagForSave();
        }
    }

    public void addCaptain(UUID captain) {
        if (captains.add(captain) && !this.isLoading()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CAPTAIN, ImmutableMap.of(
                    "playerId", captain
            ));

            flagForSave();
        }
    }

    public void addCoLeader(UUID co) {
        if (coleaders.add(co) && !this.isLoading()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CO_LEADER, ImmutableMap.of(
                    "playerId", co
            ));

            flagForSave();
        }
    }

    public void setBalance(double balance) {
        this.balance = balance;
        flagForSave();
    }

    public void setDTRCooldown(long dtrCooldown) {
        this.DTRCooldown = dtrCooldown;
        flagForSave();
    }

    public void removeCaptain(UUID captain) {
        if (captains.remove(captain)) {
            TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CAPTAIN, ImmutableMap.of(
                    "playerId", captain
            ));

            flagForSave();
        }
    }

    public void removeCoLeader(UUID co) {
        if (coleaders.remove(co)) {
            TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CO_LEADER, ImmutableMap.of(
                    "playerId", co
            ));

            flagForSave();
        }
    }

    public void setOwner(UUID owner) {
        this.owner = owner;

        if (owner != null) {
            members.add(owner);
            coleaders.remove(owner);
            captains.remove(owner);
        }

        if (this.loading) return;

        if (owner != null) {
            TeamActionTracker.logActionAsync(this, TeamActionType.LEADER_CHANGED, ImmutableMap.of(
                    "playerId", owner
            ));
        }

        flagForSave();
    }

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
        flagForSave();
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.ANNOUNCEMENT_CHANGED, ImmutableMap.of(
                "newAnnouncement", announcement
        ));

        flagForSave();
    }

    public void setHQ(Location hq) {
        String oldHQ = this.HQ == null ? "None" : (getHQ().getBlockX() + ", " + getHQ().getBlockY() + ", " + getHQ().getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        this.HQ = hq;

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.HEADQUARTERS_CHANGED, ImmutableMap.of(
                "oldHq", oldHQ,
                "newHq", newHQ
        ));

        flagForSave();
    }

    public void setPowerFaction( boolean bool ) {
        this.powerFaction = bool;
        if( bool ) {
            TeamHandler.addPowerFaction(this);
        } else {
            TeamHandler.removePowerFaction(this);
        }

        if (this.loading) return;
        TeamActionTracker.logActionAsync(this, TeamActionType.POWER_FAC_STATUS_CHANGED, ImmutableMap.of(
                "powerFaction", bool
        ));

        flagForSave();
    }

    public void setLives( int lives ) {
        this.lives = lives;
        flagForSave();
    }

    public void addLives( int lives ) {
        if( lives < 0 ) {
            return;
        }
        this.lives += lives;
        flagForSave();
    }

    public void removeLives( int lives ) {
        if( this.lives < lives || lives < 0) {
            return;
        }
        this.lives -= lives;
        flagForSave();
    }

    public void disband() {
        try {
            if (owner != null) {
                double refund = balance;

                for (Claim claim : claims) {
                    refund += Claim.getPrice(claim, this, false);
                }

                Foxtrot.getInstance().getEconomyHandler().deposit(owner, refund);
                Foxtrot.getInstance().getWrappedBalanceMap().setBalance(owner, Foxtrot.getInstance().getEconomyHandler().getBalance(owner));
                Foxtrot.getInstance().getLogger().info("Economy Logger: Depositing " + refund + " into " + UUIDUtils.name(owner) + "'s account: Disbanded team");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ObjectId allyId : getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                ally.getAllies().remove(getUniqueId());
            }
        }

        for (UUID uuid : members) {
            Foxtrot.getInstance().getChatModeMap().setChatMode(uuid, ChatMode.PUBLIC);
        }

        Foxtrot.getInstance().getTeamHandler().getTeams().stream().filter(it -> it.getFocusedTeam() == this).forEach(it -> it.setFocusedTeam(null));
        Foxtrot.getInstance().getTeamHandler().removeTeam(this);
        LandBoard.getInstance().clear(this);

        new BukkitRunnable() {

            public void run() {
                Proton.getInstance().runRedisCommand(redis -> {
                    redis.del("fox_teams." + name.toLowerCase());
                    return (null);
                });

                DBCollection teamsCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Teams");
                teamsCollection.remove(getJSONIdentifier());
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());

        needsSave = false;
    }

    public List<Outpost> findControlledOutposts() {
        return Foxtrot.getInstance().getOutpostHandler().findControllingOutposts(this);
    }

    public boolean hasEndOutpost() {
        return this.findControlledOutposts().stream().anyMatch(it -> it instanceof EndOutpost);
    }

    public boolean hasNetherOutpost() {
        return this.findControlledOutposts().stream().anyMatch(it -> it instanceof NetherOutpost);
    }

    public boolean hasRoadOutpost() {
        return this.findControlledOutposts().stream().anyMatch(it -> it instanceof RoadOutpost);
    }

    public boolean hasKitsOutpost() {
        return this.findControlledOutposts().stream().anyMatch(it -> it instanceof KitmapOutpost);
    }

    public void rename(String newName) {
        final String oldName = name;

        Foxtrot.getInstance().getTeamHandler().removeTeam(this);

        this.name = newName;

        Foxtrot.getInstance().getTeamHandler().setupTeam(this);

        Proton.getInstance().runRedisCommand(redis -> {
            redis.del("fox_teams." + oldName.toLowerCase());
            return (null);
        });

        // We don't need to do anything here as all we're doing is changing the name, not the Unique ID (which is what Mongo uses)
        // therefore, Mongo will be notified of this once the 'flagForSave()' down below gets processed.

        for (Claim claim : getClaims()) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        getOnlineMembers().forEach(LunarClientListener::updateNametag);

        flagForSave();
    }

    public void setForceInvites(int forceInvites) {
        this.forceInvites = forceInvites;
        this.flagForSave();
    }

    public void setKills(int kills) {
        this.kills = kills;
        this.recalculatePoints();
        this.recalculateGems();

        this.flagForSave();
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
        this.recalculatePoints();
        this.recalculateGems();
        this.flagForSave();
    }
    
    public void setKothCaptures(int kothCaptures) {
        this.kothCaptures = kothCaptures;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setMiniKothCaptures(int miniKothCaptures) {
        this.miniKothCaptures = miniKothCaptures;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void addPlaytimeGems(int playtimePoints) {
        this.playtimeGems = playtimePoints;
        this.recalculateGems();
        this.flagForSave();
    }

    public void setRemovedGems(int removedGems) {
        this.removedGems = removedGems;
        this.recalculateGems();
        this.flagForSave();
    }

    public void setDiamondsMined(int diamondsMined) {
        this.diamondsMined = diamondsMined;
        this.flagForSave();
    }

    public void setCitadelsCapped(int citadels) {
        this.citadelsCapped = citadels;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setClaimLocked(boolean claimLocked) {
        this.claimLocked = claimLocked;
        this.flagForSave();
    }

    public void setEotwCapped(boolean eotwCapped) {
        this.eotwCapped = eotwCapped;
        this.flagForSave();
    }

    public void setConquestsCapped(int conquestsCapped) {
        this.conquestsCapped = conquestsCapped;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setFurysCaptured(int furysCaptured) {
        this.furysCaptured = furysCaptured;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setAddedPoints(int addedPoints) {
        this.addedPoints = addedPoints;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setPoints(int points) {
        this.points = points;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setRemovedPoints(int removedPoints) {
        this.removedPoints = removedPoints;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void setDoublePoints(int doublePoints) {
        this.doublePoints = doublePoints;
        this.recalculateGems();
        this.recalculatePoints();
        this.flagForSave();
    }

    public void addSpawnersInClaim(int amount) {
        spawnersInClaim += amount;

        if (spawnersInClaim < 0) {
            spawnersInClaim = 0;
        }

        recalculatePoints();
        flagForSave();
    }

    public void removeSpawnersInClaim(int amount) {
        spawnersInClaim -= amount;

        if (spawnersInClaim < 0) {
            spawnersInClaim = 0;
        }

        recalculatePoints();
        flagForSave();
    }

    public void setSpawnersInClaim(int amount) {
        if (amount < 0) {
            amount = 0;
        }

        spawnersInClaim = amount;
        recalculatePoints();
        flagForSave();
    }

    public void recalculateSpawnersInClaims() {
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                setSpawnersInClaim(findSpawners().size());
//            }
//        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public List<CreatureSpawner> findSpawners() {
        List<CreatureSpawner> list = new ArrayList<>();

        // Iterate through chunks' tile entities rather than every block
//        for (Claim claim : getClaims()) {
//            final World world = Bukkit.getWorld(claim.getWorld());
//            final Location minPoint = claim.getMinimumPoint();
//            final Location maxPoint = claim.getMaximumPoint();
//            final int minChunkX = ((int) minPoint.getX()) >> 4;
//            final int minChunkZ = ((int) minPoint.getZ()) >> 4;
//            final int maxChunkX = ((int) maxPoint.getX()) >> 4;
//            final int maxChunkZ = ((int) maxPoint.getZ()) >> 4;
//
//            for (int chunkX = minChunkX; chunkX < maxChunkX + 1; chunkX++) {
//                for (int chunkZ = minChunkZ; chunkZ < maxChunkZ + 1; chunkZ++) {
//                    Chunk chunk = world.getChunkAt(chunkX, chunkZ);
//
//                    for (BlockState blockState : chunk.getTileEntities()) {
//                        // Check if the block is a mob spawner
//                        if (blockState instanceof CreatureSpawner) {
//                            // Even though we're iterating through chunks' tile entities
//                            // we need to make sure that the block's location is within
//                            // the claim (because claims don't have to align with chunks)
//                            final Location loc = blockState.getLocation();
//
//                            if (loc.getX() >= minPoint.getX() && loc.getZ() >= minPoint.getZ() &&
//                                    loc.getX() <= maxPoint.getX() && loc.getZ() <= maxPoint.getZ()) {
//                                list.add((CreatureSpawner) blockState);
//                            }
//                        }
//                    }
//                }
//            }
//        }

        return list;
    }

    public void flagForSave() {
        needsSave = true;
    }

    public boolean isOwner(UUID check) {
        return (check.equals(owner));
    }

    public boolean isMember(UUID check) {
        return members.contains(check);
    }

    public boolean isCaptain(UUID check) {
        return captains.contains(check);
    }

    public boolean isCoLeader(UUID check) {
        return coleaders.contains(check);
    }

    public boolean isAlly(UUID check) {
        Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(check);
        return (checkTeam != null && isAlly(checkTeam));
    }

    public boolean isAlly(Team team) {
        return (getAllies().contains(team.getUniqueId()));
    }

    public boolean ownsLocation(Location location) {
        return (LandBoard.getInstance().getTeam(location) == this);
    }

    public boolean ownsClaim(Claim claim) {
        return (claims.contains(claim));
    }

    public boolean removeMember(UUID member) {
        members.remove(member);
        captains.remove(member);
        coleaders.remove(member);

        // If the owner leaves (somehow)
        if (isOwner(member)) {
            Iterator<UUID> membersIterator = members.iterator();
            this.owner = membersIterator.hasNext() ? membersIterator.next() : null;
        }

        try {
            for (Subclaim subclaim : subclaims) {
                if (subclaim.isMember(member)) {
                    subclaim.removeMember(member);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DTR > getMaxDTR()) {
            DTR = getMaxDTR();
        }

        if (this.loading) return false;
        TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_REMOVED, ImmutableMap.of(
                "playerId", member
        ));

        getOnlineMembers().forEach(LunarClientListener::updateNametag);

        flagForSave();
        return (owner == null || members.size() == 0);
    }

    public boolean hasDTRBitmask(DTRBitmask bitmaskType) {
        if (getOwner() != null) {
            return (false);
        }

        int dtrInt = (int) DTR;
        return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
    }

    public int getOnlineMemberAmount() {
        int amt = 0;

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !ModHandler.INSTANCE.isInVanish(member)) {
                amt++;
            }
        }

        return (amt);
    }

    public Collection<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !ModHandler.INSTANCE.isInVanish(member)) {
                players.add(exactPlayer);
            }
        }

        return (players);
    }

    public Collection<UUID> getOfflineMembers() {
        List<UUID> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer == null || ModHandler.INSTANCE.isInVanish(member)) {
                players.add(member);
            }
        }

        return (players);
    }

    public Subclaim getSubclaim(String name) {
        for (Subclaim subclaim : subclaims) {
            if (subclaim.getName().equalsIgnoreCase(name)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public Subclaim getSubclaim(Location location) {
        for (Subclaim subclaim : subclaims) {
            if (new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2()).contains(location)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public int getSize() {
        return (getMembers().size());
    }

    public boolean isRaidable() {
        return (DTR <= 0);
    }

    public void playerDeath(String playerName, double dtrLoss, Player killer, boolean evader) {

        if (killer != null && Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(killer)) {
            return;
        }

        double newDTR = Math.max(DTR - dtrLoss, -.99);

        TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_DEATH, ImmutableMap.of(
                "playerName", playerName,
                "dtrLoss", dtrLoss,
                "oldDtr", DTR,
                "newDtr", newDTR
        ));

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + playerName);
                player.sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + Math.ceil(newDTR));
            }
        }

        if (!isRaidable() && newDTR <= 0) {
            final TeamRaidableEvent teamRaidableEvent = new TeamRaidableEvent(Foxtrot.getInstance().getServer().getPlayer(playerName), killer, this, DTR, newDTR, UUIDUtils.uuid(playerName));
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamRaidableEvent);
        }

        Foxtrot.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + playerName + "]");
        setDTR(newDTR);

        if (this.getPurchasedUpgrades().contains(UpgradeType.REDUCED_DTR_REGEN) || evader) {
            DTRCooldown = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(25);
        } else if (killer != null && killer.getWorld().getEnvironment() == World.Environment.NORMAL) {
            DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
        } else {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
            DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
        }

        DTRHandler.wasOnCooldown.add(this.uniqueId);
    }

    public double getDTRIncrement() {
        return (getDTRIncrement(getOnlineMemberAmount()));
    }

    public double getDTRIncrement(int playersOnline) {
        double dtrPerHour = DTRHandler.getBaseDTRIncrement(getSize()) * playersOnline;
        return (dtrPerHour / 60);
    }

    public double getMaxDTR() {
        return (DTRHandler.getMaxDTR(getSize()));
    }

    public void load(BasicDBObject obj) {
        loading = true;
        setUniqueId(obj.getObjectId("_id"));
        setOwner(obj.getString("Owner") == null ? null : UUID.fromString(obj.getString("Owner")));
        if (obj.containsField("CoLeaders")) for (Object coLeader : (BasicDBList) obj.get("CoLeaders")) addCoLeader(UUID.fromString((String) coLeader));
        if (obj.containsField("Captains")) for (Object captain : (BasicDBList) obj.get("Captains")) addCaptain(UUID.fromString((String) captain));
        if (obj.containsField("Members")) for (Object member : (BasicDBList) obj.get("Members")) addMember(UUID.fromString((String) member));
        if (obj.containsField("Invitations")) for (Object invite : (BasicDBList) obj.get("Invitations")) getInvitations().add(UUID.fromString((String) invite));
        if (obj.containsField("DTR")) setDTR(obj.getDouble("DTR"));
        if (obj.containsField("DTRCooldown")) setDTRCooldown(obj.getDate("DTRCooldown").getTime());
        if (obj.containsField("Balance")) setBalance(obj.getDouble("Balance"));
        if (obj.containsField("MaxOnline")) setMaxOnline(obj.getInt("MaxOnline"));
        if (obj.containsField("HQ")) setHQ(LocationSerializer.deserialize((BasicDBObject) obj.get("HQ")));
        if (obj.containsField("Announcement")) setAnnouncement(obj.getString("Announcement"));
        if (obj.containsField("PowerFaction")) setPowerFaction(obj.getBoolean("PowerFaction"));
        if (obj.containsField("Lives")) setLives(obj.getInt("Lives"));
        if (obj.containsField("Claims")) for (Object claim : (BasicDBList) obj.get("Claims")) getClaims().add(Claim.fromJson((BasicDBObject) claim));
        if (obj.containsField("Subclaims")) for (Object subclaim : (BasicDBList) obj.get("Subclaims")) getSubclaims().add(Subclaim.fromJson((BasicDBObject) subclaim));
        if (obj.containsKey("SpawnersInClaim")) setSpawnersInClaim(obj.getInt("SpawnersInClaim"));

        loading = false;
    }

    public void load(String str) {
        load(str, false);
    }

    public void load(String str, boolean forceSave) {
        loading = true;
        String[] lines = str.split("\n");

        for (String line : lines) {
            if (line.indexOf(':') == -1) {
                System.out.println("Found an invalid line... `" + line + "`");
                continue;
            }

            String identifier = line.substring(0, line.indexOf(':'));
            String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

            if (identifier.equalsIgnoreCase("Owner")) {
                if (!lineParts[0].equals("null")) {
                    setOwner(UUID.fromString(lineParts[0].trim()));
                }
            } else if (identifier.equalsIgnoreCase("UUID")) {
                uniqueId = new ObjectId(lineParts[0].trim());
            } else if (identifier.equalsIgnoreCase("Members")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addMember(UUID.fromString(name.trim()));
                    }
                }
            } else if(identifier.equalsIgnoreCase("CoLeaders")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCoLeader(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Captains")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCaptain(UUID.fromString(name.trim()));
                    }
                }
            }
//            else if (identifier.equalsIgnoreCase("Rosters")) {
//                for (String name : lineParts) {
//                    if (name.length() >= 2) {
//                        Role role = Role.valueOf(name.split("_")[1]);
//
//                        getRoster().put(UUID.fromString(name.trim().replace("_" + role, "")), role);
//                    }
//                }
//            }
            else if (identifier.equalsIgnoreCase("Invited")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getInvitations().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getHistoricalMembers().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("PotionEffects")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        this.purchasedEffects.add(PotionEffectType.getByName(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Upgrades")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        this.purchasedUpgrades.add(UpgradeType.valueOf(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HQ")) {
                setHQ(parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
                setDTR(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
                setBalance(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("MaxOnline")) {
                setMaxOnline(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ForceInvites")) {
                setForceInvites(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
                setDTRCooldown(Long.parseLong(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
                setName(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("Claims")) {
                for (String claim : lineParts) {
                    claim = claim.replace("[", "").replace("]", "");

                    if (claim.contains(":")) {
                        String[] split = claim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String world = split[7].trim();

                        Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
                        claimObj.setName(name);

                        getClaims().add(claimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Allies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Foxtrot.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String ally : lineParts) {
                    ally = ally.replace("[", "").replace("]", "");

                    if (ally.length() != 0) {
                        allies.add(new ObjectId(ally.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Foxtrot.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String requestedAlly : lineParts) {
                    requestedAlly = requestedAlly.replace("[", "").replace("]", "");

                    if (requestedAlly.length() != 0) {
                        requestedAllies.add(new ObjectId(requestedAlly.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Subclaims")) {
                for (String subclaim : lineParts) {
                    subclaim = subclaim.replace("[", "").replace("]", "");

                    if (subclaim.contains(":")) {
                        String[] split = subclaim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String membersRaw = "";

                        if (split.length >= 8) {
                            membersRaw = split[7].trim();
                        }

                        Location location1 = new Location(Bukkit.getWorlds().get(0), x1, y1, z1);
                        Location location2 = new Location(Bukkit.getWorlds().get(0), x2, y2, z2);
                        List<UUID> members = new ArrayList<>();

                        for (String uuidString : membersRaw.split(", ")) {
                            if (uuidString.isEmpty()) {
                                continue;
                            }

                            members.add(UUID.fromString(uuidString.trim()));
                        }

                        Subclaim subclaimObj = new Subclaim(location1, location2, name);
                        subclaimObj.setMembers(members);

                        getSubclaims().add(subclaimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Announcement")) {
                setAnnouncement(lineParts[0]);
            } else if(identifier.equalsIgnoreCase("PowerFaction")) {
                setPowerFaction(Boolean.parseBoolean(lineParts[0]));
            } else if(identifier.equalsIgnoreCase("Lives")) {
                setLives(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Kills")) {
                setKills(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Deaths")) {
                setDeaths(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("KothCaptures")) {
                setKothCaptures(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("MiniKothCaptures")) {
                setMiniKothCaptures(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("TrapDoors")) {
                setTrapDoors(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DiamondsMined")) {
                setDiamondsMined(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("CitadelsCapped")) {
                setCitadelsCapped(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("SpawnersInClaim")) {
                setSpawnersInClaim(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ClaimLocked")) {
                setClaimLocked(Boolean.parseBoolean(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ConquestsCapped")) {
                setConquestsCapped(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Points")) {
                setPoints(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("AddedPoints")) {
                setAddedPoints(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("TrappingPoints")) {
                setTrappingPoints(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FactionsMadeRaidable")) {
                setFactionsMadeRaidable(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("RemovedPoints")) {
                setRemovedPoints(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DoublePoints")) {
                setDoublePoints(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Gems")) {
                setGems(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DoubleKillPoints")) {
                this.doubleKillPoints = Integer.parseInt(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("RemovedGems")) {
                setRemovedGems(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("PlaytimeGems")) {
                setPlaytimeGems(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("AddedGems")) {
                setAddedGems(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FurysCapped")) {
                setFurysCaptured(Integer.parseInt(lineParts[0]));
            }
        }

        if (uniqueId == null) {
            uniqueId = new ObjectId();
            Foxtrot.getInstance().getLogger().info("Generating UUID for team " + getName() + "...");
        }

        loading = false;
        needsSave = forceSave;
    }

    public String saveString(boolean toJedis) {
        if (toJedis) {
            needsSave = false;
        }

        if (loading) {
            return (null);
        }

        StringBuilder teamString = new StringBuilder();

        StringBuilder members = new StringBuilder();
        StringBuilder captains = new StringBuilder();
        StringBuilder coleaders = new StringBuilder();
        StringBuilder invites = new StringBuilder();
        StringBuilder rosters = new StringBuilder();
        StringBuilder historicalMembers = new StringBuilder();
        StringBuilder potionEffects = new StringBuilder();
        StringBuilder upgrade = new StringBuilder();

        for (UUID member : getMembers()) {
            members.append(member.toString()).append(", ");
        }

        for (UUID captain : getCaptains()) {
            captains.append(captain.toString()).append(", ");
        }

        for (UUID co : getColeaders()) {
            coleaders.append(co.toString()).append(", ");
        }

        for (PotionEffectType potionEffectType : this.purchasedEffects) {
            potionEffects.append(potionEffectType.getName()).append(", ");
        }

        for (UpgradeType upgradeType : this.purchasedUpgrades) {
            upgrade.append(upgradeType.name()).append(", ");
        }

        for (UUID invite : getInvitations()) {
            invites.append(invite.toString()).append(", ");
        }

//        for (Map.Entry<UUID, Role> entry : getRoster().entrySet()) {
//            rosters.append(entry.getKey().toString()).append("_").append(entry.getValue().name());
//        }

        for (UUID member : getHistoricalMembers()) {
            historicalMembers.append(member.toString()).append(", ");
        }

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

        if (historicalMembers.length() > 2) {
            historicalMembers.setLength(historicalMembers.length() - 2);
        }

        teamString.append("UUID:").append(getUniqueId().toString()).append("\n");
        teamString.append("Owner:").append(getOwner()).append('\n');
        teamString.append("CoLeaders:").append(coleaders).append('\n');
        teamString.append("Captains:").append(captains).append('\n');
        teamString.append("Upgrades:").append(upgrade).append('\n');
        teamString.append("Members:").append(members).append('\n');
        teamString.append("PotionEffects:").append(potionEffects).append('\n');
        teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
        teamString.append("Rosters:").append(rosters.toString().replace("\n", "")).append('\n');
        teamString.append("Subclaims:").append(getSubclaims().toString().replace("\n", "")).append('\n');
        teamString.append("Claims:").append(getClaims().toString().replace("\n", "")).append('\n');
        teamString.append("Allies:").append(getAllies().toString()).append('\n');
        teamString.append("RequestedAllies:").append(getRequestedAllies().toString()).append('\n');
        teamString.append("HistoricalMembers:").append(historicalMembers).append('\n');
        teamString.append("DTR:").append(getDTR()).append('\n');
        teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("TrapDoors:").append(getTrapDoors()).append('\n');
        teamString.append("MaxOnline:").append(getMaxOnline()).append('\n');
        teamString.append("ForceInvites:").append(getForceInvites()).append('\n');
        teamString.append("DTRCooldown:").append(getDTRCooldown()).append('\n');
        teamString.append("FriendlyName:").append(getName().replace("\n", "")).append('\n');
        teamString.append("Announcement:").append(String.valueOf(getAnnouncement()).replace("\n", "")).append("\n");
        teamString.append("PowerFaction:").append(isPowerFaction()).append("\n");
        teamString.append("Lives:").append(getLives()).append("\n");
        teamString.append("Kills:").append(getKills()).append("\n");
        teamString.append("Deaths:").append(getDeaths()).append("\n");
        teamString.append("DiamondsMined:").append(getDiamondsMined()).append("\n");
        teamString.append("KothCaptures:").append(getKothCaptures()).append("\n");
        teamString.append("MiniKothCaptures:").append(getMiniKothCaptures()).append("\n");
        teamString.append("CitadelsCapped:").append(getCitadelsCapped()).append("\n");
        teamString.append("ConquestsCapped:").append(getConquestsCapped()).append("\n");
        teamString.append("SpawnersInClaim:").append(getSpawnersInClaim()).append("\n");
        teamString.append("DoublePoints:").append(getDoublePoints()).append("\n");
        teamString.append("AddedPoints:").append(getAddedPoints()).append("\n");
        teamString.append("TrappingPoints:").append(getTrappingPoints()).append("\n");
        teamString.append("Gems:").append(getGems()).append("\n");
        teamString.append("AddedGems:").append(getAddedGems()).append("\n");
        teamString.append("RemovedGems:").append(getRemovedGems()).append("\n");
        teamString.append("PlaytimeGems:").append(getPlaytimeGems()).append("\n");
        teamString.append("RemovedPoints:").append(getRemovedPoints()).append("\n");
        teamString.append("FactionsMadeRaidable:").append(getFactionsMadeRaidable()).append("\n");
        teamString.append("ClaimLocked:").append(isClaimLocked()).append("\n");
        teamString.append("EotwCapped:").append(isEotwCapped()).append("\n");
        teamString.append("FurysCapped:").append(getFurysCaptured()).append("\n");
        teamString.append("DoubleKillPoints:").append(getDoubleKillPoints()).append("\n");

        if (getHQ() != null) {
            teamString.append("HQ:").append(getHQ().getWorld().getName()).append(",").append(getHQ().getX()).append(",").append(getHQ().getY()).append(",").append(getHQ().getZ()).append(",").append(getHQ().getYaw()).append(",").append(getHQ().getPitch()).append('\n');
        }

        return (teamString.toString());
    }

    public BasicDBObject toJSON() {
        BasicDBObject dbObject = new BasicDBObject();
        
        dbObject.put("Owner", getOwner() == null ? null : getOwner().toString());
        dbObject.put("CoLeaders", UUIDUtils.uuidsToStrings(getColeaders()));
        dbObject.put("Captains", UUIDUtils.uuidsToStrings(getCaptains()));
        dbObject.put("Members", UUIDUtils.uuidsToStrings(getMembers()));
        dbObject.put("Invitations", UUIDUtils.uuidsToStrings(getInvitations()));
        dbObject.put("Allies", getAllies());
        dbObject.put("RequestedAllies", getRequestedAllies());
        dbObject.put("DTR", getDTR());
        dbObject.put("DTRCooldown", new Date(getDTRCooldown()));
        dbObject.put("Balance", getBalance());
        dbObject.put("MaxOnline", getMaxOnline());
        dbObject.put("Name", getName());
        dbObject.put("HQ", LocationSerializer.serialize(getHQ()));
        dbObject.put("Announcement", getAnnouncement());
        dbObject.put("PowerFaction", isPowerFaction());
        dbObject.put("Lives", getLives());
        dbObject.put("TrapDoors", getTrapDoors());

        BasicDBList claims = new BasicDBList();
        BasicDBList subclaims = new BasicDBList();

        for (Claim claim : getClaims()) {
            claims.add(claim.json());
        }

        for (Subclaim subclaim : getSubclaims()) {
            subclaims.add(subclaim.json());
        }

        dbObject.put("Claims", claims);
        dbObject.put("Subclaims", subclaims);
        dbObject.put("Kills", this.kills);
        dbObject.put("Deaths", this.deaths);
        dbObject.put("DiamondsMined", this.diamondsMined);
        dbObject.put("CitadelsCaptured", this.citadelsCapped);
        dbObject.put("ConquestsCapped", this.conquestsCapped);
        dbObject.put("KothCaptures", this.kothCaptures);
        dbObject.put("ClaimLocked", this.claimLocked);
        dbObject.put("EotwCapped", this.eotwCapped);
        dbObject.put("FactionsMadeRaidable", this.factionsMadeRaidable);
        dbObject.put("AddedPoints", this.addedPoints);
        dbObject.put("TrappingPoints", this.trappingPoints);
        dbObject.put("DoublePoints", this.doublePoints);
        dbObject.put("RemovedPoints", this.removedPoints);
        dbObject.put("Gems", this.gems);
        dbObject.put("SpawnersInClaim", this.spawnersInClaim);
        dbObject.put("FurysCapped", this.furysCaptured);
        dbObject.put("DoubleKillPoints", this.doubleKillPoints);

        return (dbObject);
    }

    public BasicDBObject getJSONIdentifier() {
        return (new BasicDBObject("_id", getUniqueId().toHexString()));
    }

    private Location parseLocation(String[] args) {
        if (args.length != 6) {
            return (null);
        }

        World world = Foxtrot.getInstance().getServer().getWorld(args[0]);
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        float yaw = Float.parseFloat(args[4]);
        float pitch = Float.parseFloat(args[5]);

        return (new Location(world, x, y, z, yaw, pitch));
    }

    public void sendMessage(String message) {
        this.getOnlineMembers().forEach(it -> it.sendMessage(message));
    }

    public void playSound(Sound sound) {
        this.getOnlineMembers().forEach(it -> it.playSound(it.getLocation(), sound, 1, 1));
    }
    public void sendTitle(String title, String subtitle) {
        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                PlayerUtil.sendTitle(player, title, subtitle);
            }
        }
    }
    public int recalculatePoints() {
        int basePoints = 0;

        boolean teams = Foxtrot.getInstance().getMapHandler().isKitMap();

        basePoints += kills;
        basePoints -= deaths;
        basePoints += kothCaptures * (Foxtrot.getInstance().getMapHandler().isKitMap() ? 10 : 20);
        basePoints += miniKothCaptures * 10;
        basePoints += citadelsCapped * (Foxtrot.getInstance().getServerHandler().isAu() ? 125 : 200);
        basePoints += furysCaptured * 125;
        basePoints += addedPoints;
        basePoints += doublePoints;
        basePoints -= removedPoints;

        if (basePoints < 0) {
            basePoints = 0;
        }

        this.points = basePoints;
        return basePoints;
    }

    public int recalculateGems() {
        int baseGems = 0;

        baseGems += kills;
        baseGems -= deaths;
        baseGems += addedGems;
        baseGems -= removedGems;
        baseGems += playtimeGems;
        baseGems += kothCaptures * 25;
        baseGems += miniKothCaptures * 10;
        baseGems += citadelsCapped * 100;

        if (baseGems < 0) {
            baseGems = 0;
        }

        this.gems = baseGems;
        return baseGems;
    }

    public String[] getPointBreakDown() {
        int basePoints = 0;

        basePoints += kills;
        basePoints -= deaths;
        basePoints += kothCaptures * 10;
        basePoints += citadelsCapped * 100;
        basePoints += conquestsCapped * 125;
        basePoints += addedPoints;
        basePoints += doublePoints;
        basePoints -= removedPoints;

        if (basePoints < 0) {
            basePoints = 0;
        }

        return new String[]{
                "&a&lKill &6Points: &7(&f" + kills + " kills&7) &6x &a1pts/each &7= &f" + (kills) + " pts",
                "&4&lDeath &6Points: &7(&f" + deaths + " deaths&7) &6x &c-1pts/each &7= &f-" + (deaths) + " pts",
                "&9&lKOTH &6Captures Points: &7(&f" + kothCaptures + " caps&7) &6x &a10 " + "pts/each &7= &f" + (kothCaptures * 10) + " pts",
                "&5&lCitadel &6Captures Points: &7(&f" + citadelsCapped + " caps&7) &6x &a100pts/each &7= &f" + (citadelsCapped * 100) + " pts",
                "&9&lConquest &6Captures Points: &7(&f" + conquestsCapped + " caps&7) &6x &a125pts/each &7= &f" + (conquestsCapped * 125) + " pts",
                "&2&lExtra &6Added Points: &f" + addedPoints + " pts",
                "&d&lDouble &6Points: &f" + doublePoints + " pts",
                "&c&lExtra &6Deleted Points: &f-" + removedPoints + " pts",
                "&6Sum Points: &f" + basePoints
        };
    }

    public void sendTeamInfo(Player player) {
        // Don't make our null teams have DTR....
        // @HCFactions
        if (getOwner() == null) {
            player.sendMessage(GRAY_LINE);
            player.sendMessage(getName(player));

            if ( HQ != null && HQ.getWorld().getEnvironment() != World.Environment.NORMAL) {
                String world = HQ.getWorld().getEnvironment() == World.Environment.NETHER ? "Nether" : "End"; // if it's not the nether, it's the end

                if (HQ.getWorld().getName().contains("MiniKOTH")) {
                    world = "Mini KOTH";
                }

                player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ() + " (" + world + ")"));
            } else {
                player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
            }

            if (getName().equalsIgnoreCase("Glowstone")) {
                player.sendMessage(ChatColor.YELLOW + "Next Reset: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int) (Foxtrot.getInstance().getGlowHandler().getResetTime()/1000)));
            }

            player.sendMessage(GRAY_LINE);
            return;
        }

        KillsMap killsMap = Foxtrot.getInstance().getKillsMap();
        DeathbanMap deathbanMap = Foxtrot.getInstance().getDeathbanMap();
        Player owner = Foxtrot.getInstance().getServer().getPlayer(getOwner());
        StringBuilder allies = new StringBuilder();

        FancyMessage coleadersJson = new FancyMessage("Co-Leaders: ").color(ChatColor.YELLOW);

        FancyMessage captainsJson = new FancyMessage("Captains: ").color(ChatColor.YELLOW);

        if (player.hasPermission("foxtrot.manage")) {
            captainsJson.command("/manageteam demote " + getName()).tooltip("§bClick to demote captains");
        }

        FancyMessage membersJson = new FancyMessage("Members: ").color(ChatColor.YELLOW);

        if (player.hasPermission("foxtrot.manage")) {
            membersJson.command("/manageteam promote " + getName()).tooltip("§bClick to promote members");
        }

        int onlineMembers = 0;

        for (ObjectId allyId : getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                allies.append(ally.getName(player)).append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN).append(ally.getOnlineMemberAmount()).append("/").append(ally.getSize()).append(ChatColor.YELLOW).append("]").append(ChatColor.GRAY).append(", ");
            }
        }


        for (Player onlineMember : getOnlineMembers()) {
            onlineMembers++;

            // There can only be one owner, so we special case it.
            if (isOwner(onlineMember.getUniqueId())) {
                continue;
            }

            FancyMessage appendTo = membersJson;
            if(isCoLeader(onlineMember.getUniqueId())) {
                appendTo = coleadersJson;
            } else if(isCaptain(onlineMember.getUniqueId())) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(onlineMember.getName()).color(ChatColor.GREEN).then("[").color(ChatColor.YELLOW);
            appendTo.then(killsMap.getKills(onlineMember.getUniqueId()) + "").color(ChatColor.GREEN);
            appendTo.then("]").color(ChatColor.YELLOW);
        }

        for (UUID offlineMember : getOfflineMembers()) {
            if (isOwner(offlineMember)) {
                continue;
            }

            FancyMessage appendTo = membersJson;
            if(isCoLeader(offlineMember)) {
                appendTo = coleadersJson;
            } else if(isCaptain(offlineMember)) {
                appendTo = captainsJson;
            }

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(Proton.getInstance().getUuidCache().name(offlineMember)).color(deathbanMap.isDeathbanned(offlineMember) ? ChatColor.RED : ChatColor.GRAY);
            appendTo.then("[").color(ChatColor.YELLOW).then("" + killsMap.getKills(offlineMember)).color(ChatColor.GREEN);
            appendTo.then("]").color(ChatColor.YELLOW);

        }

        // Now we can actually send all that info we just processed.
        player.sendMessage(GRAY_LINE);

        FancyMessage teamLine = new FancyMessage();

        teamLine.text(ChatColor.BLUE + getName()).command("/f focus " + this.getName()).tooltip(ChatColor.GREEN + "Click to focus this team!");
        teamLine.then().text(ChatColor.GRAY + " [" + onlineMembers + "/" + getSize() + "]" + ChatColor.DARK_AQUA + " - ");
        teamLine.then().text(ChatColor.YELLOW + "HQ: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));

        if (HQ != null && player.hasPermission("neutron.staff")) {
            teamLine.command("/tppos " + HQ.getBlockX() + " " + HQ.getBlockY() + " " + HQ.getBlockZ());
            teamLine.tooltip("§aClick to warp to HQ");
        }

        if (player.hasPermission("neutron.staff")) {
            teamLine.then().text("§3 - §e[Manage]").color(ChatColor.YELLOW).command("/manageteam manage " + getName()).tooltip("§bClick to manage team");
        }

        teamLine.send(player);

        if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Allies: " + allies);
        }

        FancyMessage leader = new FancyMessage(ChatColor.YELLOW + "Leader: " + (owner == null || ModHandler.INSTANCE.isInVanish(owner.getUniqueId()) ? (deathbanMap.isDeathbanned(getOwner()) ? ChatColor.RED : ChatColor.GRAY) : ChatColor.GREEN) + Proton.getInstance().getUuidCache().name(getOwner()) + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(getOwner()) + ChatColor.YELLOW + "]");


        if (player.hasPermission("neutron.staff")) {
            leader.command("/manageteam leader " + getName()).tooltip("§bClick to change leader");
        }

        leader.send(player);

        if (!ChatColor.stripColor(coleadersJson.toOldMessageFormat()).endsWith("s: ")) {
            coleadersJson.send(player);
        }

        if (!ChatColor.stripColor(captainsJson.toOldMessageFormat()).endsWith("s: ")) {
            captainsJson.send(player);
        }


        if (!ChatColor.stripColor(membersJson.toOldMessageFormat()).endsWith("s: ")) {
            membersJson.send(player);
        }


        FancyMessage balance = new FancyMessage(ChatColor.YELLOW + "Balance: " + ChatColor.BLUE + "$" + Math.round(getBalance()));

        if (player.hasPermission("foxtrot.manage")) {
            balance.command("/manageteam balance " + getName()).tooltip("§bClick to modify team balance");
        }

        balance.send(player);

        int totalHearts = (int) Math.ceil(getMaxDTR());
        int currentHearts = (int) Math.ceil(getDTR());

        StringBuilder aids = new StringBuilder();

        for (int i = 0; i < totalHearts; i++) {
            if (this.isRaidable() && DTRHandler.isRegenerating(this)) {
                aids.append(ChatColor.GOLD + "❤");
                continue;
            }

            if (this.isRaidable()) {
                aids.append(ChatColor.DARK_RED + "❤");
                continue;
            }

            if (i >= currentHearts) {
                if (DTRHandler.isRegenerating(this)) {
                    aids.append(ChatColor.GOLD + "❤");
                    continue;
                }

                aids.append(ChatColor.GRAY + "❤");
                continue;
            }

            if (DTR / getMaxDTR() <= 0.25) {
                if (!this.isRaidable()) {
                    aids.append(ChatColor.YELLOW + "❤");
                    continue;
                }
            }


            aids.append(ChatColor.GREEN + "❤");
        }

        FancyMessage dtrMessage = new FancyMessage(ChatColor.YELLOW + "Deaths Until Raidable: " + getDTRColor() + aids + getDTRSuffix());

        if (!Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())) {
            dtrMessage = new FancyMessage(ChatColor.YELLOW + "Deaths Until Raidable: " + getDTRColor() + Math.ceil(getDTR()) + getDTRSuffix());
        }

        if (player.hasPermission("foxtrot.manage")) {
            dtrMessage.command("/manageteam dtr " + getName()).tooltip("§bClick to modify team DTR");
        }

        dtrMessage.send(player);

        if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
            if (this.recalculateGems() > 0) {
                player.sendMessage(ChatColor.YELLOW + "Gems: " + ChatColor.RED + this.recalculateGems());
            }
            if (this.factionsMadeRaidable > 0) {
                player.sendMessage(ChatColor.YELLOW + "Factions Made Raidable: " + ChatColor.RED + this.getFactionsMadeRaidable());
            }
            if (this.trappingPoints > 0) {
                player.sendMessage(ChatColor.YELLOW + "Trapping Points: " + ChatColor.RED + this.trappingPoints);
            }
        }

        if (this.recalculatePoints() > 0) {
            player.sendMessage(ChatColor.YELLOW + "Points: " + ChatColor.RED + this.recalculatePoints());
        }

        if (this.kothCaptures > 0) {
            player.sendMessage(ChatColor.YELLOW + "KOTH Captures: " + ChatColor.RED + getKothCaptures());
        }

        if (this.spawnersInClaim > 0) {
            player.sendMessage(ChatColor.YELLOW + "Spawners: " + ChatColor.RED + getSpawnersInClaim());
        }

        if (this.citadelsCapped > 0) {
            player.sendMessage(ChatColor.YELLOW + "Citadel Captures: " + ChatColor.RED + getCitadelsCapped());
        }

        if (DTRHandler.isOnCooldown(this)) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.YELLOW + "DTR Regen: " + ChatColor.BLUE + TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())/1000)).trim());
            } else {
                FancyMessage message = new FancyMessage(ChatColor.YELLOW + "DTR Regen: ")
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

                message.then(TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis()))/1000)).color(ChatColor.BLUE)
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

                message.send(player);
            }
        }

        // Only show this if they're a member.
        if (isMember(player.getUniqueId()) && announcement != null && !announcement.equals("null")) {
            player.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.LIGHT_PURPLE + announcement);
        }

        player.sendMessage(GRAY_LINE);
        // .... and that is how we do a /f who.
    }

    public String formatDTR() {
        return getDTRColor().toString() + Math.ceil(getDTR()) + getDTRSuffix();
    }

    public List<TrapDoor> findTrapDoors() {
        if (Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Cannot call Team#findSpawners on main thread");
        }

        List<TrapDoor> list = new ArrayList<>();

        // Iterate through chunks' tile entities rather than every block
        for (Claim claim : getClaims()) {
            final World world = Bukkit.getWorld(claim.getWorld());
            final Location minPoint = claim.getMinimumPoint();
            final Location maxPoint = claim.getMaximumPoint();
            final int minChunkX = ((int) minPoint.getX()) >> 4;
            final int minChunkZ = ((int) minPoint.getZ()) >> 4;
            final int maxChunkX = ((int) maxPoint.getX()) >> 4;
            final int maxChunkZ = ((int) maxPoint.getZ()) >> 4;

            for (int chunkX = minChunkX; chunkX < maxChunkX + 1; chunkX++) {
                for (int chunkZ = minChunkZ; chunkZ < maxChunkZ + 1; chunkZ++) {
                    Chunk chunk = world.getChunkAt(chunkX, chunkZ);

                    for (BlockState blockState : chunk.getTileEntities()) {
                        // Check if the block is a mob spawner
                        if (blockState instanceof TrapDoor) {
                            // Even though we're iterating through chunks' tile entities
                            // we need to make sure that the block's location is within
                            // the claim (because claims don't have to align with chunks)
                            final Location loc = blockState.getLocation();

                            if (loc.getX() >= minPoint.getX() && loc.getZ() >= minPoint.getZ() &&
                                    loc.getX() <= maxPoint.getX() && loc.getZ() <= maxPoint.getZ()) {
                                list.add((TrapDoor) blockState);
                            }
                        }
                    }
                }
            }
        }

        return list;
    }

    @Override
    public int hashCode() {
        return uniqueId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Team)) {
            return false;
        }

        Team other = (Team) obj;
        return other.uniqueId.equals(uniqueId);
    }

    public ChatColor getDTRColor() {
        ChatColor dtrColor = ChatColor.GREEN;

        if (DTR / getMaxDTR() <= 0.25) {
            if (isRaidable()) {
                dtrColor = ChatColor.DARK_RED;
            } else {
                dtrColor = ChatColor.YELLOW;
            }
        }

        return (dtrColor);
    }

    public String getDTRSuffix() {
        if (DTRHandler.isRegenerating(this)) {
            if (getOnlineMemberAmount() == 0) {
                return (ChatColor.GRAY + "◀");
            } else {
                return (ChatColor.GREEN + "▲");
            }
        } else if (DTRHandler.isOnCooldown(this)) {
            return (ChatColor.RED + "■");
        } else {
            return (ChatColor.GREEN + "◀");
        }
    }

}
