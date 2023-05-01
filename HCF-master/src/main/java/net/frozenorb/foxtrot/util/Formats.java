package net.frozenorb.foxtrot.util;

import org.bukkit.ChatColor;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Formats {

    public static String formatNumber(Number number) {
        return NumberFormat.getInstance().format(number);
//        if (number.getClass() == double.class || number.getClass() == Double.class) {
//            return
//        }
    }

    public static String formatExperience(int exp) {
        return CC.GOLD + "✪" + CC.YELLOW + formatNumber(exp);
    }

    public static List<String> renderLines(String prefix, String text) {
        List<String> lines = new ArrayList<>();

        if (text.contains("%line%")) {
            for (String piece : text.split("%line%")) {
                lines.addAll(renderLines(prefix, piece));
            }

            return lines;
        }

        if (text.length() < 32) {
            lines.add(prefix + ChatColor.translateAlternateColorCodes('&', text));
            return lines;
        }

        StringBuilder line = new StringBuilder(prefix);
        for (String piece : text.split(" ")) {
            if (ChatColor.stripColor(line + " " + piece).length() > 32) {
                lines.add(ChatColor.translateAlternateColorCodes('&', line.toString()));
                line = new StringBuilder(prefix).append(piece);
            } else {
                if (line.length() > prefix.length()) {
                    line.append(" ");
                }

                line.append(piece);
            }
        }

        if (line.length() > 0) {
            lines.add(ChatColor.translateAlternateColorCodes('&', line.toString()));
        }

        return lines;
    }

}
