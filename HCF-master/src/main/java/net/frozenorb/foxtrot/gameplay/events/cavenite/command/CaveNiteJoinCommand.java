package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CaveNiteJoinCommand {

    @Command(names = {"cavenite join"}, permission = "")
    public static void execute(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            player.sendMessage(ChatColor.RED + "The game isn't active!");
            return;
        }

        if (caveNiteHandler.getGameState() != CaveNiteState.WAITING) {
            player.sendMessage(ChatColor.RED + "The game has already started!");
            return;
        }

        if (caveNiteHandler.getOnlinePlayers().contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already in the game queue!");
            return;
        }

        caveNiteHandler.addPlayer(player);
    }

    @Command(names = {"cavenite spec"}, permission = "")
    public static void addSpec(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
            player.sendMessage(ChatColor.RED + "The game isn't active!");
            return;
        }

        if (caveNiteHandler.getOnlinePlayers().contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already a player!");
            return;
        }

        if (caveNiteHandler.getOnlineSpectators().contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already a spectator!");
            return;
        }

        caveNiteHandler.addSpectator(player);
    }
}
