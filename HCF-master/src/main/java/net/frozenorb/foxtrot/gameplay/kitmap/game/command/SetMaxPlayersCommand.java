package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetMaxPlayersCommand {

    @Command(names = { "game setmaxplayers" }, description = "Set the ongoing game's max players", permission = "op")
    public static void execute(Player player, @Parameter(name = "players") int maxPlayers) {
        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null) {
            Game game = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            if (game != null) {
                game.setMaxPlayers(maxPlayers);
                player.sendMessage(ChatColor.GREEN + "Set max players of " + game.getGameType().getDisplayName() + ChatColor.GREEN + " event to " + ChatColor.WHITE + maxPlayers + ChatColor.GREEN + "!");
            } else {
                player.sendMessage(ChatColor.RED + "There is no ongoing game.");
            }
        }
    }

}
