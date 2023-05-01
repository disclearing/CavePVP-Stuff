package cc.fyre.proton.nametag.provider;

import cc.fyre.proton.nametag.construct.NameTagInfo;
import org.bukkit.entity.Player;

public class DefaultNameTagProvider extends NameTagProvider {

    public DefaultNameTagProvider() {
        super("Default Provider", 0);
    }

    @Override
    public NameTagInfo fetchNameTag(Player toRefresh,Player refreshFor) {
        return (createNameTag(toRefresh.getDisplayName().replace(toRefresh.getName(),""), ""));
    }

}
