package net.frozenorb.foxtrot.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AccessUtil {
    public static Field setAccessible(final Field f) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(f, f.getModifiers() & 0xFFFFFFEF);
        return f;
    }

    public static Method setAccessible(final Method m) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        m.setAccessible(true);
        return m;
    }

    public static Constructor setAccessible(final Constructor c) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        c.setAccessible(true);
        return c;
    }
}
