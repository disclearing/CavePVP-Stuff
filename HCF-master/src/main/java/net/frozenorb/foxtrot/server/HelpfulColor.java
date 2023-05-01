package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;

@AllArgsConstructor
public enum HelpfulColor {
    WHITE(Color.WHITE),
    SILVER(Color.SILVER),
    GRAY(Color.GRAY),
    RED(Color.RED),
    MAROON(Color.MAROON),
    YELLOW(Color.YELLOW),
    OLIVE(Color.OLIVE),
    LIME(Color.LIME),
    GREEN(Color.GREEN),
    AQUA(Color.AQUA),
    TEAL(Color.TEAL),
    BLUE(Color.BLUE),
    NAVY(Color.NAVY),
    PURPLE(Color.PURPLE),
    ORANGE(Color.ORANGE);

    @Getter Color color;
}
