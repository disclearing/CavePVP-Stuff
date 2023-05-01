package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class TeamfightModeMap extends PersistMap<Boolean> {

    public TeamfightModeMap() {
        super("TeamFight", "TeamFight");
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

    public void setTeamfight(UUID update,boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isTeamfight(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }

}

