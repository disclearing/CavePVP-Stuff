package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class MinerClass extends PvPClass implements Listener {

    private static final int Y_HEIGHT = 20;

    private final Map<String, Integer> noDamage = new HashMap<>();
    @Getter
    private final Map<String, Integer> invis = new HashMap<>();

    public MinerClass() {
        super("Miner", null);

        this.getPermanentEffects().add(PotionEffectType.NIGHT_VISION);
        this.getPermanentEffects().add(PotionEffectType.FAST_DIGGING);
        if (!Foxtrot.getInstance().getServerHandler().isHardcore()) {
            this.getPermanentEffects().add(PotionEffectType.FIRE_RESISTANCE);
        }

        new BukkitRunnable() {

            public void run() {
                for (String key : new HashMap<>(noDamage).keySet()) {
                    int left = noDamage.remove(key);
                    Player player = Foxtrot.getInstance().getServer().getPlayerExact(key);

                    if (player == null) {
                        continue;
                    }

                    if (left == 0) {
                        if (player.getLocation().getY() <= Y_HEIGHT) {
                            invis.put(player.getName(), 10);
                            player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
                        }
                    } else {
                        noDamage.put(player.getName(), left - 1);
                    }
                }

                //Manage invisibility
                for (String key : new HashMap<>(invis).keySet()) {
                    Player player = Foxtrot.getInstance().getServer().getPlayerExact(key);

                    if (player != null) {
                        int secs = invis.get(player.getName());

                        if (secs == 0) {
                            if (player.getLocation().getY() <= Y_HEIGHT) {
                                if (!(player.hasPotionEffect(PotionEffectType.INVISIBILITY))) {
                                    player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been enabled!");
                                    player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));
                                }
                            }
                        } else {
                            invis.put(player.getName(), secs - 1);
                        }
                    }
                }
            }

        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
    }

    @Override
    public boolean qualifies(Player player, PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.IRON_HELMET &&
                armor.getChestplate().getType() == Material.IRON_CHESTPLATE &&
                armor.getLeggings().getType() == Material.IRON_LEGGINGS &&
                armor.getBoots().getType() == Material.IRON_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), true);
        if (!Foxtrot.getInstance().getServerHandler().isHardcore())
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
        }

        int diamonds = Foxtrot.getInstance().getDiamondMinedMap().getMined(player.getUniqueId());

        if (shouldApplyPotion(player, PotionEffectType.FAST_DIGGING, diamonds > 50 ? 2 : 1)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, diamonds > 50 ? 2 : 1), true);
        }

        int speedLevel = diamonds >= 250 ? 1 : diamonds >= 100 ? 0 : -1;

        if (speedLevel != -1 && shouldApplyPotion(player, PotionEffectType.SPEED, speedLevel)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speedLevel), true);
        }

        if (diamonds >= 500 && shouldApplyPotion(player, PotionEffectType.INVISIBILITY, 0)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
        }

        int resistanceLevel = diamonds >= 1000 ? 1 : diamonds >= 750 ? 0 : -1;

        if (resistanceLevel != -1 && shouldApplyPotion(player, PotionEffectType.DAMAGE_RESISTANCE, resistanceLevel)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, resistanceLevel), true);
        }

        if (diamonds >= 1500 && shouldApplyPotion(player, PotionEffectType.REGENERATION, 0)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
        }

        super.tick(player);
    }

    public boolean shouldApplyPotion(Player player, PotionEffectType eff, int level) {
        int potionLevel = -1;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(eff)) {
                potionLevel = effect.getAmplifier();
                break;
            }
        }
        return !player.hasPotionEffect(eff) || potionLevel < level;
    }

    @Override
    public void remove(Player player) {
        removeInfiniteEffects(player);
        noDamage.remove(player.getName());
        invis.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }

        noDamage.put(player.getName(), 15);

        if (invis.containsKey(player.getName()) && invis.get(player.getName()) != 0) {
            invis.put(player.getName(), 10);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getDamager();

        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }

        noDamage.put(player.getName(), 15);

        if (invis.containsKey(player.getName()) && invis.get(player.getName()) != 0) {
            invis.put(player.getName(), 10);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockY() == event.getTo().getBlockY()) {
            return;
        }

        Player player = event.getPlayer();

        if (!PvPClassHandler.hasKitOn(player, this)) {
            return;
        }

        if (event.getTo().getBlockY() <= Y_HEIGHT) { // Going below 20
            if (!invis.containsKey(player.getName())) {
                invis.put(player.getName(), 10);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " will be activated in 10 seconds!");
            }
        } else if (event.getTo().getBlockY() > Y_HEIGHT) { // Going above 20
            if (invis.containsKey(player.getName())) {
                noDamage.remove(player.getName());
                invis.remove(player.getName());
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been removed!");
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (!PvPClassHandler.hasKitOn(player, this) || event.getItem().getItemStack().getType() != Material.COBBLESTONE) {
            return;
        }

        if (!Foxtrot.getInstance().getCobblePickupMap().isCobblePickup(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

}