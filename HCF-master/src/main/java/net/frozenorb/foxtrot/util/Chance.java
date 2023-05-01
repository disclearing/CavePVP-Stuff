package net.frozenorb.foxtrot.util;

import java.util.concurrent.ThreadLocalRandom;

public class Chance {

    public static boolean percent(double percent) {
        return percent > 0.0 && ThreadLocalRandom.current().nextDouble(100.0) >= 100.0 - percent;
    }

}
