package cc.fyre.neutron.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.time.DurationFormatUtils;

@UtilityClass
public class FormatUtil {


    private static final String HOUR_FORMAT = "%02d:%02d:%02d";
    private static final String MINUTE_FORMAT = "%02d:%02d";
    private static final String TIME_UNTIL_PATTERN = "d'd'H'h'mm'm'";

    public static String millisToTimer(long millis) {

        final long seconds = millis / 1000L;

        if (seconds > 3600L) {
            return String.format(HOUR_FORMAT, seconds / 3600L, seconds % 3600L / 60L, seconds % 60L);
        }

        return String.format(MINUTE_FORMAT, seconds / 60L, seconds % 60L);
    }

    public static String millisToRoundedTime(long millis,boolean formatBelow24) {

        if (millis > (86400 * 1000)) {
            return DurationFormatUtils.formatDuration(millis,TIME_UNTIL_PATTERN);
        }

        return formatBelow24 ? millisToTimer(millis):DurationFormatUtils.formatDuration(millis,TIME_UNTIL_PATTERN);
    }

}
