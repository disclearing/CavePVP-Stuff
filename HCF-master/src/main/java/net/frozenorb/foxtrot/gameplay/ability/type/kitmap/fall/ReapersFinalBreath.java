package net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap.GrimReaperClass;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ReapersFinalBreath extends Ability {
    public ReapersFinalBreath() {
        final ItemMeta itemMeta = this.hassanStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Reaper's Final Breath");
        this.hassanStack.setItemMeta(itemMeta);
    }

    @Override
    public Category getCategory() {
        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "You have been given effects for 5 seconds!";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.WEB;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Final Breath";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fRight Click and every"));
        toReturn.add(ChatColor.translate("&6❙ &fenemy around gets put in webs."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in a &d&lPartner Package&f."));

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
    public boolean inPartnerPackage() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        for (Player nearByEnemy : GrimReaperClass.getNearByEnemies(player, 25)) {
            final Block blockAt = nearByEnemy.getLocation().getBlock();

            if (blockAt.getType().equals(Material.AIR)) {
                blockAt.setType(Material.WEB);
            }

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                blockAt.setType(Material.AIR);
            }, 20*10);
        }

        this.applyCooldown(player);
    }
}