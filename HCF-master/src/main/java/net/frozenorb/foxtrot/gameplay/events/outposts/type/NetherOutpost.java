package net.frozenorb.foxtrot.gameplay.events.outposts.type;

import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetherOutpost extends Outpost {
    @Override
    public String getId() {
        return "Nether";
    }

    @Override
    public ChatColor getDisplayColor() {
        return ChatColor.DARK_RED;
    }

    @Override
    public Material getMaterial() {
        return Material.NETHERRACK;
    }

    @Override
    public int getSlot() {
        return 16;
    }

    @Override
    public List<String> getBenefits() {
        return Collections.singletonList("Double KOTH Points");
    }
}
