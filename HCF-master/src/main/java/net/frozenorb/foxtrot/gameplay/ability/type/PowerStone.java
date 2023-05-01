package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PowerStone extends Ability {
    public PowerStone() {
        this.hassanStack.setDurability((byte)5);
    }

    private List<UUID> powerStone = new ArrayList<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.INK_SACK;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Power Stone";
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "You have received Strength, Resistance and Regeneration for 10 seconds.";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fRight Click to receive Strength II,"));
        toReturn.add(ChatColor.translate("&6❙ &fResistance III and Regeneration III for &e&l10 seconds"));
        toReturn.add(ChatColor.translate("&6❙ &fDuring that time you may not use any potions!"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &6&lOctober Mystery Box&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        final Team teamAt = LandBoard.getInstance().getTeam(location);

        if (teamAt != null && teamAt.getOwner() != null && teamAt.getOwner().toString().equals("dad8441f-dece-499d-a894-74cf3bd63d4a")) {
            return false;
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 165_000L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        final Location blockAt = player.getLocation();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        final Block belowBlock = blockAt.getBlock().getRelative(BlockFace.DOWN);

        if (!player.isOnGround() && belowBlock.getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You may not use the " + this.getDisplayName() + ChatColor.RED + " in the air!");
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);

        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 11, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 11, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 11, 1), true);

        final UUID uuid = player.getUniqueId();
        powerStone.add(uuid);

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            player.sendMessage("");
            player.sendMessage(ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired! You may now splash potions!");
            player.sendMessage("");
            powerStone.remove(uuid);
        }, 20 * 10);

        this.applyCooldown(player);
    }

    @EventHandler
    private void onSplash(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null) {
            return;
        }

        if (itemStack.getType() != Material.POTION) {
            return;
        }

        if (itemStack.getDurability() == 0) { // Water bottle
            return;
        }

        final Potion potion = Potion.fromItemStack(itemStack);

        if (potion.getType() != PotionType.INSTANT_HEAL) {
            return;
        }

        if (!powerStone.contains(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You may not splash health potions whilst using " + this.getDisplayName() + ChatColor.RED + ".");
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSplash(PotionSplashEvent event) {
        final ThrownPotion thrownPotion = event.getPotion();

        if (!(thrownPotion.getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getPotion().getShooter();

        if (thrownPotion.getEffects().stream().noneMatch(it -> it.getType().getName().contains("HEAL"))) {
            return;
        }

        if (!powerStone.contains(shooter.getUniqueId())) {
            return;
        }

        shooter.sendMessage(ChatColor.RED + "You may not splash health potions whilst using " + this.getDisplayName() + ChatColor.RED + ".");
        event.setCancelled(true);
    }
}