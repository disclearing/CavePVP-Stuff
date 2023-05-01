package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramParameterType implements ParameterType<Hologram> {

    @Override
    public Hologram transform(CommandSender sender,String source) {

        int id;

        try {
            id = Integer.parseInt(source);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Provided id " + ChatColor.YELLOW + source + ChatColor.RED + " is not an integer.");
            return null;
        }

        final Hologram hologram = Proton.getInstance().getHologramHandler().getCache().get(id);

        if (hologram == null) {
            sender.sendMessage(ChatColor.RED + "Hologram with id " + ChatColor.YELLOW + id + ChatColor.RED + " does not exist.");
            return null;
        }

        return hologram;
    }

}
