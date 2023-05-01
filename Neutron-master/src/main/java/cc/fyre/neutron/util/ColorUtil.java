package cc.fyre.neutron.util;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@UtilityClass
public class ColorUtil {

    // Credits: https://github.com/IPVP-MC/iBase/blob/master/base/src/main/java/com/doctordark/util/BukkitUtils.java
    public static final ImmutableMap<ChatColor,DyeColor> COLOR_MAP;
    public static final ImmutableMap<Character, ChatColor> CHAR_COLOR_MAP;

    static {

        COLOR_MAP = /*TODO:Maps.immutableEnumMap*/(ImmutableMap.<ChatColor, DyeColor>builder().
                put(ChatColor.AQUA, DyeColor.LIGHT_BLUE).
                put(ChatColor.BLACK, DyeColor.BLACK).
                put(ChatColor.BLUE, DyeColor.LIGHT_BLUE).
                put(ChatColor.DARK_AQUA, DyeColor.CYAN).
                put(ChatColor.DARK_BLUE, DyeColor.BLUE).
                put(ChatColor.DARK_GRAY, DyeColor.GRAY).
                put(ChatColor.DARK_GREEN, DyeColor.GREEN).
                put(ChatColor.DARK_PURPLE, DyeColor.PURPLE).
                put(ChatColor.DARK_RED, DyeColor.RED).
                put(ChatColor.GOLD, DyeColor.ORANGE).
                put(ChatColor.GRAY, DyeColor.SILVER).
                put(ChatColor.GREEN, DyeColor.LIME).
                put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA).
                put(ChatColor.RED, DyeColor.RED).
                put(ChatColor.WHITE, DyeColor.WHITE).
                put(ChatColor.YELLOW, DyeColor.YELLOW).build());
        CHAR_COLOR_MAP = (ImmutableMap.<Character, ChatColor>builder()
                .put('0',ChatColor.BLACK)
                .put('1',ChatColor.DARK_BLUE)
                .put('2',ChatColor.DARK_GREEN)
                .put('3',ChatColor.DARK_AQUA)
                .put('4',ChatColor.DARK_RED)
                .put('5',ChatColor.DARK_PURPLE)
                .put('6',ChatColor.GOLD)
                .put('7',ChatColor.GRAY)
                .put('8',ChatColor.DARK_GRAY)
                .put('9',ChatColor.BLUE)
                .put('a',ChatColor.GREEN)
                .put('b',ChatColor.AQUA)
                .put('c',ChatColor.RED)
                .put('d',ChatColor.LIGHT_PURPLE)
                .put('e',ChatColor.YELLOW)
                .put('f',ChatColor.WHITE)
                .put('k',ChatColor.MAGIC)
                .put('l',ChatColor.BOLD)
                .put('m',ChatColor.STRIKETHROUGH)
                .put('n',ChatColor.UNDERLINE)
                .put('o',ChatColor.ITALIC)
                .put('r',ChatColor.RESET)
                .build());

    }

    public static String getProperName(ChatColor color) {
        if (color.name().contains("_")) {
            return StringUtils.capitalize(color.name().split("_")[0]) + " " + StringUtils.capitalize(color.name().split("_")[1]);
        }

        return StringUtils.capitalize(color.name().toLowerCase());
    }
}