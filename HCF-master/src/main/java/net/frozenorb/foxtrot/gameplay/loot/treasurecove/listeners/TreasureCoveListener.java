package net.frozenorb.foxtrot.gameplay.loot.treasurecove.listeners;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.event.HourEvent;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class TreasureCoveListener implements Listener {
    private Foxtrot instance;

    public static long SPAWN_IN = 0;

    @EventHandler
    public void onHour(HourEvent event) {
        final Server server = this.instance.getServer();

        if (event.getHour() % 3 == 0) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(this.instance, () -> {
                final Location centralChest = this.instance.getTreasureCoveHandler().getCentralChest();

                server.broadcastMessage("");
                server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Treasure Cove");
                server.broadcastMessage(ChatColor.GRAY + "All chests have regenerated!");
                server.broadcastMessage(ChatColor.translate("&cLocation: &f" + centralChest.getBlockX() + ", " + centralChest.getBlockY() + ", " + centralChest.getBlockZ()));
                server.broadcastMessage("");

                Foxtrot.getInstance().getTreasureCoveHandler().respawnTreasureChests();
            }, 20*30);
        }

        if (this.instance.getTreasureCoveHandler().getCentralChest() == null) {
            return;
        }

        final Location centralChest = this.instance.getTreasureCoveHandler().getCentralChest();

        if (event.getHour() == 15) {
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Treasure Cove");
            server.broadcastMessage(ChatColor.GRAY + "A random Buycraft Voucher will appear in the middle of Treasure Cove in " + ChatColor.WHITE + "1 hour" + ChatColor.GRAY + "!");
            server.broadcastMessage(ChatColor.translate("&cLocation: &f" + centralChest.getBlockX() + ", " + centralChest.getBlockY() + ", " + centralChest.getBlockZ()));
            server.broadcastMessage("");

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                SPAWN_IN = System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5L);

                server.broadcastMessage("");
                server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Treasure Cove");
                server.broadcastMessage(ChatColor.GRAY + "A random Buycraft Voucher will appear in the middle of Treasure Cove in " + ChatColor.WHITE + "5 minutes" + ChatColor.GRAY + "!");
                server.broadcastMessage(ChatColor.translate("&cLocation: &f" + centralChest.getBlockX() + ", " + centralChest.getBlockY() + ", " + centralChest.getBlockZ()));
                server.broadcastMessage("");
            }, 20*60*55);
            return;
        }

        if (event.getHour() == 16) {
            for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

                onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
                onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &4&lTreasure Cove"));
                onlinePlayer.sendMessage(ChatColor.translate("&7███" + "&4█" + "&7███ &7A voucher has dropped!"));
                onlinePlayer.sendMessage(ChatColor.translate("&7███" + "&4█" + "&7███"));
                onlinePlayer.sendMessage(ChatColor.translate("&7███" + "&4█" + "&7███ &cLocation:"));
                onlinePlayer.sendMessage(ChatColor.translate("&7███" + "&4█" + "&7███ &f" + centralChest.getBlockX() + ", " + centralChest.getBlockY() + ", " + centralChest.getBlockZ()));
                onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
                onlinePlayer.sendMessage("");

                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_SPAWN, 1, 1);
            }

            Foxtrot.getInstance().getTreasureCoveHandler().generateCentralChest();
        }
    }

    @EventHandler
    private void onEnter(TeamEnterClaimEvent event) {
        final Player player = event.getPlayer();
        final Team teamTo = event.getToTeam();

        if (teamTo == null || !player.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            return;
        }

        if (!teamTo.getName().startsWith("TreasureCove") || teamTo.getOwner() != null) {
            return;
        }

        final BattlePassProgress battlePassProgress = Foxtrot.getInstance().getBattlePassHandler().fetchProgress(player.getUniqueId());

        if (battlePassProgress == null) {
            return;
        }

        if (battlePassProgress.isVisitTreasureCove()) {
            return;
        }

        PlayerUtil.sendTitle(player, "&6&lTreasure Cove", "&eWelcome to Treasure Cove! Loot chests for free items!");

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () ->
                PlayerUtil.sendTitle(player, "&6&lTreasure Cove", "&eType &f/treasurecove &eto view when the next reset is!"), 20*6);

        battlePassProgress.setVisitTreasureCove(true);
        battlePassProgress.requiresSave();

        Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
    }

//    @EventHandler(priority = EventPriority.LOW)
//    private void onCommand(PlayerCommandPreprocessEvent event) {
//        final Player player = event.getPlayer();
//        final String message = event.getMessage().toLowerCase();
//
//        if (message.startsWith("/f show treasure") || message.startsWith("/t show treasure") || message.startsWith("/f who treasure") || message.startsWith("/t who treasure")
//                || message.startsWith("/t i treasure") || message.startsWith("/f i treasure")) {
//            event.setCancelled(true);
//            player.chat("/f show treasurecove");
//        }
//
//    }
}