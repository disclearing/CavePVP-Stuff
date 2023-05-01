package net.frozenorb.foxtrot.gameplay.ability.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import com.mysql.jdbc.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AbilityParameter implements ParameterType<Ability> {

    @Override
    public Ability transform(CommandSender commandSender, String s) {

        final Ability toReturn = Foxtrot.getInstance().getMapHandler().getAbilityHandler().fromName(s);

        if (toReturn == null) {
            commandSender.sendMessage(ChatColor.RED + "Ability " + ChatColor.YELLOW + s + ChatColor.RED + " not found.");
            return null;
        }

        return toReturn;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values().stream().filter(ability -> StringUtils.startsWithIgnoreCase(ability.getName(),source)).map(Ability::getName).collect(Collectors.toList());
    }
}
