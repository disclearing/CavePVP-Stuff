package net.frozenorb.foxtrot.deathmessage.objects;

import lombok.Getter;

public abstract class PlayerDamage extends Damage {

    @Getter private String damager;

    public PlayerDamage(String damaged, double damage, String damager) {
        super(damaged, damage);
        this.damager = damager;
    }

}