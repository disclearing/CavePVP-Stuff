package net.frozenorb.foxtrot.gameplay.clickitem;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.clickitem.parameter.ClickItemParameter;
import net.frozenorb.foxtrot.gameplay.clickitem.type.BossSummoner;
import net.frozenorb.foxtrot.gameplay.clickitem.type.RandomAbilityItem;
import net.frozenorb.foxtrot.gameplay.clickitem.type.RandomArmorClass;
import net.frozenorb.foxtrot.gameplay.clickitem.type.RandomSpawner;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.parameter.ItemBoxParameter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ClickItemHandler implements Listener {
    private Foxtrot instance;

    @Getter private List<ClickItem> clickItems = new ArrayList<>();

    public ClickItemHandler(Foxtrot instance) {
        this.instance = instance;

        this.clickItems.add(new BossSummoner());
        this.clickItems.add(new RandomAbilityItem());
        this.clickItems.add(new RandomArmorClass());
        this.clickItems.add(new RandomSpawner());

        Proton.getInstance().getCommandHandler().registerParameterType(ClickItem.class, new ClickItemParameter());
        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    public ClickItem findClickItem(String id) {
        return this.clickItems.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
