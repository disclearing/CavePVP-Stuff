package net.frozenorb.foxtrot.gameplay.loot.crate.command.parameterType;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CrateParameterProvider implements ParameterType<Crate> {
    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return null;
    }

    @Override
    public Crate transform(CommandSender commandSender, String s) {
        final Optional<Crate> optionalCrate = Foxtrot.getInstance().getCrateHandler().findById(s);

        if (!optionalCrate.isPresent()) {
            commandSender.sendMessage(ChatColor.translate("&cCrate named &e'" + s + "' &cdoesn't exist."));
            return null;
        }

        return optionalCrate.get();
    }
}
