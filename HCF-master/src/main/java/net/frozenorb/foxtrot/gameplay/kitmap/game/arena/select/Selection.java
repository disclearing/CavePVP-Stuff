package net.frozenorb.foxtrot.gameplay.kitmap.game.arena.select;

import cc.fyre.proton.cuboid.Cuboid;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@Data
@AllArgsConstructor
public class Selection {

    public static final ItemStack SELECTION_WAND = ItemBuilder.of(Material.GOLD_AXE).name("&aSelection Wand").build();
    public static final String SELECTION_METADATA_KEY = "selection";

    private Location loc1, loc2;

    private Selection() {}

    public Cuboid getCuboid() {
        if (!isComplete()) return null;

        return new Cuboid(loc1, loc2);
    }

    public boolean isComplete() {
        return loc1 != null && loc2 != null;
    }

    public static Selection getOrCreateSelection(Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (Selection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }

        Selection selection = new Selection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(Foxtrot.getInstance(), selection));

        return selection;
    }

}
