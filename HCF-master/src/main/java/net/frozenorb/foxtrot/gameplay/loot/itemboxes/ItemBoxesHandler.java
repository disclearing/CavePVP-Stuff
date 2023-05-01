package net.frozenorb.foxtrot.gameplay.loot.itemboxes;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.parameter.ItemBoxParameter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ItemBoxesHandler implements Listener {
    private Foxtrot instance;

    @Getter private List<ItemBox> itemBoxes = new ArrayList<>();

    @Getter @Setter private ItemBox activeItemBox;

    public ItemBoxesHandler(Foxtrot instance) {
        this.instance = instance;

        for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(),"net.frozenorb.foxtrot.gameplay.loot.itemboxes.type")) {

            if (!ItemBox.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                this.itemBoxes.add((ItemBox)clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        Proton.getInstance().getCommandHandler().registerParameterType(ItemBox.class, new ItemBoxParameter());
        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    public ItemBox findItemBox(String id) {
        return this.itemBoxes.stream().filter(it -> it.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}
