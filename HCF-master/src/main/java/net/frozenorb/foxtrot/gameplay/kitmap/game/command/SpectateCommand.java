package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.ItemUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectateCommand {

    @Command(names = {"spec", "spectate", "game spec", "game spectate"}, description = "Join an ongoing event", permission = "")
    public static void execute(Player player) {
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Spectating is currently disabled");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        if (ModHandler.INSTANCE.isInModMode(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can't join the event while in mod-mode.");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while spawn-tagged.");
            return;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return;
        }

        ongoingGame.addSpectator(player);
    }

}
