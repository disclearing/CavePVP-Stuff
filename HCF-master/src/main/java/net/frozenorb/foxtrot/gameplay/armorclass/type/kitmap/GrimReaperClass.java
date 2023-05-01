package net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall.PiercingShot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GrimReaperClass extends ArmorClass {
    private Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getId() {
        return "grimreaper";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Grim Reaper";
    }

    @Override
    public int getSlot() {
        return 13;
    }

    @Override
    public Material getDisplayItem() {
        return Material.IRON_HOE;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("10% more damage towards kits.");
        toReturn.add("Shift Right CLick to give every enemy Blindness III,");
        toReturn.add("Slowness III and they take 10% more damage for 7 seconds.");
        toReturn.add("Reapers Revenge & Final Breath cannot be used within 10 blocks.");

        return toReturn;
    }

    @Override
    public Category getCategory() {
        return Category.DIAMOND;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isWearing(player)) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long millisLeft = ((cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L) * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000));

            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return;
        }

        int count = 0;

        for (Player nearByEnemy : getNearByEnemies(player, 20)) {
            nearByEnemy.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*8, 2));
            nearByEnemy.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*8, 2));

            PiercingShot.cache.put(nearByEnemy.getUniqueId(), player.getUniqueId());

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                PiercingShot.cache.remove(nearByEnemy.getUniqueId());
            }, 20*7);
        }

        player.sendMessage(ChatColor.translate("&aSuccessfully effected &f" + count + " &aplayers!"));
        cooldown.put(player.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(3));
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();
        final Player target = (Player) event.getEntity();

        if (!isWearing(damager)) {
            return;
        }

        if (PvPClassHandler.getPvPClass(target) != null) {
            event.setDamage(event.getDamage()*1.1D);
        }
    }

    @Override
    public ItemStack createPiece(ArmorPiece armorPiece) {
        final ItemBuilder itemBuilder = ItemBuilder.of(armorPiece.getDefaultMaterial())
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .enchant(Enchantment.DURABILITY, 3)
                .name(getDisplayName() + " " + getPieceName(armorPiece))
                .addToLore("", getChatColor() + "Armor Class: &f" + ChatColor.stripColor(getDisplayName()), getChatColor() + "Perks:");
        for (String perk : this.getPerks()) {
            itemBuilder.addToLore(getChatColor() + "❙ &f" + perk);
        }
        itemBuilder.addToLore("");

        return itemBuilder.build();
    }

    @Override
    public void apply(Player player) {
    }

    @Override
    public void unapply(Player player) {
    }

    public static List<Player> getNearByEnemies(Player player, int radius) {

        final List<Player> toReturn = new ArrayList<>();

        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {

            if (!(entity instanceof Player)) {
                continue;
            }

            final Player target = (Player)entity;

            if (player.getUniqueId().equals(target.getUniqueId())) {
                continue;
            }

            if (target.isDead()) {
                continue;
            }

            if (Foxtrot.getInstance().getTeamHandler().getTeam(player) != null && Foxtrot.getInstance().getTeamHandler().getTeam(player).getMembers().contains(target.getUniqueId())) {
                continue;
            }

            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())) {
                continue;
            }

            if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(target.getUniqueId())) {
                continue;
            }

            toReturn.add(target);
        }

        return toReturn;
    }
}
