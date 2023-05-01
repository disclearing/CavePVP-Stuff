package net.frozenorb.foxtrot.gameplay.armorclass.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ArmorClassParameter implements ParameterType<ArmorClass> {

    @Override
    public ArmorClass transform(CommandSender commandSender, String s) {
        final ArmorClass toReturn = Foxtrot.getInstance().getArmorClassHandler().findArmorClass(s);

        if (toReturn == null) {
            commandSender.sendMessage(ChatColor.RED + "Armor class " + ChatColor.YELLOW + s + ChatColor.RED + " not found.");
            return null;
        }

        return toReturn;
    }
}
