package net.frozenorb.foxtrot.listener;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.serialization.LocationSerializer;
import cc.fyre.proton.util.PlayerUtils;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.listener.event.PlayerIncreaseKillEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;

import cc.fyre.proton.serialization.PlayerInventorySerializer;
import net.minecraft.server.v1_7_R4.*;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.suge.Suge;

import java.util.*;

public class CombatLoggerListener implements Listener {

    public static final String COMBAT_LOGGER_METADATA = "CombatLogger";
    @Getter
    private Set<Entity> combatLoggers = new HashSet<>();

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {

        boolean hasCombatLoggerMeta = event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA);
        if (hasCombatLoggerMeta) {
            boolean isKitMap = Foxtrot.getInstance().getMapHandler().isKitMap();
            combatLoggers.remove(event.getEntity());
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (!metadata.playerName.equals(event.getEntity().getCustomName().substring(2))) {
                Foxtrot.getInstance().getLogger().warning("Combat logger name doesn't match metadata for " + metadata.playerName + " (" + event.getEntity().getCustomName().substring(2) + ")");
            }

            Foxtrot.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + event.getEntity().getLocation().getBlockX() + ", " + event.getEntity().getLocation().getBlockY() + ", " + event.getEntity().getLocation().getBlockZ() + ") died.");

            // Deathban the player
            Foxtrot.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);

            // Increment players deaths
            int deaths = Foxtrot.getInstance().getDeathsMap().getDeaths(metadata.playerUUID);
            Foxtrot.getInstance().getDeathsMap().setDeaths(metadata.playerUUID, deaths + 1);

            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            // Take away DTR.
            if (team != null) {
                team.setDeaths(team.getDeaths()+1);
                team.playerDeath(metadata.playerName, Foxtrot.getInstance().getServerHandler().getDTRLoss(event.getEntity().getLocation()), event.getEntity().getKiller(), false);
            }

            // Drop the player's items.
            for (ItemStack item : metadata.contents) {
                event.getDrops().add(item);
            }
            for (ItemStack item : metadata.armor) {
                event.getDrops().add(item);
            }

            Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(metadata.playerUUID).addDeath();
            Profiles.getInstance().getReputationHandler().takeReputation(metadata.playerUUID, metadata.playerName, 1);

            // store the death amount -- we'll use this later on.
            int victimKills = Foxtrot.getInstance().getKillsMap().getKills(event.getEntity().getUniqueId());

            if (isKitMap) {
                victimKills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getUniqueId()).getKills();
            }

            if (event.getEntity().getKiller() != null) {
                // give them a kill
                Foxtrot.getInstance().getKillsMap().setKills(event.getEntity().getKiller().getUniqueId(), Foxtrot.getInstance().getKillsMap().getKills(event.getEntity().getKiller().getUniqueId()) + 1);
                Foxtrot.getInstance().getKillstreakMap().setKillstreak(event.getEntity().getUniqueId(), Foxtrot.getInstance().getKillstreakMap().getKillstreak(event.getEntity().getUniqueId()));

                Bukkit.getPluginManager().callEvent(new PlayerIncreaseKillEvent(event.getEntity().getKiller()));

                final Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller());

                if (killerTeam != null) {
                    killerTeam.setKills(killerTeam.getKills() + 1);
                    killerTeam.recalculateGems();
                    if (CustomTimerCreateCommand.isDoublePoints() || killerTeam.hasEndOutpost()) {
                        killerTeam.setDoublePoints(killerTeam.getDoublePoints() + 1);
                    }
                }

                // store the kill amount -- we'll use this later on.
                int killerKills;

                Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).addKill();
                Profiles.getInstance().getReputationHandler().addReputation(event.getEntity().getKiller().getUniqueId(), event.getEntity().getKiller().getName(), 1);

                killerKills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(event.getEntity().getKiller()).getKills();

                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + event.getEntity().getKiller().getName() + ChatColor.DARK_RED + "[" + killerKills + "]" + ChatColor.YELLOW + ".";

                for (Player player : Bukkit.getOnlinePlayers()) {

                    if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                        player.sendMessage(deathMessage);
                    } else {
                        if (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller().getUniqueId()).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }

                // Add the death sign.

//              if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
//                  event.getDrops().add(Foxtrot.getInstance().getServerHandler().generateDeathSign(metadata.playerName, event.getEntity().getKiller().getKitName()));
//              }
            } else {
                String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                        player.sendMessage(deathMessage);
                    } else {
                        if (Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()) == null) {
                            continue;
                        }

                        if (Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID) != null
                                && Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID).equals(Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId()))) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }
            }

            Player target = Foxtrot.getInstance().getServer().getPlayer(metadata.playerUUID);

            if (target == null) {
                // Create an entity to load the player data
                MinecraftServer server = ((CraftServer) Foxtrot.getInstance().getServer()).getServer();
                EntityPlayer entity = new EntityPlayer(
                        server,
                        server.getWorldServer(0),
                        new GameProfile(metadata.playerUUID, metadata.playerName),
                        new PlayerInteractManager(server.getWorldServer(0)));
                target = entity.getBukkitEntity();

                if (target != null) {
                    target.loadData();
                }
            }

            if (target != null) {
                EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                target.getInventory().setContents(metadata.contents);
                target.getInventory().setArmorContents(metadata.armor);
                humanTarget.setHealth(0);

                if (Foxtrot.getInstance().getConquestHandler().getGame() != null) {
                    Foxtrot.getInstance().getConquestHandler().getGame().death(target);
                }

                spoofWebsiteData(target, event.getEntity().getKiller());

                target.getInventory().clear();
                target.getInventory().setArmorContents(null);
                target.saveData();
            }

            LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

            event.getEntity().remove();
        }
    }

    // Prevent trading with the logger.
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    // Kill loggers when their chunk unloads
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata(COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                entity.remove();
            }
        }
    }

    // Don't let the NPC go through portals
    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }
    }

    // Despawn the NPC when its owner joins.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Iterator<Entity> combatLoggerIterator = combatLoggers.iterator();

        while (combatLoggerIterator.hasNext()) {
            Villager villager = (Villager) combatLoggerIterator.next();

            if (villager.isCustomNameVisible() && ChatColor.stripColor(villager.getCustomName()).equals(event.getPlayer().getName())) {
                villager.remove();
                combatLoggerIterator.remove();
            }
        }
    }

    // Prevent combat logger friendly fire.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null) {
            CombatLoggerMetadata metadata = (CombatLoggerMetadata) event.getEntity().getMetadata(COMBAT_LOGGER_METADATA).get(0).value();

            if (DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
                event.setCancelled(true);
                return;
            }

//            if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getEntity().getLocation())) {
//                ((EntityLiving) ((CraftEntity) event.getEntity()).getHandle()).knockbackReduction = 1D;
//            }

            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

            if (team != null && team.isMember(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime(damager));
        }
    }

    // Prevent combatloggers from activating a pressure plate
    @EventHandler
    public void onEntityPressurePlate(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.STONE_PLATE && event.getEntity() instanceof Villager && event.getEntity().hasMetadata(COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true); // block is stone, entity is a combat tagged villager
        }
    }

    // Spawn the combat logger
    @EventHandler(priority = EventPriority.LOWEST) // So we run before Mod Suite code will.
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getPlayer())) {
            return;
        }
        // If the player safe logged out
        if (event.getPlayer().hasMetadata("loggedout")) {
            event.getPlayer().removeMetadata("loggedout", Foxtrot.getInstance());
            return;
        }

        if (ModHandler.INSTANCE.isInModMode(event.getPlayer().getUniqueId()) || ModHandler.INSTANCE.isInVanish(event.getPlayer().getUniqueId())) {
            return;
        }

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        // If the player is in spawn
        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            return;
        }

        // If they have a PvP timer.
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null &&
                Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame() &&
                Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(event.getPlayer().getUniqueId())) {
            return;
        }

        // If they're dead.
        if (event.getPlayer().isDead()) {
            return;
        }

        // If the player is below Y = 0 (aka in the void)
        if (event.getPlayer().getLocation().getBlockY() <= 0) {
            return;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(event.getPlayer())) {
            return;
        }

        if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
            return;
        }

        boolean spawnCombatLogger = false;

        for (Entity entity : event.getPlayer().getNearbyEntities(40, 40, 40)) {
            if (entity instanceof Player) {
                Player other = (Player) entity;

                if (ModHandler.INSTANCE.isInVanish(other.getUniqueId())) {
                    continue;
                }

                Team otherTeam = Foxtrot.getInstance().getTeamHandler().getTeam(other);
                Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

                if (otherTeam != playerTeam || playerTeam == null) {
                    spawnCombatLogger = true;
                    break;
                }
            }
        }

        if (!event.getPlayer().isOnGround()) {
            spawnCombatLogger = true;
        }

        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && !ModHandler.INSTANCE.isInVanish(event.getPlayer().getUniqueId()) && spawnCombatLogger && !event.getPlayer().isDead()) {
            Foxtrot.getInstance().getLogger().info(event.getPlayer().getName() + " combat logged at (" + event.getPlayer().getLocation().getBlockX() + ", " + event.getPlayer().getLocation().getBlockY() + ", " + event.getPlayer().getLocation().getBlockZ() + ")");

            ItemStack[] armor = event.getPlayer().getInventory().getArmorContents();
            ItemStack[] inv = event.getPlayer().getInventory().getContents();

            final Villager villager = (Villager) event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.VILLAGER);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 100));
            //villager.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 100));

            if (event.getPlayer().hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) {
                for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                    // have to use .equals() as PotionEffectType isn't an enum
                    if (potionEffect.getType().equals(PotionEffectType.FIRE_RESISTANCE)) {
                        villager.addPotionEffect(potionEffect);
                        break;
                    }
                }
            }

            CombatLoggerMetadata metadata = new CombatLoggerMetadata();

            metadata.playerName = event.getPlayer().getName();
            metadata.playerUUID = event.getPlayer().getUniqueId();
            metadata.deathBanTime = Foxtrot.getInstance().getServerHandler().getDeathban(metadata.playerUUID, event.getPlayer().getLocation());
            metadata.contents = inv;
            metadata.armor = armor;

            villager.setMetadata(COMBAT_LOGGER_METADATA, new FixedMetadataValue(Foxtrot.getInstance(), metadata));

            villager.setMaxHealth(calculateCombatLoggerHealth(event.getPlayer()));
            villager.setHealth(villager.getMaxHealth());

            villager.setCustomName(ChatColor.YELLOW.toString() + event.getPlayer().getName());
            villager.setCustomNameVisible(true);

            villager.setFallDistance(event.getPlayer().getFallDistance());
            villager.setRemoveWhenFarAway(false);
            villager.setVelocity(event.getPlayer().getVelocity());

            combatLoggers.add(villager);

            new BukkitRunnable() {

                public void run() {
                    if (!villager.isDead() && villager.isValid()) {
                        combatLoggers.remove(villager);
                        villager.remove();
                    }
                }

            }.runTaskLater(Foxtrot.getInstance(), 30 * 20L);

            if (villager.getWorld().getEnvironment() == World.Environment.THE_END) {
                // check every second if the villager fell out of the world and kill the player if that happened.
                new BukkitRunnable() {

                    int tries = 0;

                    @Override
                    public void run() {
                        if (villager.getLocation().getBlockY() >= 0) {
                            tries++;

                            if (tries == 30) {
                                cancel();
                            }
                            return;
                        }

                        Foxtrot.getInstance().getLogger().info(metadata.playerName + "'s combat logger at (" + villager.getLocation().getBlockX() + ", " + villager.getLocation().getBlockY() + ", " + villager.getLocation().getBlockZ() + ") died.");

                        // Deathban the player
                        Foxtrot.getInstance().getDeathbanMap().deathban(metadata.playerUUID, metadata.deathBanTime);
                        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(metadata.playerUUID);

                        // Take away DTR.
                        if (team != null) {
                            team.playerDeath(metadata.playerName, Foxtrot.getInstance().getServerHandler().getDTRLoss(villager.getLocation()), null, false);
                        }

                        // store the death amount -- we'll use this later on.
                        int victimKills = Foxtrot.getInstance().getKillsMap().getKills(metadata.playerUUID);

                        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                            victimKills = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(metadata.playerUUID).getKills();
                        }

                        String deathMessage = ChatColor.RED + metadata.playerName + ChatColor.DARK_RED + "[" + victimKills + "]" + ChatColor.GRAY + " (Combat-Logger)" + ChatColor.YELLOW + " died.";
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                                player.sendMessage(deathMessage);
                            } else {
                                if (team != null && team == Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId())) {
                                    player.sendMessage(deathMessage);
                                }
                            }
                        }

                        Player target = Foxtrot.getInstance().getServer().getPlayer(metadata.playerUUID);

                        if (target == null) {
                            // Create an entity to load the player data
                            MinecraftServer server = ((CraftServer) Foxtrot.getInstance().getServer()).getServer();
                            EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(metadata.playerUUID, metadata.playerName), new PlayerInteractManager(server.getWorldServer(0)));
                            target = entity.getBukkitEntity();

                            if (target != null) {
                                target.loadData();
                            }
                        }

                        if (target != null) {
                            EntityHuman humanTarget = ((CraftHumanEntity) target).getHandle();

                            target.getInventory().clear();
                            target.getInventory().setArmorContents(null);
                            humanTarget.setHealth(0);

                            spoofWebsiteData(target, villager.getKiller());
                            target.saveData();
                        }

                        LastInvCommand.recordInventory(metadata.playerUUID, metadata.contents, metadata.armor);

                        cancel();
                        villager.remove();
                    }

                }.runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
            }
        }
    }

    public double calculateCombatLoggerHealth(Player player) {
        int potions = 0;
        boolean gapple = false;

        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == Material.POTION && itemStack.getDurability() == (short) 16421) {
                potions++;
            } else if (!gapple && itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == (short) 1) {
                // Only let the player have one gapple count.
                potions += 15;
                gapple = true;
            }
        }

        return ((potions * 3.5D) + player.getHealth());
    }

    public static class CombatLoggerMetadata {

        private ItemStack[] contents;
        private ItemStack[] armor;
        private String playerName;
        private UUID playerUUID;
        private long deathBanTime;

    }

    private void spoofWebsiteData(Player player, Player killer) {
        final BasicDBObject playerDeath = new BasicDBObject();

        if (killer != null) {
            playerDeath.append("healthLeft", (int) killer.getHealth());
            playerDeath.append("killerUUID", killer.getUniqueId().toString());
            playerDeath.append("killerLastUsername", killer.getName());
            playerDeath.append("killerInventory", PlayerInventorySerializer.getInsertableObject(killer));
            playerDeath.append("killerPing", PlayerUtils.getPing(killer));
            playerDeath.append("killerHunger", killer.getFoodLevel());
            playerDeath.append("killerLocation", LocationSerializer.serialize(killer.getLocation()));

            final Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);

            playerDeath.append("killerTeam", (killerTeam == null ? "None Found" : killerTeam.getName()));
            playerDeath.append("killerDTR", (killerTeam == null ? "No Faction" : killerTeam.formatDTR()));
        } else {
            playerDeath.append("reason", "combat-logged");

            playerDeath.append("playerInventory", PlayerInventorySerializer.getInsertableObject(player));
            playerDeath.append("uuid", player.getUniqueId().toString().replace("-", ""));
            playerDeath.append("lastUsername", player.getName());
            playerDeath.append("hunger", player.getFoodLevel());
            playerDeath.append("ping", PlayerUtils.getPing(player));
            playerDeath.append("when", new Date());
            playerDeath.append("systemTime", System.currentTimeMillis());

            playerDeath.append("tps", 20);

            final Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

            playerDeath.append("playerTeam", (playerTeam == null ? "None Found" : playerTeam.getName()));
            playerDeath.append("beforeDTR", (playerTeam == null ? "No Faction" : playerTeam.formatDTR()));
            playerDeath.append("afterDTR", (playerTeam == null ? 0 : playerTeam.getDTR() - Foxtrot.getInstance().getServerHandler().getDTRLoss(player.getLocation())));

            playerDeath.append("playerLocation", LocationSerializer.serialize(player.getLocation()));

            playerDeath.put("_id", UUID.randomUUID().toString().replaceAll("-", ""));

            new BukkitRunnable() {

                public void run() {
                    Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
                }

            }.runTaskAsynchronously(Foxtrot.getInstance());
        }
    }
}