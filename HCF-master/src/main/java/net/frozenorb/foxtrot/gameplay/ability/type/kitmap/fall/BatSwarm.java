package net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BatSwarm extends Ability {

    public BatSwarm() {
        this.hassanStack.setDurability((byte) 65);
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
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Bat Swarm";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fHit a player 3 times to activate,"));
        toReturn.add(ChatColor.translate("&6❙ &fgiven them Poison II, Blindness II"));
        toReturn.add(ChatColor.translate("&6❙ &ffor 5 seconds and 3 bats fly in their face."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in an &9&lHeaded Crate&f!"));

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
        return 200_000L;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (damager.getItemInHand() == null || !this.isSimilar(damager.getItemInHand())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);

        int value = target.hasMetadata("BATSWARM") ? (target.getMetadata("BATSWARM").get(0).asInt() + 1) : 1;

        if (value != 3) {
            abilityUseEvent.setOneHit(true);
        }

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        target.setMetadata("BATSWARM", new FixedMetadataValue(Foxtrot.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(CC.translate("&cYou have to hit &f" + target.getName() + " &c" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("BATSWARM", Foxtrot.getInstance());

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }


        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*6, 1));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*6, 1));

        final BlockFace blockFace = getDirection(target);
        final Block block = target.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(blockFace, 2);

        for (int i = 0; i < 3; i++) {
            block.getWorld().spawnEntity(block.getLocation(), EntityType.BAT);
        }

        this.fullDescription = "You have bat swarmed " + target.getName() + "!";

        target.sendMessage("");
        target.sendMessage(CC.translate("&4" + damager.getName() + " &chas hit you with " + this.getDisplayName() + "&c!"));
        target.sendMessage(CC.translate("&cYou were bat swarmed!"));
        target.sendMessage("");

        this.applyCooldown(damager);
    }

    @Override
    public Category getCategory() {
        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "";
    }

    private BlockFace getDirection(Player player) {
        double rot = (player.getLocation().getYaw() - 90) % 360;
        if (rot < 0) {
            rot += 360.0;
        }

        if (0 <= rot && rot < 22.5) {
            return BlockFace.WEST;
        } else if (22.5 <= rot && rot < 67.5) {
            return BlockFace.NORTH_WEST;
        } else if (67.5 <= rot && rot < 112.5) {
            return BlockFace.NORTH;
        } else if (112.5 <= rot && rot < 157.5) {
            return BlockFace.NORTH_EAST;
        } else if (157.5 <= rot && rot < 202.5) {
            return BlockFace.EAST;
        } else if (202.5 <= rot && rot < 247.5) {
            return BlockFace.SOUTH_EAST;
        } else if (247.5 <= rot && rot < 292.5) {
            return BlockFace.SOUTH;
        } else if (292.5 <= rot && rot < 337.5) {
            return BlockFace.SOUTH_WEST;
        } else if (337.5 <= rot && rot < 360.0) {
            return BlockFace.WEST;
        } else {
            return BlockFace.NORTH;
        }
    }
}