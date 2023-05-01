package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

import java.util.UUID;

public class TeamFocusColorMap extends PersistMap<ChatColor> {

    public TeamFocusColorMap() {
        super("FocusColor", "FocusColor");
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
        return (contains(check) ? getValue(check) : ChatColor.LIGHT_PURPLE);
    }

    public void setChatColor(UUID update, ChatColor chatColor) {
        updateValueAsync(update, chatColor);
    }

}