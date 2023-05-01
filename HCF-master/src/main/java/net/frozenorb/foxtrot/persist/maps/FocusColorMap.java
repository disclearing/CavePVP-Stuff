package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

import java.util.UUID;

public class FocusColorMap extends PersistMap<ChatColor> {

    public FocusColorMap() {
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
        return (contains(check) ? getValue(check) : ChatColor.DARK_PURPLE);
    }

    public void setChatColor(UUID update, ChatColor chatColor) {
        updateValueAsync(update, chatColor);
    }

}