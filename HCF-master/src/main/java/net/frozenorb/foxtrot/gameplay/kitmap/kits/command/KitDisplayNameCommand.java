package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitDisplayNameCommand {

    @Command(names = { "defaultkit displayname" }, description = "Sets the displayname of a kit", permission = "op")
    public static void execute(Player player, @Parameter(name = "kit") DefaultKit kit, @Parameter(name = "displayname", wildcard = true) String displayName) {
        kit.setDisplayName(displayName);
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set display name of " + kit.getName() + "!");
    }

}
