package net.frozenorb.foxtrot.util;

import org.bukkit.ChatColor;

public class ProgressBarUtil {
    public static String getProgressBar(int current, int max, int totalBars) {
        return getProgressBar(current, max, totalBars, "|", ChatColor.GREEN, ChatColor.RED);
    }

    public static String getProgressBar(int current, int max, int totalBars, String bar, ChatColor progressColor, ChatColor leftColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);

        StringBuilder sb = new StringBuilder();
        sb.append(progressColor);
        for(int i = 0; i < progressBars; i++)
            sb.append(bar);

        sb.append(leftColor);
        for(int i = 0; i < leftOver; i++)
            sb.append(bar);

        return sb.toString();
    }

}
