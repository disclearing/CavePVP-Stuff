package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceStartCommand {

    @Command(names = { "game forcestart" }, description = "Force start an event", permission = "game.command.start")
    public static void execute(Player player) {
        if (!Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.getState() == GameState.WAITING) {
            ongoingGame.forceStart();
        } else {
            player.sendMessage(ChatColor.RED + "Can't force start an event that has already been started.");
        }
    }

}
