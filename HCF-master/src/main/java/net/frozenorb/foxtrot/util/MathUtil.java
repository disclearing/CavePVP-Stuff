package net.frozenorb.foxtrot.util;

public class MathUtil {

    public static int scaleBetween(int value, int minAllowed, int maxAllowed, int min, int max) {
        return (maxAllowed - minAllowed) * (value - min) / (max - min) + minAllowed;
    }

    public static long scaleBetween(long value, long minAllowed, long maxAllowed, long min, long max) {
        return (maxAllowed - minAllowed) * (value - min) / (max - min) + minAllowed;
    }

    public static float scaleBetween(float value, float minAllowed, float maxAllowed, float min, float max) {
        return (maxAllowed - minAllowed) * (value - min) / (max - min) + minAllowed;
    }

    public static double scaleBetween(double value, double minAllowed, double maxAllowed, double min, double max) {
        return (maxAllowed - minAllowed) * (value - min) / (max - min) + minAllowed;
    }
}
