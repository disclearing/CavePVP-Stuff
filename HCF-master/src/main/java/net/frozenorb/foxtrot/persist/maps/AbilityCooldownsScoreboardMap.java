package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class AbilityCooldownsScoreboardMap extends PersistMap<Boolean> {

    public AbilityCooldownsScoreboardMap() {
        super("AbilityCooldownsScoreboard", "AbilityCooldownsScoreboardEnabled");
    }

    @Override
    public String getRedisValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void setStatus(UUID update,boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isScoreboard(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}

