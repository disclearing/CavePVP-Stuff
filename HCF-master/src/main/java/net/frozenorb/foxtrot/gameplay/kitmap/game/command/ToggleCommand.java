package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ToggleCommand {

    public static void execute(CommandSender sender) {
        Foxtrot.getInstance().getMapHandler().getGameHandler().setDisabled(!Foxtrot.getInstance().getMapHandler().getGameHandler().isDisabled());

        if (Foxtrot.getInstance().getMapHandler().getGameHandler().isDisabled()) {
            sender.sendMessage(ChatColor.YELLOW + "Events are now disabled!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Events are now enabled!");
        }
    }

}
