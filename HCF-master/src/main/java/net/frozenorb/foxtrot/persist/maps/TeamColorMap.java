package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

import java.util.UUID;

public class TeamColorMap extends PersistMap<ChatColor> {

    public TeamColorMap() {
        super("TeamColor", "TeamColor");
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
        return (contains(check) ? getValue(check) : ChatColor.DARK_GREEN);
    }

    public void setChatColor(UUID update, ChatColor chatColor) {
        updateValueAsync(update, chatColor);
    }

}