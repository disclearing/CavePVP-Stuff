package org.bukkit.craftbukkit.util;

public class LongHash {
    public static long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }

    public static int msw(long l) {
        return (int) (l >> 32);
    }

    public static int lsw(long l) {
        return (int) (l & -1L) + Integer.MIN_VALUE; // Spigot - remove redundant & // Spigot Update - 20140921a
    }
}
