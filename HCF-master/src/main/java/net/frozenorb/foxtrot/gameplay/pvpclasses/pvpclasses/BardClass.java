package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.portablebard.PortableResistance;
import net.frozenorb.foxtrot.gameplay.ability.type.portablebard.PortableStrength;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.energy.EnergyEffect;
import net.frozenorb.foxtrot.gameplay.pvpclasses.event.BardEffectUseEvent;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BardClass extends PvPClass implements Listener {

    /*
            Things commented with // CUSTOM
            are the 'unique' abilities, or things that have custom behaviour not seen by most other effects.
            An example is invis, whose passive cannot be used while its click is on cooldown.
            This is therefore commented with // CUSTOM
     */

    public final Map<Material, EnergyEffect> BARD_CLICK_EFFECTS = new HashMap<>();
    public final Map<Material, EnergyEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();

    public static int BARD_RANGE = 30;
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final float ULTIMATE_PER_BARD_EFFECT = 10;

    public BardClass() {
        super("Bard", null, true);

        this.getPermanentEffects().add(PotionEffectType.SPEED);
        this.getPermanentEffects().add(PotionEffectType.REGENERATION);
        this.getPermanentEffects().add(PotionEffectType.DAMAGE_RESISTANCE);

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            BARD_RANGE = 35;
        }

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 20));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 45));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 46, 0), 40));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 35));

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));


        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            BARD_RANGE = 40;
            BARD_PASSIVE_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 6, 0)));
        } else {
            BARD_CLICK_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 46, 0), 40));
        }

    }

    @Override
    public boolean canApply(Player player) {
        if (!this.canUseEffect(player)) {
            return false;
        }

        return super.canApply(player);
    }

    @Override
    public boolean qualifies(Player player, PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.GOLD_HELMET &&
                armor.getChestplate().getType() == Material.GOLD_CHESTPLATE &&
                armor.getLeggings().getType() == Material.GOLD_LEGGINGS &&
                armor.getBoots().getType() == Material.GOLD_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
        }
    }

    @Override
    public void tick(Player player) {

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        }

        if (player.getItemInHand() != null && BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType()) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            // CUSTOM
            if (!canUseEffect(player)) {
                return;
            }
            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }
        super.tick(player);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (PvPClassHandler.hasKitOn(player, this)) {
            event.setDamage(event.getDamage()*0.8);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !BARD_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || !Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        if (!canUseEffect(event.getPlayer())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Bard effects cannot be used while in spawn.");
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
            return;
        }

        long cooldown = !Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().containsKey(event.getPlayer().getUniqueId()) ? 0 : Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().get(event.getPlayer().getUniqueId()) - System.currentTimeMillis();

        if (cooldown > 0 && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            double value = (cooldown / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        Float energy = Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().get(event.getPlayer().getUniqueId());

        EnergyEffect bardEffect = BARD_CLICK_EFFECTS.get(event.getItem().getType());

        if (bardEffect.getEnergy() > energy) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy for this! You need " + bardEffect.getEnergy() + " energy, but you only have " + energy.intValue());
            return;
        }

        if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
            final ItemStack itemStack = event.getItem();

            if (itemStack != null && (PortableStrength.itemStack.isSimilar(itemStack) || PortableResistance.itemStack.isSimilar(itemStack))) {
                return;
            }

            if (itemStack != null && PortableResistance.itemStack.isSimilar(itemStack)) {
                return;
            }
        }

        boolean negative = bardEffect.getPotionEffect() != null && FoxListener.DEBUFFS.contains(bardEffect.getPotionEffect().getType());

        final BardEffectUseEvent bardEffectUseEvent = new BardEffectUseEvent(event.getPlayer(), bardEffect, this.getNearbyPlayers(event.getPlayer(), !negative).stream().filter(nearbyPlayer -> !nearbyPlayer.getUniqueId().equals(event.getPlayer().getUniqueId())).collect(Collectors.toList()));

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(bardEffectUseEvent);

        if (bardEffectUseEvent.isCancelled()) {
            return;
        }

        this.giveBardEffect(event.getPlayer(), bardEffect, !negative, true);

        PvPClassHandler.getUltimate().put(event.getPlayer().getUniqueId(), Math.min((PvPClassHandler.getUltimate().get(event.getPlayer().getUniqueId()) + ULTIMATE_PER_BARD_EFFECT), 100F));

        if (event.getPlayer().isSneaking() && PvPClassHandler.getUltimate().get(event.getPlayer().getUniqueId()) >= 100F) {
            PvPClassHandler.getUltimate().put(event.getPlayer().getUniqueId(), 0F);
            this.giveEffect(event.getPlayer(), new PotionEffect(PotionEffectType.SPEED, 20 * 5, 2), true, true);
            this.giveEffect(event.getPlayer(), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), true, true);
            this.giveEffect(event.getPlayer(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), true, true);
            event.getPlayer().sendMessage(ChatColor.GREEN + "You have used your " + ChatColor.WHITE + "Bard Ultimate" + ChatColor.GREEN + ".");
        } else {
            Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().put(event.getPlayer().getUniqueId(), energy - bardEffect.getEnergy());
            Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            Foxtrot.getInstance().getMasteryUpgradeHandler().addUpgrade(event.getPlayer(), "Bard", 1);
        }

        SpawnTagHandler.addOffensiveSeconds(event.getPlayer(), SpawnTagHandler.getMaxTagTime(event.getPlayer()));

        if (event.getPlayer().getItemInHand().getAmount() == 1) {
            event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
            event.getPlayer().updateInventory();
        } else {
            event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
        }

    }

    public void giveBardEffect(Player source, EnergyEffect bardEffect, boolean friendly, boolean persistOldValues) {

        for (Player player : getNearbyPlayers(source, friendly)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && bardEffect.getPotionEffect() != null && bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                continue;
            }

            if (bardEffect.getPotionEffect() != null) {

//                if (bardEffect.getPotionEffect().getType() == PotionEffectType.SPEED) {
//                    player.setSprinting(true);
//                } Dumbest decision of my life what the fuck even was this bro my screen keep seizing

                smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues, this);
            }
        }
    }


    public void giveEffect(Player source, PotionEffect potionEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : getNearbyPlayers(source, friendly)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            if (potionEffect != null) {
                smartAddPotion(player, potionEffect, persistOldValues, this);
            }
        }
    }

    public boolean canUseEffect(Player player) {
        int limit = Foxtrot.getInstance().getMapHandler().isKitMap() ? 3 : 1;

        if (PvPClassHandler.countMembersWithKitOn(player, this) > limit) {
            player.sendMessage(ChatColor.RED + "Your team has too many bards!");
            return false;
        }

        return true;
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(BARD_RANGE, 15, BARD_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isFriendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBardEffectUse(BardEffectUseEvent event) {

        if (event.getBardEffect().getPotionEffect() == null) {
            return;
        }

        if (event.getAffectedPlayers().isEmpty()) {
            event.getSource().sendMessage(ChatColor.YELLOW + "You gave " + event.getBardEffect().getFancyName() + ChatColor.YELLOW + " to yourself.");
            return;
        }

        final int amount = event.getAffectedPlayers().size();

        event.getSource().sendMessage(ChatColor.YELLOW + "You have given " + event.getBardEffect().getFancyName() + ChatColor.YELLOW + " to " +
                (FoxListener.DEBUFFS.contains(event.getBardEffect().getPotionEffect().getType()) ? ChatColor.RED.toString() + amount + ChatColor.YELLOW + " " + (amount == 1 ? "enemy" : "enemies") : ChatColor.GREEN.toString() + amount + ChatColor.YELLOW + " " + (amount == 1 ? "teammate" : "teammates"))
                + ChatColor.YELLOW + "."
        );
    }
}
