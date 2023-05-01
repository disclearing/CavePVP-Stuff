package net.frozenorb.foxtrot.util;

import org.apache.commons.lang.Validate;

import java.util.Random;

public class RandomUtil {

    public static final Random RANDOM = new Random();

    public static int getRandInt(int min, int max) throws IllegalArgumentException {
        if(min == max)
            return min;

        Validate.isTrue(max > min, "Max can't be smaller than min!");
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static double getRandDouble(double min, double max) throws IllegalArgumentException {
        if(min == max)
            return min;

        Validate.isTrue(max > min, "Max can't be smaller than min!");
        return RANDOM.nextDouble() * (max - min) + min;
    }

    public static boolean getChance(double chance) {
        return chance >= 100.0D || chance >= getRandDouble(0.0D, 100.0D);
    }
}
