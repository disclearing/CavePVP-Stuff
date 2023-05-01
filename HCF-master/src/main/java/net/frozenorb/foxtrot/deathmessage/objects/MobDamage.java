package net.frozenorb.foxtrot.deathmessage.objects;

import lombok.Getter;
import org.bukkit.entity.EntityType;

public abstract class MobDamage extends Damage {

    @Getter private EntityType mobType;

    public MobDamage(String damaged, double damage, EntityType mobType) {
        super(damaged, damage);
        this.mobType = mobType;
    }

}