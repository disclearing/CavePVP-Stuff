package net.frozenorb.foxtrot.server.keyalls.command.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

import java.util.Optional;

public class KeyAllParameter implements ParameterType<KeyAll> {
    @Override
    public KeyAll transform(CommandSender commandSender, String s) {
        final KeyAll keyAll = Foxtrot.getInstance().getKeyAllHandler().findKeyAll(s);

        if (keyAll == null) {
            commandSender.sendMessage(ChatColor.RED + "The key-all named " + ChatColor.YELLOW + "'" + s + "'" + ChatColor.RED + " doesn't exist!");
            return null;
        }

        return keyAll;
    }
}
