package net.frozenorb.foxtrot.gameplay.ability.type;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
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
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.entity.PotionEffectRemoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Invisibility extends Ability {
    @Override
    public Category getCategory() {
        return Category.AIRDROPS;
    }

    @Override
    public String getDescription() {
        return "You are now fully invisible! Your armor no longer shows!";
    }

    public static PotionEffect EFFECT = new PotionEffect(PotionEffectType.INVISIBILITY,(3*60)*20,2,false);

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
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Invisibility";
    }



    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fWhen you right click this item"));
        toReturn.add(ChatColor.translate("&6❙ &fyour armor will no longer be visible."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &d&lPartner Crate&f!"));

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
    private void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        final ItemStack itemStack = event.getItem();

        if (itemStack.getAmount() == 1) {
            event.getPlayer().setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        event.getPlayer().addPotionEffect(EFFECT, true);
        event.getPlayer().setFireTicks(0);

        ((CraftPlayer)event.getPlayer()).getHandle().getDataWatcher().watch(9, (byte) 0);

        this.sendRestorePacket(event.getPlayer(),Foxtrot.getInstance().getServer().getOnlinePlayers(),true);

        this.applyCooldown(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || (!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
            return;
        }

        final Player player = (Player) event.getEntity();

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(EFFECT.getType()) && it.getAmplifier() == EFFECT.getAmplifier()).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            this.sendRestorePacket(player,Foxtrot.getInstance().getServer().getOnlinePlayers(),false);

            final PotionEffect clone = new PotionEffect(it.getType(),it.getDuration(),0);

            player.addPotionEffect(clone);

            ((Player)event.getEntity()).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING!" + ChatColor.YELLOW + " You have been hit and are no " + ChatColor.RED + ChatColor.BOLD + "LONGER" + ChatColor.YELLOW + " invisible!");
        });

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamagerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || (!(event.getEntity() instanceof Player)) || (!(event.getDamager() instanceof Player))) {
            return;
        }

        final Player player = (Player) event.getDamager();

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(EFFECT.getType()) && it.getAmplifier() == EFFECT.getAmplifier()).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            this.sendRestorePacket(player,Foxtrot.getInstance().getServer().getOnlinePlayers(),false);

            final PotionEffect clone = new PotionEffect(it.getType(),it.getDuration(),0);

            player.addPotionEffect(clone);

            player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING!" + ChatColor.YELLOW + " You have hit a player and are no " + ChatColor.RED + ChatColor.BOLD + "LONGER" + ChatColor.YELLOW + " invisible!");
        });

    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPotionEffectExpire(PotionEffectExpireEvent event) {
        this.onPotionEffectRemove(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPotionEffectRemove(PotionEffectRemoveEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!event.getEffect().getType().equals(EFFECT.getType()) || event.getEffect().getAmplifier() != EFFECT.getAmplifier()) {
            return;
        }

        final Player player = (Player) event.getEntity();

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(EFFECT.getType()) && it.getAmplifier() == EFFECT.getAmplifier()).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            this.sendRestorePacket(player,Foxtrot.getInstance().getServer().getOnlinePlayers(),false);

            final PotionEffect clone = new PotionEffect(it.getType(),it.getDuration(),0);

            player.addPotionEffect(clone);

            ((Player)event.getEntity()).sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "WARNING!" + ChatColor.YELLOW + " Your " + ChatColor.WHITE + "Invisibility" + ChatColor.YELLOW + " has expired and are no " + ChatColor.RED + ChatColor.BOLD + "LONGER" + ChatColor.YELLOW + " invisible!");
        });

    }

    private void sendRestorePacket(Player player, Collection<? extends Player> players, boolean clear) {

        final List<PacketContainer> packets = new ArrayList<>();

        for (int i = 0; i < 4; i++) {

            final PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

            packet.getIntegers().write(0,player.getEntityId());
            packet.getIntegers().write(1,i+1);

            packet.getItemModifier().write(0,clear ? new ItemStack(Material.AIR):player.getInventory().getArmorContents()[i]);

            packets.add(packet);
        }

        players.stream().filter(it -> it.getUniqueId() != player.getUniqueId()).forEach(it -> packets.forEach(packet -> {

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(it,packet);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }));

    }
}
