package net.frozenorb.foxtrot.util;

import net.minecraft.util.com.google.common.base.Strings;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public final class CC {

    public static final String BLUE = ChatColor.BLUE.toString();
    public static final String AQUA = ChatColor.AQUA.toString();
    public static final String YELLOW = ChatColor.YELLOW.toString();
    public static final String RED = ChatColor.RED.toString();
    public static final String GRAY = ChatColor.GRAY.toString();
    public static final String GOLD = ChatColor.GOLD.toString();
    public static final String GREEN = ChatColor.GREEN.toString();
    public static final String WHITE = ChatColor.WHITE.toString();
    public static final String BLACK = ChatColor.BLACK.toString();
    public static final String BOLD = ChatColor.BOLD.toString();
    public static final String ITALIC = ChatColor.ITALIC.toString();
    public static final String UNDERLINE = ChatColor.UNDERLINE.toString();
    public static final String STRIKETHROUGH = ChatColor.STRIKETHROUGH.toString();
    public static final String RESET = ChatColor.RESET.toString();
    public static final String MAGIC = ChatColor.MAGIC.toString();
    public static final String OBFUSCATED = MAGIC;
    public static final String B = BOLD;
    public static final String M = MAGIC;
    public static final String O = MAGIC;
    public static final String I = ITALIC;
    public static final String S = STRIKETHROUGH;
    public static final String R = RESET;
    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
    public static final String DARK_RED = ChatColor.DARK_RED.toString();
    public static final String D_BLUE = DARK_BLUE;
    public static final String D_AQUA = DARK_AQUA;
    public static final String D_GRAY = DARK_GRAY;
    public static final String D_GREEN = DARK_GREEN;
    public static final String D_PURPLE = DARK_PURPLE;
    public static final String D_RED = DARK_RED;
    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static final String L_PURPLE = LIGHT_PURPLE;
    public static final String PINK = L_PURPLE;
    public static final String SCOREBAORD_SEPARATOR = CC.GRAY + CC.S + "----------------------";
    public static final String HORIZONTAL_SEPARATOR = CC.GRAY + CC.S + Strings.repeat("-", 51);
    public static final String VERTICAL_SEPARATOR = CC.GRAY + StringEscapeUtils.unescapeJava("\u2758");

    public static final String ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
    public static final String ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25B6");
    public static final String ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
    public static final String ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");

    private CC() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static List<String> translateLines(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return toReturn;
    }

    public static String strip(String in) {
        return ChatColor.stripColor(translate(in));
    }

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }


    public static String translateAlternateColorCodes(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }
}
