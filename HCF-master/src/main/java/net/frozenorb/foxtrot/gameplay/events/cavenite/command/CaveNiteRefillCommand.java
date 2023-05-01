package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class CaveNiteRefillCommand {

    @Command(names = {"cavenite refillpotions"}, permission = "op")
    public static void execute(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        if (caveNiteHandler == null) {
            player.sendMessage(ChatColor.RED + "Cave Nite doesn't exist on here!");
            return;
        }

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            player.sendMessage(ChatColor.RED + "Game is inactive.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Refilling players...");
        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

            if (!caveNiteHandler.getPlayersRemaining().contains(onlinePlayer.getUniqueId())) {
                continue;
            }

            while (onlinePlayer.getInventory().firstEmpty() != -1) {
                onlinePlayer.getInventory().addItem(new Potion(PotionType.INSTANT_HEAL, 2, true).toItemStack(1));
            }
        }
    }

}
