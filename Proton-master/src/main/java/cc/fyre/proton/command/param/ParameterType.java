package cc.fyre.proton.command.param;

import cc.fyre.proton.Proton;
import com.mysql.jdbc.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ParameterType<T> {

    T transform(CommandSender sender,String source);

    default List<String> tabComplete(Player sender,Set<String> flags,String source) {
        return Proton.getInstance().getServer().getOnlinePlayers()
                .stream()
                .filter(loopPlayer -> StringUtils.startsWithIgnoreCase(loopPlayer.getName(),source) && Proton.getInstance().getVisibilityHandler().treatAsOnline(loopPlayer,sender))
                .map(Player::getName)
                .collect(Collectors.toList());
    }

}