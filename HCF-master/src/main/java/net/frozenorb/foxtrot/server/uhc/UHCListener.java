package net.frozenorb.foxtrot.server.uhc;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.PlayerUtils;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardScore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerHealthChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UHCListener implements Listener {

    private static final String BELOW_NAME_OBJECTIVE_NAME = "BelowName";
    private static final char HEART_CHAR = '❤';
    private static final DecimalFormat HEARTS_FORMAT = new DecimalFormat("#.#");

    private static Field aField = null;
    private static Field bField = null;
    private static Field cField = null;
    private static Field dField = null;

    static {
        try {
            aField = PacketPlayOutScoreboardScore.class.getDeclaredField("a");
            aField.setAccessible(true);

            bField = PacketPlayOutScoreboardScore.class.getDeclaredField("b");
            bField.setAccessible(true);

            cField = PacketPlayOutScoreboardScore.class.getDeclaredField("c");
            cField.setAccessible(true);

            dField = PacketPlayOutScoreboardScore.class.getDeclaredField("d");
            dField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private Map<UUID, Long> deathTime = new HashMap<>();

    public UHCListener() {
        ShapedRecipe recipe = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE));
        recipe.shape(
                "NNN",
                "NAN",
                "NNN"
        );
        recipe.setIngredient('N', Material.GOLD_NUGGET);
        recipe.setIngredient('A', Material.APPLE);

        Bukkit.addRecipe(recipe);
    }

    private void init(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        Objective objective = scoreboard.registerNewObjective(BELOW_NAME_OBJECTIVE_NAME, Criterias.HEALTH);
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.DARK_RED.toString() + HEART_CHAR);

        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> {
            updateAllTo(player);
            updateToAll(player);
        });
    }

    private void updateToAll(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

        // not yet initialized
        if (objective == null) {
            return;
        }

        try {
            PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
            aField.set(packet, player.getName());
            bField.set(packet, BELOW_NAME_OBJECTIVE_NAME);
            cField.set(packet, getHealth(player));
            dField.set(packet, 0);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.getScoreboard() == null || onlinePlayer.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) == null) {
                    continue;
                }

                ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAllTo(Player player) {
        Objective belowNameObjective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

        // not yet initialized
        if (belowNameObjective == null) {
            return;
        }

        try {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (player.getScoreboard() == null || player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) == null) {
                    continue;
                }

                PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
                aField.set(packet, onlinePlayer.getName());
                bField.set(packet, BELOW_NAME_OBJECTIVE_NAME);
                cField.set(packet, getHealth(onlinePlayer));
                dField.set(packet, 0);

                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (isOldCrappleRecipe(event.getRecipe())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPrepare(PrepareItemCraftEvent event) {
        if (isOldCrappleRecipe(event.getRecipe())) {
            ItemStack result = ItemBuilder.of(Material.WOOL).data(DyeColor.RED.getWoolData()).name("&cThis recipe is disabled.").addToLore("&eUse gold nuggets instead of ingots.").build();

            event.getInventory().setResult(result);
        }
    }

    private boolean isOldCrappleRecipe(Recipe r) {
        if (!(r instanceof ShapedRecipe)) {
            return false;
        }

        ShapedRecipe recipe = (ShapedRecipe) r;

        char[] goldChars = ("abc" + "df" + "ghi").toCharArray();
        char appleChar = 'e';

        for (char c : goldChars) {
            if (recipe.getIngredientMap().get(c) == null) {
                return false;
            }

            if (recipe.getIngredientMap().get(c).getType() != Material.GOLD_INGOT) {
                return false;
            }
        }

        return recipe.getIngredientMap().get(appleChar).getType() == Material.APPLE;
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> init(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onHealthChange(PlayerHealthChangeEvent event) {
        updateToAll(event.getPlayer());
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();

        if (item.getType() != Material.GOLDEN_APPLE) {
            return;
        }

        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 8 * 25, 1), true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player damaged = (Player) event.getEntity();
            Player damager = PlayerUtils.getDamageSource(event.getDamager());

            if (damager != null) {
                // we have to delay this for the damage to apply
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!deathTime.containsKey(damaged.getUniqueId()) || (System.currentTimeMillis() - deathTime.get(damaged.getUniqueId()) > 200L)) {
                            damager.sendMessage(damaged.getDisplayName() + ChatColor.YELLOW + " is now at " + ChatColor.RED + formatHearts(damaged, true) + ChatColor.YELLOW + ".");
                        }
                    }

                }.runTask(Foxtrot.getInstance());
            }
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player) || event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) {
            return;
        }

        Player player = (Player) event.getEntity();
        Team team = LandBoard.getInstance().getTeam(player.getLocation());

        if (team == null || !team.isMember(player.getUniqueId()) || SpawnTagHandler.isTagged(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void enderpearlDamage(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getDamager() instanceof EnderPearl) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        deathTime.put(event.getEntity().getUniqueId(), System.currentTimeMillis());
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> deathTime.remove(event.getEntity().getUniqueId()), 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeafDecay(LeavesDecayEvent event) {
        if (event.getBlock().getType() == Material.LEAVES) {
            if ((Math.random() * 100) <= 3) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        } else if (event.getBlock().getType() == Material.LEAVES_2) {
            if ((Math.random() * 100) <= 3) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        int multiplier = 1;

        if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.SHEARS && event.getPlayer().getItemInHand().hasItemMeta() && event.getPlayer().getItemInHand().getItemMeta().hasDisplayName() && event.getPlayer().getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.GOLD.toString() + ChatColor.BOLD)) {
            multiplier = 3;
        }

        if (event.getBlock().getType() == Material.LEAVES) {
            if ((Math.random() * 100) <= 3 * multiplier) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        } else if (event.getBlock().getType() == Material.LEAVES_2) {
            if ((Math.random() * 100) <= 3 * multiplier) {
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
            }
        }
    }

    private static String formatHearts(Player player, boolean heartChar) {
        return HEARTS_FORMAT.format(getHearts(player)) + (heartChar ? HEART_CHAR : "");
    }

    private static double getHearts(Player player) {
        return getHearts0(player.getHealth() + ((CraftPlayer) player).getHandle().getAbsorptionHearts());
    }

    private static int getHealth(Player player) {
        return (int) Math.ceil(player.getHealth() + ((CraftPlayer) player).getHandle().getAbsorptionHearts());
    }

    private static double getHearts0(double health) {
        return (double) Math.round((Math.ceil(health) / 2) * 2) / 2;
    }

}
