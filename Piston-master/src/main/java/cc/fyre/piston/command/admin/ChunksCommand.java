package cc.fyre.piston.command.admin;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.command.Command;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ChunksCommand {

    @Command(
            names = {"chunks"},
            permission = "command.chunks"
    )
    public static void execute(Player player) {

        player.sendMessage(ChatColor.GOLD + "Loaded chunks per world:");

        for (World world : Neutron.getInstance().getServer().getWorlds()) {
            player.sendMessage(ChatColor.YELLOW + StringUtils.capitalize(world.getName().contains("_") ? world.getName().replace("world","").replace("_","").replace("the",""):world.getName()) + ": " + ChatColor.RED + world.getLoadedChunks().length);
        }

    }

}
