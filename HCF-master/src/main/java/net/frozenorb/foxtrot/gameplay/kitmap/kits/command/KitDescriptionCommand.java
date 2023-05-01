package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDescriptionCommand {

    @Command(names = { "defaultkit setdesc" }, description = "Sets the description of a kit", permission = "op")
    public static void execute(Player player, @Parameter(name = "kit") DefaultKit kit, @Parameter(name = "description", wildcard = true) String description) {
        kit.setDescription(description);
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + "!");
    }

}
