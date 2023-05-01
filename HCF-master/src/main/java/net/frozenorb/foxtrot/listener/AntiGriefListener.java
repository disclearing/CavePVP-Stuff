package net.frozenorb.foxtrot.listener;

import cc.fyre.piston.Piston;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.KillTheKingCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerHealthChangeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class AntiGriefListener implements Listener {
    public static List<String> effects = Arrays.asList("INCREASE_DAMAGE", "RESISTANCE", "REGEN", "HEALTH_BOOST");
    public static List<String> allowPotion = Arrays.asList("SPEED", "HEAL", "POISON", "INVISIBILITY", "SLOW");

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDropPotion(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemDrop().getItemStack();

        if (itemStack == null) {
            return;
        }

//        if (itemStack.getType() == Material.DIAMOND_SWORD) {
//
//            int sharpLevel = itemStack.containsEnchantment(Enchantment.DAMAGE_ALL) ? itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0;
//
//            if (sharpLevel > 3) {
//                event.setCancelled(true);
//                this.alertStaff("neutron.manager", player.getName() + " &ehas dropped &b&lDIAMOND SWORD &ewith &c&lSharpness " + sharpLevel);
//            }
//        }
//
        if (itemStack.getType() == Material.POTION) {
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getType().getName().contains("STRENGTH") || effect.getType().getName().contains("WAEKNESS") || effect.getType().getName().contains("HARM") || effect.getType().getName().contains("HUNGER") || effect.getType().getName().contains("REGEN") || effect.getType().getName().contains("RES")) {
                    this.alertStaff("neutron.manager", player.getName() + " has dropped " + effect.getType().getName() + " amplifier " + effect.getAmplifier() + " " + effect.getDuration());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPickUpPotion(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem().getItemStack();

//        if (itemStack.getType() == Material.DIAMOND_SWORD) {
//
//            int sharpLevel = itemStack.containsEnchantment(Enchantment.DAMAGE_ALL) ? itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0;
//
//            if (sharpLevel > 3) {
//                event.setCancelled(true);
//                this.alertStaff("neutron.manager", player.getName() + " &ehas picked up &b&lDIAMOND SWORD &ewith &c&lSharpness " + sharpLevel);
//            }
//        }

        if (itemStack.getType() == Material.POTION) {
            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getType().getName().contains("STRENGTH") || effect.getType().getName().contains("WITHER") || effect.getType().getName().contains("WEAKNESS") || effect.getType().getName().contains("HARM") || effect.getType().getName().contains("HUNGER") || effect.getType().getName().contains("REGEN") || effect.getType().getName().contains("RES")) {
                    this.alertStaff("neutron.manager", player.getName() + " &ehas picked up " + effect.getType().getName() + " amplifier " + effect.getAmplifier() + " " + effect.getDuration());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getDamager();
        final ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || !itemStack.containsEnchantment(Enchantment.DAMAGE_ALL) || Foxtrot.getInstance().getMapHandler().isKitMap() && KillTheKingCommand.king != null && KillTheKingCommand.king.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            return;
        }

        int enchantLevel = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);

//        if (enchantLevel > 3 && !player.isOp()) {
//            this.alertStaff("neutron.manager", player.getName() + " &ehas used &c&lSHARPNESS " + enchantLevel);
//
//            player.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
//            player.updateInventory();
//
//            player.sendMessage(ChatColor.RED + "Your sword contained an illegal enchant therefore it has been removed!");
//        }

//        if (itemStack.containsEnchantment(Enchantment.KNOCKBACK) && !player.isOp()) {
//            this.alertStaff("neutron.manager", player.getName() + " &ehas used &c&lKNOCKBACK " + enchantLevel);
//
//            player.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
//            player.updateInventory();
//
//            player.sendMessage(ChatColor.RED + "Your sword contained an illegal enchant therefore it has been removed!");
//        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onShoot(EntityShootBowEvent event) {
        if (event.getBow() == null) {
            return;
        }

        if (!(event.getProjectile() instanceof Arrow)) {
            return;
        }

        if (!(((Arrow) event.getProjectile()).getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) ((Arrow) event.getProjectile()).getShooter();
        final ItemStack bow = event.getBow();

        if (bow.containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
            shooter.sendMessage(ChatColor.RED + "You may not shoot a punch bow!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onArmor(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (Foxtrot.getInstance().getMapHandler().isKitMap() && KillTheKingCommand.king != null && KillTheKingCommand.king.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            return;
        }

        if (player.isOp()) {
            return;
        }

        for (ItemStack armorContent : player.getInventory().getArmorContents()) {

            int protectionLevel = armorContent.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL) ? armorContent.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL) : 0;

            if (!player.isOp() && (protectionLevel > 3)) {
                event.setCancelled(true);
                this.alertStaff("neutron.manager", player.getName() + " &ehas illegal armor enchants: &a" + (armorContent.containsEnchantment(Enchantment.THORNS) ? " THORNS " + armorContent.getEnchantmentLevel(Enchantment.THORNS) : " PROTECTION " + protectionLevel));

                player.getInventory().setArmorContents(null);
                player.sendMessage(ChatColor.RED + "Your armor contained an illegal enchant therefore it has been cleared!");
            }

            if (armorContent.containsEnchantment(Enchantment.THORNS)) {
                event.setCancelled(true);
                this.alertStaff("neutron.manager", player.getName() + " &ehas illegal armor enchants: &a" + (armorContent.containsEnchantment(Enchantment.THORNS) ? " THORNS " + armorContent.getEnchantmentLevel(Enchantment.THORNS) : " PROTECTION " + protectionLevel));

                player.getInventory().setArmorContents(null);
                player.sendMessage(ChatColor.RED + "Your armor contained an illegal enchant therefore it has been cleared!");
            }



        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSplash(PotionSplashEvent event) {
        final ThrownPotion potion = event.getPotion();

        if (!(potion.getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) potion.getShooter();

        for (PotionEffect effect : potion.getEffects()) {

            if (effect.getType().getName().equalsIgnoreCase("HEALTH_BOOST") || allowPotion.stream().noneMatch(it -> effect.getType().getName().contains(it))) {
                shooter.sendMessage(ChatColor.RED + "You may not splash that potion!");
                event.setCancelled(true);

                System.out.println();
                System.out.println("[ALERT] " + shooter.getName() + " has splashed " + effect.getType().getName() + " " + effect.getAmplifier());
                System.out.println("Affected Users:");
                for (LivingEntity affectedEntity : event.getAffectedEntities()) {
                    System.out.println("- " + ((CraftLivingEntity) affectedEntity).getHandle().getName());
                }
                System.out.println("");

                Foxtrot.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.hasPermission("neutron.staff") && !Piston.getInstance().getToggleStaff().contains(it.getUniqueId()))
                        .forEach(it -> {
                            it.sendMessage("");
                            it.sendMessage(ChatColor.translate("&c&l[ALERT] &f" + shooter.getName() + " &ehas splashed " + effect.getType().getName() + " " + effect.getAmplifier()));
                            it.sendMessage(ChatColor.GRAY + "Affected Users:");
                            for (LivingEntity affectedEntity : event.getAffectedEntities()) {
                                it.sendMessage(ChatColor.GREEN + ((CraftLivingEntity) affectedEntity).getHandle().getName());
                            }
                            it.sendMessage("");
                        });
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPotion(PotionEffectAddEvent event) {

        if (!(event.getEntity() instanceof Player) || effects.stream().noneMatch(it -> event.getEffect().getType().getName().contains(it))) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final PotionEffect potionEffect = event.getEffect();

        if (event.getEffect().getType().getName().equalsIgnoreCase("HEALTH_BOOST")) {

            if (event.getEffect().getType().equals(PotionEffectType.DAMAGE_RESISTANCE) && event.getEffect().getAmplifier() == 4) {
                return;
            }

            this.alertStaff("neutron.op", player.getName() + " &ehas received " + potionEffect.getType().getName() + " " + potionEffect.getAmplifier());

            Foxtrot.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.isOp() && !Piston.getInstance().getToggleStaff().contains(it.getUniqueId()))
                    .forEach(it -> {
                        it.sendMessage(ChatColor.GRAY + "Nearby Users:");
                        for (Entity nearbyEntity : player.getNearbyEntities(10, 10, 10)) {
                            if (!(nearbyEntity instanceof Player)) {
                                continue;
                            }

                            final Player target = (Player) nearbyEntity;

                            it.sendMessage("- " + target.getName());
                        }
                        it.sendMessage("");
                    });

            event.setCancelled(!player.isOp());
            return;
        }

        int amplifierAlertRequirement = 2;
        int amplifierCancelRequirement = 3;

        if (potionEffect.getType().getName().contains("REGEN")) {
            amplifierAlertRequirement = 4;
            amplifierCancelRequirement = 5;
        }

        if (potionEffect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
            amplifierAlertRequirement = 4;
            amplifierCancelRequirement = 5;
        }

        if (potionEffect.getAmplifier() > amplifierAlertRequirement || potionEffect.getAmplifier() < 0) {
            if (potionEffect.getAmplifier() > amplifierCancelRequirement || potionEffect.getAmplifier() < 0) {
                event.setCancelled(!player.isOp());
            }

            this.alertStaff("neutron.staff", player.getName() + " &ehas received " + potionEffect.getType().getName() + " " + potionEffect.getAmplifier());
        }
    }

    public void alertStaff(String permissionNode, String message) {
        Foxtrot.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.hasPermission(permissionNode)).forEach(it -> {
            it.sendMessage("");
            it.sendMessage(ChatColor.translate("&c&l[ALERT] &f" + message));
            it.sendMessage("");
        });

        System.out.println();
        System.out.println("[ALERT] " + ChatColor.stripColor(ChatColor.translate(message)));
        System.out.println();
    }

}
