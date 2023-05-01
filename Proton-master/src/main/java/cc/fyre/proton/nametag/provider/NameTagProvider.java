package cc.fyre.proton.nametag.provider;

import cc.fyre.proton.Proton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import cc.fyre.proton.nametag.construct.NameTagInfo;
import org.bukkit.entity.Player;

@AllArgsConstructor
public abstract class NameTagProvider {

    @Getter private String name;
    @Getter private int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh,Player refreshFor);

    public final NameTagInfo createNameTag(String prefix,String suffix) {
        return Proton.getInstance().getNameTagHandler().getOrCreate(prefix, suffix);
    }

}