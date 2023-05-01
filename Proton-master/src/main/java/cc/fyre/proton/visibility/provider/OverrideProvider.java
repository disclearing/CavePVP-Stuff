package cc.fyre.proton.visibility.provider;

import cc.fyre.proton.visibility.action.OverrideAction;
import org.bukkit.entity.Player;

public interface OverrideProvider {

    OverrideAction getAction(Player target,Player viewer);

}
