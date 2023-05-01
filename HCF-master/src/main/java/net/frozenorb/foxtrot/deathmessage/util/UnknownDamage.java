package net.frozenorb.foxtrot.deathmessage.util;

import net.frozenorb.foxtrot.deathmessage.objects.Damage;

public class UnknownDamage extends Damage {

    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    public String getDeathMessage() {
        return (wrapName(getDamaged()) + " died.");
    }

}