package net.frozenorb.foxtrot.gameplay.events.conquest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ConquestCapzone {

    RED(ChatColor.RED, "Red"),
    BLUE(ChatColor.BLUE, "Blue"),
    GREEN(ChatColor.GREEN, "Green"),
    YELLOW(ChatColor.YELLOW, "Yellow");

    @Getter private ChatColor chatColor;
    @Getter private String name;

}