package org.cavepvp.suge.kit.command.parameter;

import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

import java.util.Optional;

public class KitTypeParameter implements ParameterType<Kit> {
    @Override
    public Kit transform(CommandSender commandSender, String s) {
        final Optional<Kit> optionalKit = Suge.getInstance().getKitHandler().findKit(s);

        if (!optionalKit.isPresent()) {
            commandSender.sendMessage(ChatColor.RED + "The kit named " + ChatColor.YELLOW + "'" + s + "'" + ChatColor.RED + " doesn't exist!");
            return null;
        }

        return optionalKit.get();
    }
}
