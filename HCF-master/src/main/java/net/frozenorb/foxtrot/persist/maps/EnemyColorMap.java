package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

import java.util.UUID;

public class EnemyColorMap extends PersistMap<ChatColor> {

    public EnemyColorMap() {
        super("EnemyColor", "EnemyColor");
    }

    @Override
    public String getRedisValue(ChatColor chatColor) {
        return (chatColor.name());
    }

    @Override
    public ChatColor getJavaObject(String str) {
        return (ChatColor.valueOf(str));
    }

    @Override
    public Object getMongoValue(ChatColor chatColor) {
        return (chatColor);
    }

    public ChatColor getChatColor(UUID check) {
        return (contains(check) ? getValue(check) : ChatColor.RED);
    }

    public void setChatColor(UUID update, ChatColor chatColor) {
        updateValueAsync(update, chatColor);
    }

}