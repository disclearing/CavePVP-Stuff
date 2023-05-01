package net.frozenorb.foxtrot.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.util.ItemUtils;
import cc.fyre.proton.uuid.UUIDCache;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.collect.ImmutableSet;
import com.vexsoftware.votifier.model.VotifierEvent;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.commands.KillTheKingCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.commands.team.TeamStuckCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import net.frozenorb.foxtrot.team.event.TeamLeaveClaimEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.PotionUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticType;
import org.cavepvp.suge.Suge;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

public class FoxListener implements Listener {

    private Map<UUID, Integer> sessionVotes = new HashMap<>();
    public static final ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(FISHING_ROD);
    public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER);
    public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(LAVA_BUCKET, WATER_BUCKET, BUCKET);
    public static final Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(GLASS, WOOD_DOOR, IRON_DOOR, FENCE_GATE);
    public static final Set<Material> NO_INTERACT = ImmutableSet.of(
            FENCE_GATE, FURNACE, BURNING_FURNACE, BREWING_STAND, CHEST, HOPPER, DISPENSER, WOODEN_DOOR, STONE_BUTTON, WOOD_BUTTON, TRAPPED_CHEST,
            TRAP_DOOR, LEVER, DROPPER, ENCHANTMENT_TABLE, BED_BLOCK, ANVIL, BEACON
    );
    private static final List<UUID> processingTeleportPlayers = new CopyOnWriteArrayList<>();

    private static final List<String> SPAWN_COMMANDS = Arrays.asList(
            "pv", "vault", "chest", "vc", "playervaults",
            "playervaults:pv", "playervaults:vault", "playervaults:chest", "playervaults:vc", "playervaults:playervaults"
    );
    private static final List<String> DISALLOWED_COMMANDS = Arrays.asList(
            "pv", "vault", "chest", "vc", "playervaults",
            "playervaults:pv", "playervaults:vault", "playervaults:chest", "playervaults:vc", "playervaults:playervaults",
            "tinker", "tinkerer",
            "rename"
    );

    static {
        FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        processTerritoryInfo(event); // this only works because I'm lucky and PlayerTeleportEvent extends PlayerMoveEvent :0
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onMelt(BlockFadeEvent event) {
        if (event.getBlock().getWorld().getEnvironment() == World.Environment.NETHER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onVote(VotifierEvent event) {
        final Server server = Foxtrot.getInstance().getServer();
        final Player player = Foxtrot.getInstance().getServer().getPlayer(event.getVote().getUsername());

        if (player == null || !player.isOnline()) {
            return;
        }

        int votes = sessionVotes.getOrDefault(player.getUniqueId(), 0)+1;
        sessionVotes.put(player.getUniqueId(), votes);

        if (votes >= 3) {
            sessionVotes.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPortal(PlayerPortalEvent event) {
        processTerritoryInfo(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCombatCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        String cmd = command.split(" ")[0].replace("/", "");

        if (!player.isOp() && (event.getMessage().startsWith("/mv") || event.getMessage().startsWith("/multiverse-core:mv") || event.getMessage().startsWith("/multiverse"))) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "No permission");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }


        if (SPAWN_COMMANDS.contains(cmd) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can only do this at spawn.");
            return;
        }

        if (SpawnTagHandler.isTagged(event.getPlayer()) && DISALLOWED_COMMANDS.contains(cmd)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use this command in combat.");
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
                && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())
                && DISALLOWED_COMMANDS.contains(cmd)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot use this command in a game.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockDropItems(BlockDropItemsEvent event) {
        if (event.getToDrop().stream().anyMatch(it -> it.getItemStack().getType().equals(SNOW_BALL))) {
            event.getToDrop().clear();
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        if (player.getItemInHand() == null || player.getItemInHand().getType() != EYE_OF_ENDER || !player.getItemInHand().hasItemMeta() || !player.getItemInHand().getItemMeta().hasDisplayName() || !player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(RED + BOLD.toString() + "Teleporter")) {
            return;
        }

        player.sendMessage(LIGHT_PURPLE + "Wooooooooooooooooooooooooooosh to the border!");

        final Location location = this.findSuitableLocation(event.getBlock().getLocation().clone(), this.getDirection(player));
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        player.teleport(location);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
            Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()).getTaskId());
            ServerHandler.getTasks().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
        }

        processTerritoryInfo(event);
    }

    // GOLDEN APPLE NERF
    @EventHandler(priority = EventPriority.MONITOR)
    private void onConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack.getType() == GOLDEN_APPLE && itemStack.getDurability() != 0) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 16, 4), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 61, 0), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1, 0), true);
            }, 2);
        } else if (!Foxtrot.getInstance().getMapHandler().isKitMap() && itemStack.getType() == GOLDEN_APPLE && itemStack.getDurability() == 0) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 0), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 1), true);
            }, 2);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Foxtrot.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/to ") || event.getMessage().startsWith("/targetoffset ") || event.getMessage().startsWith("//targetoffset ") || event.getMessage().startsWith("//to ")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
        }

        if (event.getMessage().startsWith("/lives give") || event.getMessage().startsWith("/lives send") || event.getMessage().startsWith("/pvp sendlives") || event.getMessage().startsWith("/lives pay")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onXPChange(PlayerExpChangeEvent event) {
        final Player player = event.getPlayer();

        if (!player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            event.setAmount(event.getAmount()*2);
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        Foxtrot.getInstance().getPlaytimeMap().playerJoined(player.getUniqueId());
        Foxtrot.getInstance().getLastJoinMap().setLastJoin(player.getUniqueId());

        if (!player.hasPlayedBefore()) {
            Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "clickitem give random-spawner 1 " + player.getName());

            Foxtrot.getInstance().getFriendLivesMap().setLives(player.getUniqueId(), 3);

            if (!Foxtrot.getInstance().getServerHandler().isAu()) {
                PlayerProfileAPI.addStatistic(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getStatisticServer(), StatisticType.MAPS_PLAYED, 1);
            }

            Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), false);
            Foxtrot.getInstance().getFirstJoinMap().setFirstJoin(player.getUniqueId());

            if (Foxtrot.getInstance().getServerHandler().isTeams()) {
                Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 100D);
            } else {
                Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 1500D);
            }

            if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
                player.getInventory().addItem(FIRST_SPAWN_FISHING_ROD);
                player.getInventory().addItem(new ItemStack(COOKED_BEEF, 16));
            }

            //1:01AM

            if (CustomTimerCreateCommand.getCustomTimers().get("&a&lSOTW") == null) {
                if (Foxtrot.getInstance().getServerHandler().isStartingTimerEnabled()) {
                    player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
                    Foxtrot.getInstance().getPvPTimerMap().createStartingTimer(player.getUniqueId(), (int) TimeUnit.HOURS.toSeconds(1));
                } else {
                    player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
                    Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
                }
            }

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                World spawn = Foxtrot.getInstance().getServer().getWorld("Spawn");

                if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                    spawn = Foxtrot.getInstance().getServer().getWorld("world");
                }

                player.teleport(spawn.getSpawnLocation());
            }, 5);

            /* Populate these fields in mongo for Ariel, doesnt want them to be empty if player has no kills */
            if (Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()) == 0) {
                Foxtrot.getInstance().getDeathsMap().setDeaths(player.getUniqueId(), 0);
            }

            if (Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()) == 0) {
                Foxtrot.getInstance().getKillsMap().setKills(player.getUniqueId(), 0);
            }
        }

//        Team team = LandBoard.getInstance().getTeam(player.getLocation());

//        if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
//            player.setMetadata("join_invis", new FixedMetadataValue(Foxtrot.getInstance(), true));
//            for (Claim claim : team.getClaims()) {
//                for (Player target : claim.getPlayers()) {
//                    if (target == player) continue;
//                    if (target.hasMetadata("join_invis")) {
//                        target.hidePlayer(player);
//                    }
//                    player.hidePlayer(target);
//                }
//            }
//        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStealthPickaxe(BlockBreakEvent event) {
        Block block = event.getBlock();
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
            if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
                event.setCancelled(true);

                block.breakNaturally(inHand);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onStealthItemPickup(PlayerPickupItemEvent event) {
        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand.getType() == GOLD_PICKAXE && inHand.hasItemMeta()) {
            if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
                event.setCancelled(true);
                event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
                event.getItem().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (ServerHandler.getTasks().containsKey(player.getName())) {
                Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()).getTaskId());
                ServerHandler.getTasks().remove(player.getName());
                player.sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
            }
        }
    }

//    @EventHandler(priority = EventPriority.LOW)
//    private void onDamage(EntityDamageByEntityEvent event) {
//        if (!(event.getDamager() instanceof Wolf) || !(event.getEntity() instanceof Player)) {
//            return;
//        }
//
//        final Wolf wolf = (Wolf) event.getDamager();
//
//        if (wolf.hasMetadata("ATTACK_DOGS")) {
//            event.setDamage(event.getDamage()*10);
//        }
//    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onProjectileInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem().getType() == POTION) {
                ItemStack i = event.getItem();

                // We can't run Potion.fromItemStack on a water bottle.
                if (i.getDurability() != (short) 0) {
                    Potion pot = Potion.fromItemStack(i);

                    if (pot != null && pot.isSplash() && pot.getType() != null && DEBUFFS.contains(pot.getType().getEffectType())) {
                        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                            player.sendMessage(RED + "You cannot do this while your PVP Timer is active!");
                            player.sendMessage(RED + "Type '" + GRAY + "/pvp enable" + RED + "' to remove your timer.");
                            event.setCancelled(true);
                            return;
                        }

                        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(RED + "You cannot launch debuffs from inside spawn!");
                            event.getPlayer().updateInventory();
                        }
                    }
                }
            }
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (event.getClickedBlock().getType() == ENCHANTMENT_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null) {
                if (event.getItem().getType() == ENCHANTED_BOOK) {
                    event.getItem().setType(BOOK);

                    event.getPlayer().sendMessage(GREEN + "You reverted this book to its original form!");
                    event.setCancelled(true);
                }
            }

            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getClickedBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
            if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                if (event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("DOOR")) {
                    CitadelHandler citadelHandler = Foxtrot.getInstance().getCitadelHandler();

                    if (team.getName().equalsIgnoreCase("TreasureCove") && team.getOwner() == null || DTRBitmask.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer()) || DTRBitmask.OUTPOST.appliesAt(event.getClickedBlock().getLocation()) && player.getWorld().getEnvironment().equals(World.Environment.NORMAL) && playerTeam.hasRoadOutpost()) {
                        return;
                    }
                }

                if (event.getItem() != null && (event.getClickedBlock().getType() != HOPPER || player.isSneaking()) && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
                    Potion potion = Potion.fromItemStack(event.getItem());

                    if (potion.isSplash()) {
                        PotionUtil.splashPotion(player, event.getItem());
                        if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                            player.setItemInHand(null);
                            player.updateInventory();
                        } else {
                            InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                        }
                    }
                }

                event.setCancelled(true);
                event.getPlayer().sendMessage(YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + YELLOW + "'s territory.");

                if (event.getMaterial() == TRAP_DOOR || event.getMaterial() == FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
                    Foxtrot.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                }

                return;
            }

            if (event.getAction() == Action.PHYSICAL) {

                if (Foxtrot.getInstance().getMapHandler().isKitMap() && Foxtrot.getInstance().getServerHandler().isWarzone(event.getClickedBlock().getLocation())) {
                    return;
                }

                event.setCancelled(true);
            }
        } else if (event.getMaterial() == LAVA_BUCKET) {
            if (team == null || !team.isMember(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(RED + "You can only do this in your own claims!");
            }
        } else {
            UUID uuid = player.getUniqueId();

            if (team != null && !team.isCaptain(uuid) && !team.isCoLeader(uuid) && !team.isOwner(uuid)) {
                Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

                if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                    if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(YELLOW + "You do not have access to the subclaim " + GREEN + subclaim.getName() + YELLOW + "!");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignInteract(final PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign s = (Sign) event.getClickedBlock().getState();

                if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
                    if (s.getLine(0).contains("Kit")) {
                        Foxtrot.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
                    } else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
                        Foxtrot.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
                    }

                    event.setCancelled(true);
                }
            }
        }

        if (event.getItem() != null && event.getMaterial() == SIGN) {
            if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) event.getItem().getItemMeta().getLore();

                if (lore.size() > 1 && lore.get(1).contains("§e")) {
                    if (event.getClickedBlock() != null) {
                        event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(Foxtrot.getInstance(), true));

                        new BukkitRunnable() {

                            public void run() {
                                event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", Foxtrot.getInstance());
                            }

                        }.runTaskLater(Foxtrot.getInstance(), 20L);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBeacon(BlockPlaceEvent event) {
        if ((event.getBlockPlaced().getType() == BEACON || event.getBlockPlaced().getType() == BEDROCK) && !event.getPlayer().isOp()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(RED + "You can't place " + ItemUtils.getName(event.getPlayer().getItemInHand()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        if (hand.getType() == SIGN) {
            if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
                ArrayList<String> lore = (ArrayList<String>) hand.getItemMeta().getLore();

                if (event.getBlock().getType() == WALL_SIGN || event.getBlock().getType() == SIGN_POST) {
                    Sign s = (Sign) event.getBlock().getState();

                    for (int i = 0; i < 4; i++) {
                        s.setLine(i, lore.get(i));
                    }

                    s.setMetadata("deathSign", new FixedMetadataValue(Foxtrot.getInstance(), true));
                    s.update();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSpawner(SpawnerSpawnEvent event) {
        if (!(event.getEntity() instanceof EnderDragon) && !(event.getEntity() instanceof Wither) && !(event.getEntity() instanceof Giant)) {
            return;
        }

        final Location location = event.getLocation();

        event.setCancelled(true);

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

            if (onlinePlayer.hasPermission("neutron.staff")) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(ChatColor.translate("&4&l!!!! ALERT !!!!"));
                onlinePlayer.sendMessage(ChatColor.translate("&4&lAn " + event.getEntityType().name() + " has spawned! ALERT SIMPLY IMMEDIATELY!"));
                onlinePlayer.sendMessage(ChatColor.translate("&4&lLocation: " + location.getBlockX() + location.getBlockY() + location.getBlockZ()));
                onlinePlayer.sendMessage(ChatColor.translate("&4&l!!!! ALERT !!!!"));
                onlinePlayer.sendMessage("");
            }

        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final ItemStack itemStack = event.getItemInHand();

        if (event.isCancelled() || itemStack == null || itemStack.getType() != MOB_SPAWNER) {
            return;
        }

        if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
        final Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());

        if (team == null || teamAt == null || !teamAt.isMember(player.getUniqueId())) {
            player.sendMessage(RED + "You can only place a Spawner in your claim!");
            event.setCancelled(true);
            return;
        }

        boolean canPlace = false;

        String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).toLowerCase();

        if (player.hasPermission("opasofjsdigasduighasuitguasrtg")) {
            canPlace = true;
        } else if (displayName.contains("spider") || displayName.contains("skeleton") || displayName.contains("zombie")) {
            canPlace = true;
        }

        if (!canPlace) {
            player.sendMessage(ChatColor.RED + "You may not place this type of spawners!");
            return;
        }

        String name = stripColor(itemStack.getItemMeta().getDisplayName());
        String entName = name.replace(" Spawner", "");
        EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(type);
        spawner.update();

        event.getPlayer().sendMessage(GREEN + "You placed a " + entName + " spawner!");
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() == WALL_SIGN || e.getBlock().getType() == SIGN_POST) {
            if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
                e.setCancelled(true);

                Sign sign = (Sign) e.getBlock().getState();

                ItemStack deathsign = new ItemStack(SIGN);
                ItemMeta meta = deathsign.getItemMeta();

                if (sign.getLine(1).contains("Captured")) {
                    meta.setDisplayName("§dKOTH Capture Sign");
                } else {
                    meta.setDisplayName("§dDeath Sign");
                }

                meta.setLore(Arrays.asList(sign.getLines()));
                deathsign.setItemMeta(meta);
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

                e.getBlock().setType(AIR);
                e.getBlock().getState().removeMetadata("deathSign", Foxtrot.getInstance());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onHour(HourEvent event) {
        final Foxtrot instance = Foxtrot.getInstance();

        if (instance.getMapHandler().isKitMap() || instance.getServerHandler().isTeams()) {
            return;
        }

        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));

        final int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (instance.getMapHandler().isKitMap()) {

            if (day == Calendar.WEDNESDAY && event.getHour() == 0) {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 86400 &d&l2x Points");
            }

            if (day == Calendar.FRIDAY && event.getHour() == 15) {
                instance.getEventHandler().getEvents().stream().filter(Event::isActive).forEach(it -> {
                    instance.getServer().broadcastMessage("CONSOLE " + GOLD + " has cancelled " + WHITE + it.getName() + GOLD + ".");
                    it.setActive(false);
                });
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth Citadel");
            }

            if (day == Calendar.TUESDAY && event.getHour() == 16) {
                instance.getNetworkBoosterHandler().getBoostersQueued().put(UUIDCache.CONSOLE_UUID, Collections.singletonList(instance.getNetworkBoosterHandler().findBooster("Frenzy")));
            }

            if (day == Calendar.THURSDAY && event.getHour() == 13) {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 18000 &2&lDouble Gem");
            }
            return;
        }

        if (instance.getServerHandler().isAu()) {
            if (day == Calendar.THURSDAY && event.getHour() == 13) {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 86400 &d&l2x Points");
            }

            if (event.getHour() != 6) {
                return;
            }

            if (day == Calendar.SUNDAY || day == Calendar.TUESDAY) {
                instance.getEventHandler().getEvents().stream().filter(Event::isActive).forEach(it -> {
                    instance.getServer().broadcastMessage("CONSOLE " + GOLD + " has cancelled " + WHITE + it.getName() + GOLD + ".");
                    it.setActive(false);
                });
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth Citadel");
            }

            if (day == Calendar.MONDAY || day == Calendar.WEDNESDAY) {
                instance.getEventHandler().getEvents().stream().filter(Event::isActive).forEach(it -> {
                    instance.getServer().broadcastMessage("CONSOLE " + GOLD + " has cancelled " + WHITE + it.getName() + GOLD + ".");
                    it.setActive(false);
                });
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth NetherCitadel");
            }
            return;
        }

        if (day == Calendar.SUNDAY && event.getHour() == 15 || day == Calendar.THURSDAY && event.getHour() == 13) {
            instance.getEventHandler().getEvents().stream().filter(Event::isActive).forEach(it -> {
                instance.getServer().broadcastMessage("CONSOLE " + GOLD + " has cancelled " + WHITE + it.getName() + GOLD + ".");
                it.setActive(false);
            });
            instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth Citadel");
        }

        if (day == Calendar.MONDAY && event.getHour() == 12 || day == Calendar.TUESDAY && event.getHour() == 17) {
            instance.getEventHandler().getEvents().stream().filter(Event::isActive).forEach(it -> {
                instance.getServer().broadcastMessage("CONSOLE " + GOLD + " has cancelled " + WHITE + it.getName() + GOLD + ".");
                it.setActive(false);
            });
            instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth NetherCitadel");
        }

        if (day == Calendar.THURSDAY && event.getHour() == 0) {
            instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 86400 &d&l2x Points");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onArrow(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            event.setDamage(event.getDamage()*0.6);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onHighDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!DisableCommand.damage) {
            return;
        }

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }

        event.setDamage(event.getDamage()*1.125);
    }


    @EventHandler(priority = EventPriority.LOWEST)
    private void onVehicle(VehicleEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onVehiclePlace(PlayerInteractEvent event) {
        if (event.getAction() == null || event.getItem() == null) {
            return;
        }

        if (event.getAction().name().contains("RIGHT")) {
            if (event.getItem().getType() == MINECART || event.getItem().getType() == BOAT && event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(RED + "You can't place that vehicle here!");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());

        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity())) {
            return;
        }

        SpawnTagHandler.removeTag(event.getEntity());

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {

            if (KillTheKingCommand.king != null && event.getEntity().getUniqueId().toString().equalsIgnoreCase(KillTheKingCommand.king.toString())) {
                final Player killer = event.getEntity().getKiller();

                if (killer != null) {
                    Foxtrot.getInstance().getServer().broadcastMessage("");
                    Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.translate(Neutron.getInstance().getProfileHandler().findDisplayName(killer.getUniqueId()) + " &6killed the king!"));
                    Foxtrot.getInstance().getServer().broadcastMessage("");
                }

            }

            if (Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity()) || Foxtrot.getInstance().getInEventPredicate().test(event.getEntity())) {
                return;
            }
        }

        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
            Location deathLoc = event.getEntity().getLocation();
            int deathX = deathLoc.getBlockX();
            int deathY = deathLoc.getBlockY();
            int deathZ = deathLoc.getBlockZ();

            if (killerTeam != null) {
                TeamActionTracker.logActionAsync(killerTeam, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP, ImmutableMap.of("playerId", killer.getUniqueId(), "playerName", killer.getName(), "killedId", event.getEntity().getUniqueId(), "killedName", event.getEntity().getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
            }

            if (playerTeam != null) {
                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP, ImmutableMap.of("playerId", event.getEntity().getUniqueId(), "playerName", event.getEntity().getName(), "killerId", killer.getUniqueId(), "killerName", killer.getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
            }
        }

        if (playerTeam != null) {
            playerTeam.playerDeath(event.getEntity().getName(), Foxtrot.getInstance().getServerHandler().getDTRLoss(event.getEntity()), killer, Suge.getInstance().getEnchantHandler().findAllCustomEnchants(event.getEntity()).keySet().stream().anyMatch(it -> it.getName().equalsIgnoreCase("Evader")));
        }

        // Transfer money
        double bal = Foxtrot.getInstance().getEconomyHandler().getBalance(event.getEntity().getUniqueId());
        Foxtrot.getInstance().getEconomyHandler().withdraw(event.getEntity().getUniqueId(), bal);

        // Only tell player they earned money if they actually earned something
        if ((killer = event.getEntity().getKiller()) != null && !Double.isNaN(bal) && bal > 0) {
            Foxtrot.getInstance().getEconomyHandler().deposit(killer.getUniqueId(), bal);
            killer.sendMessage(GOLD + "You earned " + BOLD + "$" + bal + GOLD + " for killing " + event.getEntity().getDisplayName() + GOLD + "!");
        }
    }

    private void processTerritoryInfo(PlayerMoveEvent event) {

        Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {

            if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {

                if (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo())) {
                    Foxtrot.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
                    LunarClientListener.updateNametag(event.getPlayer());

                    event.getPlayer().sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");
                } else if (ownerTo != null && ownerTo.getOwner() != null) {
                    if (!ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
                        event.setCancelled(true);

                        for (Claim claim : ownerTo.getClaims()) {
                            if (claim.contains(event.getFrom()) && !ownerTo.isMember(event.getPlayer().getUniqueId())) {
                                Location nearest = TeamStuckCommand.nearestSafeLocation(event.getPlayer().getLocation());
                                boolean spawn = false;

                                if (nearest == null) {
                                    nearest = Foxtrot.getInstance().getServerHandler().getSpawnLocation();
                                    spawn = true;
                                }

                                event.getPlayer().teleport(nearest);
                                event.getPlayer().sendMessage(ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed.");
                                return;
                            }
                        }

                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another team's territory with PvP Protection.");
                        event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
                        return;
                    }
                }
            }
        }

        Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());

        if (ownerFrom != ownerTo) {
            ServerHandler sm = Foxtrot.getInstance().getServerHandler();
            RegionData from = sm.getRegion(ownerFrom, event.getFrom());
            RegionData to = sm.getRegion(ownerTo, event.getTo());

            if (from.equals(to)) return;

            if (!to.getRegionType().getMoveHandler().handleMove(event)) {
                return;
            }

            boolean fromReduceDeathban = from.getData() != null && (from.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));
            boolean toReduceDeathban = to.getData() != null && (to.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));

            if (fromReduceDeathban && from.getData() != null) {
                Event fromLinkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(from.getData().getName());

                if (fromLinkedKOTH != null && !fromLinkedKOTH.isActive()) {
                    fromReduceDeathban = false;
                }
            }

            if (toReduceDeathban && to.getData() != null) {
                Event toLinkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(to.getData().getName());

                if (toLinkedKOTH != null && !toLinkedKOTH.isActive()) {
                    toReduceDeathban = false;
                }
            }

            if (Foxtrot.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(event.getPlayer().getUniqueId())) {
                // create leaving message
                FancyMessage nowLeaving = new FancyMessage("Now leaving: ").color(YELLOW).then(from.getName(event.getPlayer())).color(YELLOW);

                if (ownerFrom != null) {
                    nowLeaving.command("/t i " + ownerFrom.getName()).tooltip(GREEN + "View team info");
                }

                nowLeaving.then(" (").color(YELLOW).then(fromReduceDeathban ? "Non-Deathban" : "Deathban").color(fromReduceDeathban ? GREEN : RED).then(")").color(YELLOW);

                // create entering message
                FancyMessage nowEntering = new FancyMessage("Now entering: ").color(YELLOW).then(to.getName(event.getPlayer())).color(WHITE);

                if (ownerTo != null) {
                    nowEntering.command("/t i " + ownerTo.getName()).tooltip(GREEN + "View team info");
                }

                nowEntering.then(" (").color(YELLOW).then(toReduceDeathban ? "Non-Deathban" : "Deathban").color(toReduceDeathban ? GREEN : RED).then(")").color(YELLOW);

                // send both
                nowLeaving.send(event.getPlayer());
                nowEntering.send(event.getPlayer());
            }

            final TeamEnterClaimEvent teamEnterClaimEvent = new TeamEnterClaimEvent(event.getPlayer(), event.getFrom(), event.getTo(), from.getData(), to.getData(), false);
            final TeamLeaveClaimEvent teamLeaveClaimEvent = new TeamLeaveClaimEvent(event.getPlayer(), event.getFrom(), event.getTo(), from.getData(), to.getData(), false);

            Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamEnterClaimEvent);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamLeaveClaimEvent);

            if (teamEnterClaimEvent.isCancelled() || teamLeaveClaimEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }
    }

    private BlockFace getDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return BlockFace.SOUTH;
        } else if (yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw < 315) {
            return BlockFace.EAST;
        }
        return BlockFace.NORTH;
    }

    private Location findSuitableLocation(Location to, BlockFace blockFace) {
        final Location newLocation = to.clone();

        int border = (to.getWorld().getEnvironment() == World.Environment.NORMAL ? Foxtrot.getInstance().getMapHandler().isKitMap() ? 2000 : 3000 : 1500);

        if (blockFace == BlockFace.NORTH) {
            newLocation.setZ(-border);
        }
        if (blockFace == BlockFace.SOUTH) {
            newLocation.setZ(border);
        }
        if (blockFace == BlockFace.WEST) {
            newLocation.setX(-border);
        }
        if (blockFace == BlockFace.EAST) {
            newLocation.setX(border);
        }

        newLocation.setY(to.getBlockY() + 1);

        return newLocation;
    }

}
