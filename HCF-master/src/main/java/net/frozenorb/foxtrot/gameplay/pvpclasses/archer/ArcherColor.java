package net.frozenorb.foxtrot.gameplay.pvpclasses.archer;

import cc.fyre.neutron.util.ColorUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@AllArgsConstructor
public enum ArcherColor {

    DARK_GREEN("Medusa",ChatColor.DARK_GREEN,player -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,6*20,0));
        return false;
    },ChatColor.DARK_GREEN + "Medusa " + ChatColor.GOLD + "has applied " + ChatColor.DARK_GREEN + "Poison" + ChatColor.GOLD + " for 5 seconds",
            Arrays.asList(ChatColor.GRAY + "Each shot has a chance", "of giving the player Poison II for 5 seconds.")),

    WHITE("Phantom",ChatColor.WHITE,player -> {
        player.setSprinting(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,6*20,1));
        return false;
    },ChatColor.WHITE + "Phantom " + ChatColor.GOLD + "has applied " + ChatColor.WHITE + "Blindness" + ChatColor.GOLD + " for 5 seconds",
            Arrays.asList(ChatColor.GRAY + "Each shot has chance of disabling", "their sprint and giving Blindness II for 5 seconds.")),

    BLACK("Venom",ChatColor.DARK_PURPLE,player -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,6*20,1));
        return false;
    },ChatColor.DARK_PURPLE + "Venom " + ChatColor.GOLD + "has applied " + ChatColor.DARK_PURPLE + "Wither II" + ChatColor.GOLD + " for 5 seconds",
            Arrays.asList(ChatColor.GRAY + "Each shot has a chance", "of giving the player Wither II for 5 seconds.")),

    RED("Carnage",ChatColor.RED,player -> {

        player.getActivePotionEffects().stream().filter(it -> it.getType().equals(PotionEffectType.FIRE_RESISTANCE)).findFirst().ifPresent(it -> {

            player.removePotionEffect(it.getType());

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(),() -> {

                if (!player.isOnline()) {
                    return;
                }

                player.addPotionEffect(it);
            },5*20L + 5L);

        });

        player.setFireTicks(20 * 5);
        return false;
    },ChatColor.RED + "Carnage " + ChatColor.GOLD + "has applied " + ChatColor.RED + "Fire" + ChatColor.GOLD + " for 5 seconds",
            Arrays.asList(ChatColor.GRAY + "Each shot has a chance of putting them", "on fire and disabling fire resistance for 5 seconds."));

    @Getter private final String name;
    @Getter private final ChatColor chatColor;
    @Getter private final Predicate<Player> predicate;
    @Getter private final String message;
    @Getter private final List<String> lore;

    public Color getColor() {
        return ColorUtil.COLOR_MAP.get(this.chatColor).getColor();
    }

    public boolean isWearingArmor(Player player) {

        if (player.getInventory().getHelmet() == null || player.getInventory().getChestplate() == null || player.getInventory().getLeggings() == null || player.getInventory().getBoots() == null) {
            return false;
        }

        if (Arrays.stream(player.getInventory().getArmorContents()).anyMatch(it -> !(it.getItemMeta() instanceof LeatherArmorMeta))) {
            return false;
        }

        return ((LeatherArmorMeta)player.getInventory().getHelmet().getItemMeta()).getColor().equals(this.getColor())
                && ((LeatherArmorMeta)player.getInventory().getChestplate().getItemMeta()).getColor().equals(this.getColor())
                && ((LeatherArmorMeta)player.getInventory().getLeggings().getItemMeta()).getColor().equals(this.getColor())
                && ((LeatherArmorMeta)player.getInventory().getBoots().getItemMeta()).getColor().equals(this.getColor());
    }

    public static Optional<ArcherColor> findByPlayer(Player player) {
        return Arrays.stream(values()).filter(it -> it.isWearingArmor(player)).findFirst();
    }

    public static ArcherColor findByName(String name) {
        return Arrays.stream(values()).filter(it -> it.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
