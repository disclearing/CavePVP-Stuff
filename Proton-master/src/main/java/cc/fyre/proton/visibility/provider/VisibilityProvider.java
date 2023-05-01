package cc.fyre.proton.visibility.provider;

import cc.fyre.proton.visibility.action.VisibilityAction;
import org.bukkit.entity.Player;

public interface VisibilityProvider {

    VisibilityAction getAction(Player player,Player target);

}
