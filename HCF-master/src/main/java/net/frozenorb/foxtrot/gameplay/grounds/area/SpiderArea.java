package net.frozenorb.foxtrot.gameplay.grounds.area;

import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class SpiderArea extends GroundsArea {
    @Override
    public String getGroundsID() {
        return "Spider";
    }

    @Override
    public String getGroundsDisplayName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Spider";
    }

    @Override
    public EntityType getSpawnerType() {
        return EntityType.SPIDER;
    }

    @Override
    public List<EntityType> getGuardTypes() {
        return Collections.singletonList(EntityType.SPIDER);
    }
}
