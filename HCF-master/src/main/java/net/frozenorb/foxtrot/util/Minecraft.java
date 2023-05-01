package net.frozenorb.foxtrot.util;


import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.lang.reflect.InvocationTargetException;

public class Minecraft
{
    public static final Version VERSION;

    public static String getVersion() {
        return Minecraft.VERSION.name() + ".";
    }

    public static Object getHandle(final Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getHandle", (Class<?>[])new Class[0])).invoke(object, new Object[0]);
    }

    public static Entity getBukkitEntity(final Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return (Entity)AccessUtil.setAccessible(object.getClass().getDeclaredMethod("getBukkitEntity", (Class<?>[])new Class[0])).invoke(object, new Object[0]);
    }

    public static Object getHandleSilent(final Object object) {
        try {
            return getHandle(object);
        }
        catch (Exception e) {
            return null;
        }
    }

    static {
        VERSION = Version.getVersion();
        System.out.println("[ReflectionHelper] Version is " + Minecraft.VERSION);
    }

    public enum Version
    {
        UNKNOWN(-1) {
            @Override
            public boolean matchesPackageName(final String packageName) {
                return false;
            }
        },
        v1_7_R1(10701),
        v1_7_R2(10702),
        v1_7_R3(10703),
        v1_7_R4(10704),
        v1_8_R1(10801),
        v1_8_R2(10802),
        v1_8_R3(10803),
        v1_8_R4(10804),
        v1_9_R1(109001);

        private int version;

        private Version(final int version) {
            this.version = version;
        }

        public int version() {
            return this.version;
        }

        public boolean olderThan(final Version version) {
            return this.version() < version.version();
        }

        public boolean newerThan(final Version version) {
            return this.version() >= version.version();
        }

        public boolean inRange(final Version oldVersion, final Version newVersion) {
            return this.newerThan(oldVersion) && this.olderThan(newVersion);
        }

        public boolean matchesPackageName(final String packageName) {
            return packageName.toLowerCase().contains(this.name().toLowerCase());
        }

        public static Version getVersion() {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            final String versionPackage = name.substring(name.lastIndexOf(46) + 1) + ".";
            for (final Version version : values()) {
                if (version.matchesPackageName(versionPackage)) {
                    return version;
                }
            }
            System.err.println("[ReflectionHelper] Failed to find version enum for '" + name + "'/'" + versionPackage + "'");
            return Version.UNKNOWN;
        }

        @Override
        public String toString() {
            return this.name() + " (" + this.version() + ")";
        }
    }
}
