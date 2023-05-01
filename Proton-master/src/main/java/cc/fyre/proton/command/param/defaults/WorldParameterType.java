package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldParameterType implements ParameterType<World> {

    public World transform(CommandSender sender, String source) {

        final World world = Proton.getInstance().getServer().getWorld(source);

        if (world == null) {
            sender.sendMessage(ChatColor.RED + "No world with the name " + source + " found.");
            return null;
        }

        return world;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return Proton.getInstance().getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList());
    }

}