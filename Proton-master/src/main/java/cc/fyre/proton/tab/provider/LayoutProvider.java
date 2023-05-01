package cc.fyre.proton.tab.provider;

import cc.fyre.proton.tab.construct.TabLayout;
import org.bukkit.entity.Player;

public interface LayoutProvider {

    TabLayout provide(Player player);

}
