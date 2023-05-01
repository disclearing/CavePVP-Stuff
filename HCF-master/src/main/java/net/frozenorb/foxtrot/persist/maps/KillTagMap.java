package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.gameplay.killtags.KillTags;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.Bukkit;

import java.util.UUID;

public class KillTagMap extends PersistMap<KillTags> {

    public KillTagMap() {
        super("KillTags", "KillTags");
    }

    @Override
    public String getRedisValue(KillTags killTags) {
        return (killTags.name());
    }

    @Override
    public KillTags getJavaObject(String str) {
        return (KillTags.valueOf(str));
    }

    @Override
    public Object getMongoValue(KillTags killTags) {
        return (killTags.name());
    }

    public KillTags getKillTag(UUID check) {
        return (contains(check) ? getValue(check) : KillTags.NONE);
    }

    public void setKillTag(UUID update, KillTags killTags) {
        updateValueAsync(update, killTags);
    }

}