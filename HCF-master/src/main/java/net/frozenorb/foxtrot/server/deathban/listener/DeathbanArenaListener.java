package net.frozenorb.foxtrot.server.deathban.listener;

import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

@AllArgsConstructor
public class DeathbanArenaListener implements Listener {
    private Foxtrot instance;

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null || !block.getType().name().contains("SIGN") || !(block.getState() instanceof Sign)) {
            return;
        }

        if (!block.getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        final Sign sign = (Sign) block.getState();

        if (sign.getLine(1) == null || !sign.getLine(1).startsWith(ChatColor.BLUE + "- Respawn -")) {
            return;
        }

        if (!this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            this.instance.getDeathbanArenaHandler().revive(player.getUniqueId());
            return;
        }

        long lifeCooldown = this.instance.getDeathbanArenaHandler().getLifeCooldown().getOrDefault(player.getUniqueId(), 0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown-System.currentTimeMillis())/1000;

            player.sendMessage(ChatColor.translate("&cYou may not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference) + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        int lives = this.instance.getFriendLivesMap().getLives(player.getUniqueId());

        if (lives > 0) {
            this.instance.getFriendLivesMap().setLives(player.getUniqueId(), this.instance.getFriendLivesMap().getLives(player.getUniqueId())-1);
            this.instance.getDeathbanArenaHandler().revive(player.getUniqueId());
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't afford a revive!");
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }

        if (player.getWorld().getName().equalsIgnoreCase("Deathban")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not do that here!");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return;
        }

        if (player.getWorld().getName().equalsIgnoreCase("Deathban")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not do that here!");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            player.teleport(this.instance.getServer().getWorld("Deathban").getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onRespawn(PlayerRespawnEvent event) {
        if (this.instance.getDeathbanArenaHandler().isDeathbanArena(event.getPlayer()) && this.instance.getServer().getWorld("Deathban") != null) {
            final Player player = event.getPlayer();
            final Kit kit = Suge.getInstance().getKitHandler().findKit("UltimateDiamond").orElse(null);

            if (kit != null) {
                InventoryUtils.resetInventoryNow(player);
                kit.apply(player);
            }

            event.setRespawnLocation(this.instance.getServer().getWorld("Deathban").getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getLocation().getWorld().getName().equalsIgnoreCase("Deathban")) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (player.getWorld().getName().equalsIgnoreCase("Deathban")) {
            event.getDrops().clear();
        }

        if (killer == null || killer.getName().equalsIgnoreCase(player.getName())) {
            return;
        }

        if (!this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            return;
        }

        if (!this.instance.getDeathbanArenaHandler().isDeathbanArena(killer)) {
            return;
        }

        long lifeCooldown = this.instance.getDeathbanArenaHandler().getLifeCooldown().getOrDefault(player.getUniqueId(), 0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown-System.currentTimeMillis())/1000;

            player.sendMessage(ChatColor.translate("&cYou may not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference) + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        int kills = this.instance.getDeathbanArenaHandler().getCache().getOrDefault(killer.getUniqueId(), 0)+1;

        if (kills >= 5) {
            this.instance.getDeathbanArenaHandler().revive(killer.getUniqueId());
        } else {
            this.instance.getDeathbanArenaHandler().getCache().put(killer.getUniqueId(), kills);

            killer.sendMessage(ChatColor.RED + "You need " + ChatColor.WHITE + (5-kills) + " kills" + ChatColor.RED + " until you get undeathbanned.");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (event.getMessage().startsWith("/request") || event.getMessage().startsWith("/report") || event.getMessage().startsWith("/helpop") || event.getMessage().startsWith("/hub") || event.getMessage().startsWith("/msg ") || event.getMessage().startsWith("/r ") || event.getMessage().startsWith("/reply ") || event.getMessage().startsWith("/message ") || event.getMessage().startsWith("/m ") || event.getMessage().startsWith("/w ") || event.getMessage().startsWith("/tell ")) {
            return;
        }

        if (!player.getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        if (!this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            return;
        }

        if (player.hasPermission("foxtrot.staff")) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You may not run that command whilst deathbanned!");
        event.setCancelled(true);
    }

}
