package cc.fyre.neutron;

import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.util.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NeutronConstants {

    public static final String CONSOLE_NAME = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Console";

    public static final String MONITOR_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "Monitor" + ChatColor.DARK_GRAY + "]";

    public static final String STAFF_PERMISSION = "neutron.staff";
    public static final String ADMIN_PERMISSION = "neutron.admin";
    public static final String MANAGER_PERMISSION = "neutron.manager";
    public static int ALT_LIMIT_MAX = 2;

    public static final String SILENT_PREFIX = ChatColor.GRAY + "[Silent]";
    public static final String SB_BAR = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",20);
    public static final String MENU_BAR = ChatColor.STRIKETHROUGH + StringUtils.repeat("-",18);
    public static final String CHAT_BAR = ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",48);

    public static final String RANKS_COLLECTION = "ranks";
    public static final String PREFIX_COLLECTION = "prefixes";
    public static final String PROFILE_COLLECTION = "profiles";
    public static final String ROLLBACK_LOG_COLLECTION = "rollback-logs";

    public static DurationWrapper findDurationWrapper(String source) {
        try {

            final int toReturn = TimeUtils.parseTime(source);

            if ((toReturn * 1000L) <= 0) {
                return new DurationWrapper(source, (long) Integer.MAX_VALUE);
            }

            return new DurationWrapper(source,toReturn * 1000L);
        } catch (NullPointerException | IllegalArgumentException ex) {
            return new DurationWrapper(source, (long) Integer.MAX_VALUE);
        }
    }

    public static String formatChatDisplay(Player player,String message) {
        String name = player.getName();

        if (player.getName().equalsIgnoreCase("BlondeLoverJames")) {
            name = "LilManJames";
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        final String rankPrefix = profile.getCustomPrefix() != null ? ChatColor.translate(profile.getCustomPrefix()) : profile.getHolidayType() != null ? profile.getHolidayType().getPrefix() + " " : ChatColor.stripColor(profile.getActiveGrant().getRank().getPrefix()).equals("") ?  "" : profile.getActiveGrant().getRank().getPrefix() + " ";
        final Prefix activeTag = profile.getActivePrefix();
        String tag = (profile.getActivePrefix() == null ? "" : profile.getActivePrefix().getDisplay() + " ");

        if (activeTag != null) {
            String display = activeTag.getDisplay();
            String noColor = ChatColor.stripColor(activeTag.getDisplay());

            if (display.endsWith(ChatColor.DARK_GRAY + "]") && display.startsWith(ChatColor.DARK_GRAY + "[") && noColor.length() == 3) {
                tag = display.replace(ChatColor.DARK_GRAY + "[", "").replace(ChatColor.DARK_GRAY + "]", "");
            } else if (noColor.length() == 1) {
                tag = display;
            }
        }

        return rankPrefix + tag + ChatColor.WHITE + ChatColor.getLastColors(rankPrefix) + name + ChatColor.GRAY + ": " + ChatColor.WHITE
                + profile.getChatColor() + (player.isOp() ? ChatColor.translateAlternateColorCodes('&',message):message);
    }

    public static String formatChatDisplay(Player player,String message, ChatColor chatColor) {
        String name = player.getName();

        if (player.getName().equalsIgnoreCase("BlondeLoverJames")) {
            name = "LilManJames";
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        final String rankPrefix = profile.getCustomPrefix() != null ? ChatColor.translate(profile.getCustomPrefix()) : profile.getHolidayType() != null ? profile.getHolidayType().getPrefix() + " " : ChatColor.stripColor(profile.getActiveGrant().getRank().getPrefix()).equals("") ?  "" : profile.getActiveGrant().getRank().getPrefix() + " ";
        final Prefix activeTag = profile.getActivePrefix();
        String tag = (profile.getActivePrefix() == null ? "" : profile.getActivePrefix().getDisplay() + " ");

        if (activeTag != null) {
            String display = activeTag.getDisplay();
            String noColor = ChatColor.stripColor(activeTag.getDisplay());

            if (display.endsWith(ChatColor.DARK_GRAY + "]") && display.startsWith(ChatColor.DARK_GRAY + "[") && noColor.length() == 3) {
                tag = display.replace(ChatColor.DARK_GRAY + "[", "").replace(ChatColor.DARK_GRAY + "]", "");
            } else if (noColor.length() == 1) {
                tag = display;
            }
        }

        return rankPrefix + tag + ChatColor.WHITE + ChatColor.getLastColors(rankPrefix) + name + ChatColor.GRAY + ": " + ChatColor.WHITE
                + chatColor + (player.isOp() ? ChatColor.translateAlternateColorCodes('&',message):message);
    }

}
