package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PoisonBow extends Ability {

    public PoisonBow() {
        this.hassanStack = ItemBuilder.copyOf(this.hassanStack).enchant(Enchantment.ARROW_INFINITE, 1).enchant(Enchantment.ARROW_DAMAGE, 3).build();
    }

    private static final int USES = 50;
    private static final int BASE = 384 - USES; // total durability - uses

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }



    @Override
    public Material getMaterial() {
        return Material.BOW;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Poison Bow";
    }

    @Override
    public List<String> getLore() {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fShoot an enemy and they will be"));
        toReturn.add(ChatColor.translate("&6❙ &fpoisoned for &e&l5 seconds&f!"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &d&l??? &fCrate!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 30_000L;
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();
        final ItemStack itemStack = player.getItemInHand();

        if (!this.isSimilar(itemStack)) {
            return;
        }

        short durability = itemStack.getDurability();

        if (durability <= BASE) {
            itemStack.setDurability((short) BASE);
        } else {
            itemStack.setDurability((short) (durability + 1));
        }

        event.getEntity().setMetadata("POISON_BOW", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !this.isSimilar(event.getPlayer().getItemInHand())) {
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(CC.translate("&cYou may not use " + this.getDisplayName() + " &cwhile your &a&lPvP Timer &cis active!"));
            event.getPlayer().updateInventory();
            event.setCancelled(true);
            return;
        }

        if (this.hasCooldown(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            return;
        }

        if (this.isAllowedAtLocation(event.getPlayer().getLocation())) {
            return;
        }

        String teamName;

        final Team ownerTeam = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());

        if (ownerTeam != null) {
            teamName = ownerTeam.getName(event.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(event.getPlayer().getLocation())) {
            teamName = ChatColor.GRAY + "The Wilderness";
        } else {
            teamName = ChatColor.DARK_RED + "WarZone";
        }

        event.getPlayer().sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
        event.getPlayer().updateInventory();
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Arrow) || !event.getDamager().hasMetadata("POISON_BOW")) {
            return;
        }

        final Player shooter = Foxtrot.getInstance().getServer().getPlayer(UUID.fromString(event.getDamager().getMetadata("POISON_BOW").get(0).asString()));
        final Player target = (Player) event.getEntity();

        if (shooter == null) {
            return;
        }

        if (target.getLocation().getWorld().getEnvironment() == World.Environment.THE_END || target.getLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
            shooter.sendMessage(ChatColor.RED + "You may not use this item in The End/Nether!");

            shooter.updateInventory();
            return;
        }

        if (!this.isAllowedAtLocation(target.getLocation())) {

            String location;

            final Team ownerTeam = LandBoard.getInstance().getTeam(target.getLocation());

            if (ownerTeam != null) {
                location = ownerTeam.getName(shooter);
            } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(target.getLocation())) {
                location = ChatColor.GRAY + "The Wilderness";
            } else {
                location = ChatColor.DARK_RED + "WarZone";
            }

            shooter.updateInventory();
            shooter.sendMessage(ChatColor.RED + "You may use this on " + target.getDisplayName() + " who is in " + location + ChatColor.RED + ".");
            return;
        }

        if (PvPClassHandler.getPvPClass(target) != null) {
            shooter.sendMessage(ChatColor.RED + "You may not use a " + this.getDisplayName() + ChatColor.RED + " on a " + ChatColor.WHITE + Objects.requireNonNull(PvPClassHandler.getPvPClass(target)).getName() + ChatColor.RED + ".");
            return;
        }

        fullDescription = "You have given " + target.getName() + " Poison for 5 seconds!";

        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*6, 0));
    }

    @Override
    public Category getCategory() {
        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "";
    }
}