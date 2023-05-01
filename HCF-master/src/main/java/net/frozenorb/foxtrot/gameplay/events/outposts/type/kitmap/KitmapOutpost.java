package net.frozenorb.foxtrot.gameplay.events.outposts.type.kitmap;

import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

public class KitmapOutpost extends Outpost {
    @Override
    public String getId() {
        return "Kits";
    }

    @Override
    public String getDisplayName() {
        return this.getDisplayColor() + ChatColor.BOLD.toString() + "Outpost";
    }

    @Override
    public String getFactionName() {
        return "Outpost";
    }

    @Override
    public ChatColor getDisplayColor() {
        return ChatColor.GOLD;
    }

    @Override
    public Material getMaterial() {
        return Material.EMERALD;
    }

    @Override
    public int getSlot() {
        return 13;
    }

    @Override
    public List<String> getBenefits() {
        return Collections.singletonList("Double Gems for your whole faction.");
    }
}
