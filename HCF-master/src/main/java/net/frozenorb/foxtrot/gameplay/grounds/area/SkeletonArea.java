package net.frozenorb.foxtrot.gameplay.grounds.area;

import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class SkeletonArea extends GroundsArea {
    @Override
    public String getGroundsID() {
        return "Skeleton";
    }

    @Override
    public String getGroundsDisplayName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Skeleton";
    }

    @Override
    public EntityType getSpawnerType() {
        return EntityType.SKELETON;
    }

    @Override
    public List<EntityType> getGuardTypes() {
        return Collections.singletonList(EntityType.SKELETON);
    }
}
