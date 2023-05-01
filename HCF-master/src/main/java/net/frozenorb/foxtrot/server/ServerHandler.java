package net.frozenorb.foxtrot.server;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.piston.PistonConstants;
import cc.fyre.universe.util.BungeeUtil;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.deathban.Deathban;
import net.frozenorb.foxtrot.util.Pair;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.minecraft.util.com.google.common.collect.Maps;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.server.idle.IdleCheckRunnable;
import net.frozenorb.foxtrot.server.uhc.UHCListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.Logout;
import cc.fyre.proton.util.ItemUtils;
import org.cavepvp.entity.Entity;

public class ServerHandler {

    public static int WARZONE_RADIUS = 1000;
    public static int WARZONE_BORDER = 3000;

    // NEXT MAP //
    // http://minecraft.gamepedia.com/Potion#Data_value_table
    private final Map<PotionType, PotionStatus> potionStatus = new HashMap<>();

    @Getter private Map<PotionType, Integer> potionLimits = new HashMap<>();

    @Getter private static Map<String, Logout> tasks = new HashMap<>();

    @Getter private final String serverName;
    @Getter private final String networkWebsite;
    @Getter private final String statsWebsiteRoot;

    @Getter private final String tabServerName;
    @Getter private final String tabSectionColor;
    @Getter private final String tabInfoColor;

    @Getter private final boolean teams;
    @Getter private final boolean au;
    @Getter private final boolean idleCheckEnabled;
    @Getter private final boolean startingTimerEnabled;
    @Getter private final boolean forceInvitesEnabled;
    @Getter private final boolean uhcHealing;
    @Getter private final boolean passiveTagEnabled;
    @Getter private final boolean allowBoosting;
    @Getter private final boolean waterPlacementInClaimsAllowed;
    @Getter private final boolean blockRemovalEnabled;

    @Getter private final boolean rodPrevention;
    @Getter private final boolean skybridgePrevention;

    @Getter private final boolean teamHQInEnemyClaims;

    @Getter private static Map<String, Long> homeTimer = new ConcurrentHashMap<>();

    @Getter @Setter private boolean EOTW = false;
    @Getter @Setter private boolean PreEOTW = false;

    @Getter private final boolean reduceArmorDamage;
    @Getter private final boolean blockEntitiesThroughPortals;

    @Getter private final ChatColor archerTagColor;
    @Getter private final ChatColor stunTagColor;
    @Getter private final ChatColor defaultRelationColor;

    @Getter private final boolean hardcore;
    @Getter private final boolean placeBlocksInCombat;

    @Getter private Map<UUID, String> nickName = new HashMap<>();
    @Getter private Map<UUID, String> pastNicknames = new HashMap<>();

    public ServerHandler() {
        serverName = Foxtrot.getInstance().getConfig().getString("serverName");
        networkWebsite = Foxtrot.getInstance().getConfig().getString("networkWebsite");
        statsWebsiteRoot = Foxtrot.getInstance().getConfig().getString("statsRoot");

        tabServerName = Foxtrot.getInstance().getConfig().getString("tab.serverName");
        tabSectionColor = Foxtrot.getInstance().getConfig().getString("tab.sectionColor");
        tabInfoColor = Foxtrot.getInstance().getConfig().getString("tab.infoColor");

        idleCheckEnabled = Foxtrot.getInstance().getConfig().getBoolean("idleCheck");
        startingTimerEnabled = Foxtrot.getInstance().getConfig().getBoolean("startingTimer");
        forceInvitesEnabled = Foxtrot.getInstance().getConfig().getBoolean("forceInvites");
        au = Foxtrot.getInstance().getConfig().getBoolean("au", false);
        uhcHealing = Foxtrot.getInstance().getConfig().getBoolean("uhcHealing");
        passiveTagEnabled = Foxtrot.getInstance().getConfig().getBoolean("passiveTag");
        teams = Foxtrot.getInstance().getConfig().getBoolean("teams");
        allowBoosting = Foxtrot.getInstance().getConfig().getBoolean("allowBoosting");
        waterPlacementInClaimsAllowed = Foxtrot.getInstance().getConfig().getBoolean("waterPlacementInClaims");
        blockRemovalEnabled = Foxtrot.getInstance().getConfig().getBoolean("blockRemoval");
        rodPrevention = Foxtrot.getInstance().getConfig().getBoolean("rodPrevention", true);
        skybridgePrevention = Foxtrot.getInstance().getConfig().getBoolean("skybridgePrevention", true);
        teamHQInEnemyClaims = Foxtrot.getInstance().getConfig().getBoolean("teamHQInEnemyClaims", true);

        for (PotionType type : PotionType.values()) {
            if (type == PotionType.WATER) {
                continue;
            }

            PotionStatus status = new PotionStatus(Foxtrot.getInstance().getConfig().getBoolean("potions." + type.name() + ".drinkables"), Foxtrot.getInstance().getConfig().getBoolean("potions." + type.name() + ".splash"), Foxtrot.getInstance().getConfig().getInt("potions." + type + ".maxLevel", -1));
            potionStatus.put(type, status);
        }

        if (idleCheckEnabled) {
            new IdleCheckRunnable().runTaskTimer(Foxtrot.getInstance(), 60 * 20L, 60 * 20L);
        }

        if (uhcHealing) {
            Bukkit.getPluginManager().registerEvents(new UHCListener(), Foxtrot.getInstance());
        }

        this.reduceArmorDamage = Foxtrot.getInstance().getConfig().getBoolean("reduceArmorDamage", true);
        this.blockEntitiesThroughPortals = Foxtrot.getInstance().getConfig().getBoolean("blockEntitiesThroughPortals", true);
        this.archerTagColor = ChatColor.valueOf(Foxtrot.getInstance().getConfig().getString("archerTagColor", "YELLOW"));
        this.stunTagColor = ChatColor.valueOf(Foxtrot.getInstance().getConfig().getString("stunTagColor", "BLUE"));
        this.defaultRelationColor = ChatColor.valueOf(Foxtrot.getInstance().getConfig().getString("defaultRelationColor", "RED"));

        Foxtrot.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            final StringBuilder stringBuilder = new StringBuilder();

            for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(onlinePlayer.getUniqueId());

                if (profile == null || profile.getActiveRank() == null || !profile.getActiveRank().getName().equalsIgnoreCase("Custom")) {
                    continue;
                }

                stringBuilder.append(stringBuilder.length() == 0 ? "" : ChatColor.GRAY + ", " + ChatColor.WHITE).append(onlinePlayer.getDisplayName());
            }

            Foxtrot.getInstance().getServer().broadcastMessage("");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "  Online Custom Ranks");
            Foxtrot.getInstance().getServer().broadcastMessage("");
            Foxtrot.getInstance().getServer().broadcastMessage("  " + (stringBuilder.length() == 0 ? "None" : stringBuilder.toString()));
            Foxtrot.getInstance().getServer().broadcastMessage("");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "  Purchase the Custom Rank at");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.WHITE + "  " + "https://store.cavepvp.org/category/custom-rank");
            Foxtrot.getInstance().getServer().broadcastMessage("");
        }, 350*20,350*20);

        this.hardcore = Foxtrot.getInstance().getConfig().getBoolean("hardcore", false);
        this.placeBlocksInCombat = Foxtrot.getInstance().getConfig().getBoolean("placeBlocksInCombat", true);
        
        registerPlayerDamageRestrictionListener();

        Bukkit.getServer().getPluginManager().registerEvents(new ServerListener(), Foxtrot.getInstance());

        potionLimits.put(PotionType.REGEN, 0);
        potionLimits.put(PotionType.POISON, 1);
        potionLimits.put(PotionType.SLOWNESS, 1);
        potionLimits.put(PotionType.STRENGTH, 0);
        potionLimits.put(PotionType.WEAKNESS, 0);
        potionLimits.put(PotionType.INVISIBILITY, 1);
        potionLimits.put(PotionType.INSTANT_DAMAGE, 0);
    }

    public int getPotionLimit(PotionType type) {
        return this.potionLimits.getOrDefault(type, type.getMaxLevel());
    }

    public void save() {

    }

    public String getEnchants() {
        if (Enchantment.DAMAGE_ALL.getMaxLevel() == 0 && Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0) {
            return "No Enchants";
        } else {
            return "Prot " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + ", Sharp " + Enchantment.DAMAGE_ALL.getMaxLevel();
        }
    }

    public boolean isWarzone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
            return (false);
        }

        return (Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS) || ((Math.abs(loc.getBlockX()) > WARZONE_BORDER || Math.abs(loc.getBlockZ()) > WARZONE_BORDER));
    }

    public boolean isSplashPotionAllowed(PotionType type) {
        return (!potionStatus.containsKey(type) || potionStatus.get(type).splash);
    }

    public boolean isDrinkablePotionAllowed(PotionType type) {
        return (potionStatus.containsKey(type) && !potionStatus.get(type).drinkables);
    }

    public boolean isPotionLevelAllowed(PotionType type, int amplifier) {
        return (potionStatus.containsKey(type) && potionStatus.get(type).maxLevel != -1 && potionStatus.get(type).maxLevel < amplifier);
    }

    public void startLogoutSequence(final Player player) {
        player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Logging out... " +ChatColor.YELLOW + "Please wait" + ChatColor.RED+ " 30" + ChatColor.YELLOW + " seconds.");

        BukkitTask taskid = new BukkitRunnable() {

            int seconds = 30;

            @Override
            public void run() {
                if (player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
                    player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED + ChatColor.BOLD + "CANCELLED!");
                    cancel();
                    return;
                }

                seconds--;

                if (seconds == 0) {
                    if (tasks.containsKey(player.getName())) {
                        tasks.remove(player.getName());
                        player.setMetadata("loggedout", new FixedMetadataValue(Foxtrot.getInstance(), true));

                        BungeeUtil.sendToServer(player, "Hub-0" + ThreadLocalRandom.current().nextInt(1,3));

                        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                            if (player.isOnline()) {
                                player.kickPlayer("§cYou have been safely logged out of the server!");
                            }
                        }, 20*2);
                        cancel();
                    }
                }

            }
        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);

        tasks.put(player.getName(), new Logout(taskid.getTaskId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)));
    }

    public RegionData getRegion(Team ownerTo, Location location) {
        if (ownerTo != null && ownerTo.getOwner() == null) {
            if (ownerTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                return (new RegionData(RegionType.SPAWN, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.KOTH)) {
                return (new RegionData(RegionType.KOTH, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (new RegionData(RegionType.CITADEL, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.ROAD)) {
                return (new RegionData(RegionType.ROAD, ownerTo));
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
                return (new RegionData(RegionType.CONQUEST, ownerTo));
            }
        }

        if (ownerTo != null) {
            return (new RegionData(RegionType.CLAIMED_LAND, ownerTo));
        } else if (isWarzone(location)) {
            return (new RegionData(RegionType.WARZONE, null));
        }

        return (new RegionData(RegionType.WILDNERNESS, null));
    }

    public boolean isUnclaimed(Location loc) {
        return (LandBoard.getInstance().getClaim(loc) == null && !isWarzone(loc));
    }

    public boolean isAdminOverride(Player player) {
        return (player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.admin.override"));
    }

    public Location getSpawnLocation() {
        World spawn = Foxtrot.getInstance().getServer().getWorld("Spawn");

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            spawn = Foxtrot.getInstance().getServer().getWorld("world");
        }

        return (spawn.getSpawnLocation().add(0, 1, 0));
    }

    public boolean isUnclaimedOrRaidable(Player player, Location loc) {
        final Team owner = LandBoard.getInstance().getTeam(loc);

        if (owner == null) {
            return true;
        }

        if (!owner.isRaidable()) {
            return false;
        }

        if (player == null || owner.getRaidStealTeam() == null) {
            return true;
        }


        if (Foxtrot.getInstance().getTeamHandler().getTeam(player) == null) {
            return false;
        }

        return owner.getRaidStealTeam().toString().equalsIgnoreCase(Foxtrot.getInstance().getTeamHandler().getTeam(player).getUniqueId().toString());
    }

    public double getDTRLoss(Player player) {
        return (getDTRLoss(player.getLocation()));
    }

    public double getDTRLoss(Location location) {
        double dtrLoss = 1.00D;

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            dtrLoss = 0.01D;
        }

        Team ownerTo = LandBoard.getInstance().getTeam(location);
        if (Foxtrot.getInstance().getConquestHandler().getGame() != null && location.getWorld().getEnvironment() == Environment.THE_END && ownerTo != null && ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
            dtrLoss = Math.min(dtrLoss, 0.50D);
        }

        if (ownerTo != null) {
            if (ownerTo.hasDTRBitmask(DTRBitmask.QUARTER_DTR_LOSS)) {
                dtrLoss = Math.min(dtrLoss, 0.25D);
            } else if (ownerTo.hasDTRBitmask(DTRBitmask.REDUCED_DTR_LOSS)) {
                dtrLoss = Math.min(dtrLoss, 0.75D);
            }
        }

        return (dtrLoss);
    }

    public long getDeathban(Player player) {
        return (getDeathban(player.getUniqueId(), player.getLocation()));
    }

    public long getDeathban(UUID playerUUID, Location location) {
        // Things we already know and can easily eliminate.
        if (isPreEOTW()) {
            return (TimeUnit.DAYS.toSeconds(1000));
        } else if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return (TimeUnit.SECONDS.toSeconds(5));
        }

        Team ownerTo = LandBoard.getInstance().getTeam(location);
        Player player = Foxtrot.getInstance().getServer().getPlayer(playerUUID); // Used in various checks down below.

        // Check DTR flags, which will also take priority over playtime.
        if (ownerTo != null && ownerTo.getOwner() == null) {
            Event linkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(ownerTo.getName());

            // Only respect the reduced deathban if
            // The KOTH is non-existant (in which case we're probably
            // something like a 1v1 arena) or it is active.
            // If it's there but not active,
            // the null check will be false, the .isActive will be false, so we'll ignore
            // the reduced DB check.
            if (linkedKOTH == null || linkedKOTH.isActive()) {
                if (ownerTo.hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)) {
                    return (TimeUnit.MINUTES.toSeconds(5));
                } else if (ownerTo.hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)) {
                    return (TimeUnit.MINUTES.toSeconds(15));
                }
            }
        }

        return Deathban.getDeathbanSeconds(player);
    }

    public void beginHQWarp(final Player player, final Team team, int warmup, boolean charge) {
        Team inClaim = LandBoard.getInstance().getTeam(player.getLocation());

        // quick fix
        if (team.getBalance() < 0) {
            team.setBalance(0);
        }

        if (inClaim != null) {
            if (Foxtrot.getInstance().getServerHandler().isTeams() && inClaim.getOwner() != null && !inClaim.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You may not go to your team headquarters from an enemy's claim! Use '/f stuck' first.");
                return;
            }

            if (inClaim.getOwner() == null && (inClaim.hasDTRBitmask(DTRBitmask.KOTH) || inClaim.hasDTRBitmask(DTRBitmask.CITADEL) || inClaim.hasDTRBitmask(DTRBitmask.CONQUEST))) {
                player.sendMessage(ChatColor.RED + "You may not go to your team headquarters from inside of events!");
                return;
            }

            if (inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE) || CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
                if (player.getWorld().getEnvironment() != Environment.THE_END) {
                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot teleport to your end headquarters while you're in end spawn!");
                }
                return;
            }
        }


        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not go to your team headquarters while spawn tagged!");
            return;
        }

        player.sendMessage(ChatColor.YELLOW + "Teleporting to your team's HQ in " + ChatColor.LIGHT_PURPLE + warmup + " seconds" + ChatColor.YELLOW + "... Stay still and do not take damage.");

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Your PvP Timer will be removed if the teleport is not cancelled.");
        }

        homeTimer.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmup));

        final int finalWarmup = warmup;

        new BukkitRunnable() {

            int time = finalWarmup;
            Location startLocation = player.getLocation();
            double startHealth = player.getHealth();

            @Override
            public void run() {
                time--;

                if (!player.getLocation().getWorld().equals(startLocation.getWorld()) || player.getLocation().distanceSquared(startLocation) >= 0.1 || player.getHealth() < startHealth) {
                    player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
                    homeTimer.remove(player.getName());
                    cancel();
                    return;
                }

                // Reset their previous health, so players can't start on 1/2 a heart, splash, and then be able to take damage before warping.
                startHealth = player.getHealth();

                // Prevent server lag from making the home time inaccurate.
                if (homeTimer.containsKey(player.getName()) && homeTimer.get(player.getName()) <= System.currentTimeMillis()) {
                    if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        Foxtrot.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                        LunarClientListener.updateNametag(player);
                    }

                    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                        if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                            enderPearl.remove();
                        }
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                    homeTimer.remove(player.getName());
                    cancel();
                    return;
                }

                if (time == 0) {
                    // Remove their PvP timer.
                    if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        player.removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
                        Foxtrot.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                        LunarClientListener.updateNametag(player);
                    }

                    for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                        if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                            enderPearl.remove();
                        }
                    }

                    player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.RED + team.getName() + ChatColor.YELLOW + "'s HQ.");
                    player.teleport(team.getHQ());
                    homeTimer.remove(player.getName());
                    cancel();
                }
            }

        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
    }

    private Map<UUID, Long> playerDamageRestrictMap = Maps.newHashMap();

    public void disablePlayerAttacking(final Player player, int seconds) {
        if (seconds == 10) {
            player.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
        }

        playerDamageRestrictMap.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    private void registerPlayerDamageRestrictionListener() {
    		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
    			@EventHandler(ignoreCancelled = true)
    			public void onDamage(EntityDamageByEntityEvent event) {
    				Long expiry = playerDamageRestrictMap.get(event.getDamager().getUniqueId());
    				if (expiry != null && System.currentTimeMillis() < expiry) {
    					event.setCancelled(true);
    				}
    			}

    			@EventHandler
    			public void onQuit(PlayerQuitEvent event) {
    				playerDamageRestrictMap.remove(event.getPlayer().getUniqueId());
    			}
    		}, Foxtrot.getInstance());
    }

    public boolean isCloseToSpawn(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL){
            return (false);
        }

        if (loc.getWorld().getName().equalsIgnoreCase("kits_events")) {
            return (false);
        }
        int radius = 100;
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public boolean isSpawnBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NORMAL){
            return (false);
        }

        if (loc.getWorld().getName().equalsIgnoreCase("kits_events")) {
            return (false);
        }
        int radius = Foxtrot.getInstance().getMapHandler().getWorldBuffer();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    public boolean isNetherBufferZone(Location loc) {
        if (loc.getWorld().getEnvironment() != Environment.NETHER){
            return (false);
        }

        int radius = Foxtrot.getInstance().getMapHandler().getNetherBuffer();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        return ((x < radius && x > -radius) && (z < radius && z > -radius));
    }

    //itemstack -> amount -> price
    public Pair<ItemStack, Pair<Integer, Long>> extractSignData(Sign sign) {
        ItemStack itemStack = (sign.getLine(2).contains("Crowbar") ? InventoryUtils.CROWBAR : ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", "")));

        if (itemStack == null && sign.getLine(2).contains("Antidote")) {
            itemStack = InventoryUtils.ANTIDOTE;
        }

        if (itemStack == null) {
            System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
            return null;
        }

        return null;
    }

    public void handleShopSign(Sign sign, Player player) {
        ItemStack itemStack = ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", ""));

        if (itemStack == null) {
            if (sign.getLine(2).contains("Crowbar")) {
                itemStack = InventoryUtils.CROWBAR;
            } else if (sign.getLine(2).contains("Antidote")) {
                itemStack = InventoryUtils.ANTIDOTE;
            } else if (sign.getLine(2).contains("Zombie Spawner")) {
                itemStack = InventoryUtils.ZOMBIE_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Skele Spawner")) {
                itemStack = InventoryUtils.SKELETON_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Spider Spawner")) {
                itemStack = InventoryUtils.SPIDER_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Cave Spawner")) {
                itemStack = InventoryUtils.CAVE_SPIDER_SPAWNER.clone();
            }
        }

        if (itemStack == null && !sign.getLine(2).equalsIgnoreCase("valuables")) {
            System.err.println("Failed to handle shop sign, null item: " + sign.getLine(2).toLowerCase().replace(" ", ""));
            return;
        }

        if (sign.getLine(0).toLowerCase().contains("buy")) {
            int price;
            int amount;

            try {
                price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));
            } catch (NumberFormatException e) {
                return;
            }

            if (Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()) >= price) {

                if (Double.isNaN(Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()))) {
                    Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 0);
                    player.sendMessage("§cYour balance was fucked, but we unfucked it.");
                    return;
                }

                if (player.getInventory().firstEmpty() != -1) {
                    Foxtrot.getInstance().getEconomyHandler().withdraw(player.getUniqueId(), price);

                    itemStack.setAmount(amount);
                    player.getInventory().addItem(itemStack);
                    player.updateInventory();

                    showSignPacket(player, sign,
                            "§aBOUGHT§r " + amount,
                            "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(price),
                            "New Balance:",
                            "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()))
                    );
                } else {
                    showSignPacket(player, sign,
                            "§c§lError!",
                            "",
                            "§cNo space",
                            "§cin inventory!"
                    );
                }
            } else {
                showSignPacket(player, sign,
                        "§cInsufficient",
                        "§cfunds for",
                        sign.getLine(2),
                        sign.getLine(3)
                );
            }
        } else if (sign.getLine(0).toLowerCase().contains("sell")) {
            double pricePerItem;
            int amount;

            try {
                int price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                amount = Integer.parseInt(sign.getLine(1));

                pricePerItem = (float) price / (float) amount;
            } catch (NumberFormatException e) {
                return;
            }

            int amountInInventory = Math.min(amount, countItems(player, itemStack.getType(), itemStack.getDurability()));

            if (amountInInventory == 0) {
                showSignPacket(player, sign,
                        "§cYou do not",
                        "§chave any",
                        sign.getLine(2),
                        "§con you!"
                );
            } else {
                int totalPrice = (int) (amountInInventory * pricePerItem);

                removeItem(player, itemStack, amountInInventory);
                player.updateInventory();

                Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), totalPrice);

                showSignPacket(player, sign,
                        "§aSOLD§r " + amountInInventory,
                        "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(totalPrice),
                        "New Balance:",
                        "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()))
                );
            }
        }
    }

    public void handleKitSign(Sign sign, Player player) {
        String kit = ChatColor.stripColor(sign.getLine(1));

        if (kit.equalsIgnoreCase("Fishing")){
            int uses = Foxtrot.getInstance().getFishingKitMap().getUses(player.getUniqueId());

            if (uses == 3){
                showSignPacket(player, sign, "§aFishing Kit:", "", "§cAlready used", "§c3/3 times!");
            } else {
                ItemStack rod = new ItemStack(Material.FISHING_ROD);

                rod.addEnchantment(Enchantment.LURE, 2);
                player.getInventory().addItem(rod);
                player.updateInventory();
                player.sendMessage(ChatColor.GOLD + "Equipped the " + ChatColor.WHITE + "Fishing" + ChatColor.GOLD + " kit!");
                Foxtrot.getInstance().getFishingKitMap().setUses(player.getUniqueId(), uses + 1);
                showSignPacket(player, sign, "§aFishing Kit:", "§bEquipped!", "", "§dUses: §e" + (uses + 1) + "/3");
            }
        }
    }

    public void removeItem(Player p, ItemStack it, int amount) {
        boolean specialDamage = it.getType().getMaxDurability() == (short) 0;

        for (int a = 0; a < amount; a++) {
            for (ItemStack i : p.getInventory()) {
                if (i != null) {
                    if (i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
                        if (i.getAmount() == 1) {
                            p.getInventory().clear(p.getInventory().first(i));
                        } else {
                            i.setAmount(i.getAmount() - 1);
                        }
                        break;
                    }
                }
            }
        }

    }

    public ItemStack generateDeathSign(String killed, String killer) {
        ItemStack deathsign = new ItemStack(Material.SIGN);
        ItemMeta meta = deathsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§4" + killed);
        lore.add("§eSlain By:");
        lore.add("§a" + killer);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§dDeath Sign");
        deathsign.setItemMeta(meta);

        return (deathsign);
    }

    public ItemStack generateKOTHSign(String koth, String capper, EventType eventType) {
        ItemStack kothsign = new ItemStack(Material.SIGN);
        ItemMeta meta = kothsign.getItemMeta();

        ArrayList<String> lore = new ArrayList<>();

        lore.add("§9" + koth);
        lore.add("§eCaptured By:");
        lore.add("§a" + capper);

        DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

        lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

        meta.setLore(lore);
        meta.setDisplayName("§d" + eventType.name() + "Capture Sign");
        kothsign.setItemMeta(meta);

        return (kothsign);
    }

    private HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

    public void showSignPacket(Player player, final Sign sign, String... lines) {
        player.sendSignChange(sign.getLocation(), lines);

        if (showSignTasks.containsKey(sign)) {
            showSignTasks.remove(sign).cancel();
        }

        BukkitRunnable br = new BukkitRunnable() {

            @Override
            public void run(){
                sign.update();
                showSignTasks.remove(sign);
            }

        };

        showSignTasks.put(sign, br);
        br.runTaskLater(Foxtrot.getInstance(), 90L);
    }

    public int countItems(Player player, Material material, int damageValue) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();
        int amount = 0;

        for (ItemStack item : items) {
            if (item != null) {
                boolean specialDamage = material.getMaxDurability() == (short) 0;

                if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short) damageValue)) {
                    amount += item.getAmount();
                }
            }
        }

        return (amount);
    }

    public String getNick(Player player) {
        if (!this.nickName.containsKey(player.getUniqueId())) {
            return player.getName();
        }

        return this.nickName.get(player.getUniqueId());
    }

    @AllArgsConstructor
    private static class PotionStatus {

        private boolean drinkables;
        private boolean splash;
        private int maxLevel;

    }

}
