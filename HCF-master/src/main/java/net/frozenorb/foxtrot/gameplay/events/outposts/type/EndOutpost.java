package net.frozenorb.foxtrot.gameplay.events.outposts.type;

import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;
import java.util.List;

public class EndOutpost extends Outpost {
    @Override
    public String getId() {
        return "End";
    }

    @Override
    public ChatColor getDisplayColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public Material getMaterial() {
        return Material.EYE_OF_ENDER;
    }

    @Override
    public int getSlot() {
        return 10;
    }

    @Override
    public List<String> getBenefits() {
        return Collections.singletonList("Double Kill Points");
    }
}
