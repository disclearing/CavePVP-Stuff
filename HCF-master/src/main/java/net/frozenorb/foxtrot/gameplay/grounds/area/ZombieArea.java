package net.frozenorb.foxtrot.gameplay.grounds.area;

import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class ZombieArea extends GroundsArea {
    @Override
    public String getGroundsID() {
        return "Zombie";
    }

    @Override
    public String getGroundsDisplayName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Zombie";
    }

    @Override
    public EntityType getSpawnerType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public List<EntityType> getGuardTypes() {
        return Collections.singletonList(EntityType.ZOMBIE);
    }
}
