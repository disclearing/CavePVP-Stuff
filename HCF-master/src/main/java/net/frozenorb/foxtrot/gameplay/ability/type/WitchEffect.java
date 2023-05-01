package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WitchEffect extends Ability {
    private Map<PotionEffect, String> potionEffects = new HashMap<>();

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getDescription() {
        return "A witch that give you passive effects has been spawned.";
    }

    public WitchEffect() {
        super();
        this.hassanStack.setDurability((byte) 66);
        ItemMeta meta = this.hassanStack.getItemMeta();

        this.hassanStack.setItemMeta(meta);

        potionEffects.put(new PotionEffect(PotionEffectType.SPEED,20*6, 2), "&bSpeed III");
        potionEffects.put(new PotionEffect(PotionEffectType.INCREASE_DAMAGE,20*6, 1), "&cStrength II");
        potionEffects.put(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*6, 2), "&3Resistance III");
        potionEffects.put(new PotionEffect(PotionEffectType.REGENERATION,20*6, 2), "&dRegeneration III");
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.MONSTER_EGG;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Witch Effect";
    }

    @Override
    public boolean inPartnerPackage() {
        return true;
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fSpawn a witch that gives you passive"));
        toReturn.add(ChatColor.translate("&6❙ &fbard effects: strength, regeneration,"));
        toReturn.add(ChatColor.translate("&6❙ &fand resistance."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &4&lCave Crate&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }



    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand()) || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        player.updateInventory();

        final Witch witch = (Witch) player.getWorld().spawnEntity(player.getLocation(), EntityType.WITCH);

        witch.setMetadata("WITCH_EFFECT", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
        witch.setCustomNameVisible(true);
        witch.setCustomName(ChatColor.translate("&c" + player.getName() + "'s Witch &4&l8❤"));

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {

            if (!witch.isDead()) {
                witch.remove();
            }

            if (player.isOnline()) {
                player.sendMessage(ChatColor.RED + "Your Witch has despawned!");
            }

        }, 20*60*3);

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        new BukkitRunnable() {

            int seconds = 0;

            @Override
            public void run() {

                if (witch.isDead()) {
                    player.sendMessage(ChatColor.RED + "Your Witch is now dead!");
                    this.cancel();
                    return;
                }

                final Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                final List<Player> targetPlayers = new ArrayList<>(Collections.singletonList(player));

                if (checkTeam != null) {
                    targetPlayers.addAll(checkTeam.getOnlineMembers());
                    targetPlayers.remove(player);
                    targetPlayers.remove(player);
                    targetPlayers.add(player);
                }

                seconds += 2;

                for (Player targetPlayer : targetPlayers) {
                    if (!witch.getLocation().getWorld().getName().equalsIgnoreCase(targetPlayer.getWorld().getName())) {
                        continue;
                    }

                    if (witch.getLocation().distance(targetPlayer.getLocation()) <= 15) {
                        if (seconds == 10) {
                            final PotionEffect potionEffect = new ArrayList<>(potionEffects.keySet()).get(ThreadLocalRandom.current().nextInt(potionEffects.size()));

                            targetPlayer.sendMessage(ChatColor.translate("&aYou have been given " + potionEffects.get(potionEffect) + "&a by the &f" + getDisplayName() + "&a!"));
                            targetPlayer.addPotionEffect(potionEffect, true);
                        }

                        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0));
                        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0));
                        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0));
                    }
                }

                seconds = 0;
            }
        }.runTaskTimer(Foxtrot.getInstance(), 0, 40);

        if (team == null) {
            this.applyCooldown(player);
            return;
        }

        this.applyCooldown(team, player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Witch)) {
            return;
        }

        final Witch witch = (Witch) event.getEntity();
        final String customName = witch.getCustomName();

        if (customName == null) {
            return;
        }

        final String playersName = ChatColor.stripColor(customName).replace("'s Witch", "");

        final Player player = Foxtrot.getInstance().getServer().getPlayer(playersName);

        if (player == null) {
            return;
        }

        player.sendMessage(ChatColor.RED + "Your Witch has died!");
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Witch) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();
        final Witch target = (Witch) event.getEntity();
        final String customName = target.getCustomName();

        if (customName == null) {
            return;
        }

        UUID targetID = UUID.fromString(target.getMetadata("WITCH_EFFECT").get(0).asString());

        final Player player = Foxtrot.getInstance().getServer().getPlayer(targetID);
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(targetID);

        if (player != null && player.getName().equalsIgnoreCase(damager.getName())) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.RED + "You can't hurt your own Witch!");
            return;
        }

        if (player != null && team != null && team.isMember(damager.getUniqueId())) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.RED + "You can't hurt your teammates Witch!");
            return;
        }

        int value = target.hasMetadata("WITCH_HIT") ? (target.getMetadata("WITCH_HIT").get(0).asInt() + 1) : 1;

        target.setMetadata("WITCH_HIT", new FixedMetadataValue(Foxtrot.getInstance(), value));

        target.setCustomName(ChatColor.translate("&5&lWitch Effect &4&l" + (8-value) + "❤"));

        if (value != 8) {
            event.setDamage(0);
            return;
        }

        target.removeMetadata("WITCH_HIT", Foxtrot.getInstance());
        target.setHealth(0);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onTarget(EntityTargetEvent event) {
        if (event.getEntity() instanceof Witch) {
            event.setCancelled(true);
        }
    }
 }
