package net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall.PiercingShot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UndeadWarriorClass extends ArmorClass {
    private Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getId() {
        return "undeadwarrior";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Undead Warrior";
    }

    @Override
    public int getSlot() {
        return 16;
    }

    @Override
    public Material getDisplayItem() {
        return Material.BONE;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Piercing Shot doesn't work on this class.");
        toReturn.add("Allies within a 20-block radius of you take 25%");
        toReturn.add("less fall damage and take 0.5 fewer hearts from an archer tag.");

        return toReturn;
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        boolean reduce = false;

        if (team == null) {
            return;
        }

        for (Player onlineMember : team.getOnlineMembers()) {

            if (!onlineMember.hasMetadata("UNDEAD_WARRIOR")) {
                continue;
            }

            if (!onlineMember.getLocation().getWorld().equals(player.getWorld())) {
                continue;
            }

            if (onlineMember.getLocation().distance(player.getLocation()) <= 20) {
                reduce = true;
            }

        }

        if (reduce) {
            event.setDamage(event.getDamage()*0.75);
        }
    }

    @Override
    public Category getCategory() {
        return Category.DIAMOND;
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
        player.setMetadata("UNDEAD_WARRIOR", new FixedMetadataValue(Foxtrot.getInstance(), true));
    }

    @Override
    public void unapply(Player player) {
        player.removeMetadata("UNDEAD_WARRIOR", Foxtrot.getInstance());
    }
}
