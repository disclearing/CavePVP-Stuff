package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetGemRequirementCommand {

    @Command(names = { "game setgemrequired", "game setgemreq" }, description = "Set the ongoing game's gem requirement", permission = "op")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null) {
            Game game = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            if (game != null) {
                game.setGemRequiredToJoin(!game.isGemRequiredToJoin());
                player.sendMessage(ChatColor.GREEN + "Gem requirement has been " + ChatColor.WHITE + (game.isGemRequiredToJoin() ? "enabled" : "disabled") + ChatColor.GREEN + "!");
            } else {
                player.sendMessage(ChatColor.RED + "There is no ongoing game.");
            }
        }
    }

}
