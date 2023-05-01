package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
@Getter
public class UpgradableKit {
    private final String kitName;
    private final ItemStack icon;
    private final Map<Material, Upgrades> upgrades;
}
