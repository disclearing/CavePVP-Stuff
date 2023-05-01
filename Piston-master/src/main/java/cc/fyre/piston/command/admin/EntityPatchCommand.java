package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class EntityPatchCommand {

    @Command(names = {"ep ca", "ep clearall"}, permission = "hcf.command.ep")
    public static void clearAll(CommandSender sender, @Flag(value = "d")boolean detailed) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.hasMetadata("DONT_CLEAR")) {
                    continue;
                }

                if (entity instanceof Villager && ((Villager) entity).isCustomNameVisible() && ((Villager) entity).getCustomName().contains("Token Merchant")) {
                    continue;
                }

                if (!(entity instanceof Player || entity instanceof EnderPearl || entity instanceof Wither || entity instanceof ItemFrame || entity instanceof EnderDragon)) {
                    atomicInteger.addAndGet(1);

                    if (detailed) {
                        sender.sendMessage(ChatColor.GOLD + "Cleared entity " + ChatColor.WHITE + entity.getType().name());
                    }

                    entity.remove();
                }
            }
        }
        sender.sendMessage(ChatColor.translate("&6Cleared &f" + atomicInteger.get() + " &6entities successfully!"));
    }

    @Command(names = {"ep cm", "ep clearmobs"}, permission = "hcf.command.ep")
    public static void clearMobs(CommandSender sender, @Flag(value = "d") boolean detailed) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Monster) {
                    atomicInteger.addAndGet(1);
                    if (detailed) {
                        sender.sendMessage(ChatColor.GOLD + "Cleared monster " + ChatColor.WHITE + entity.getType().name());
                    }
                    entity.remove();
                }
            }
        }
        sender.sendMessage(ChatColor.translate("&6Cleared &f" + atomicInteger.get() + " &6mobs successfully!"));
    }

    @Command(names = {"ep can", "ep clearanimals"}, permission = "hcf.command.ep")
    public static void clearAnimals(CommandSender sender, @Flag(value = "d")boolean detailed) {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Animals) {
                    atomicInteger.addAndGet(1);
                    if (detailed) {
                        sender.sendMessage(ChatColor.GOLD + "Cleared animal " + ChatColor.WHITE + entity.getType().name());
                    }
                    entity.remove();
                }
            }
        }
        sender.sendMessage(ChatColor.translate("&6Cleared &f" + atomicInteger.get() + " &6animals successfully!"));
    }

    @Command(names = {"spawnwolves"}, permission = "op")
    public static void spawnWolves(Player player, @Parameter(name = "amount")int amount) {
        IntStream.range(0, amount).forEach(it -> {
            final Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
            wolf.setOwner(player);
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 255));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
            wolf.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 255));
            wolf.setMaxHealth(1000);
            wolf.setHealth(1000);
            wolf.setSitting(false);
        });
    }

}
