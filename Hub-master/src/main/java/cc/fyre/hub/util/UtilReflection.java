package cc.fyre.hub.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public final class UtilReflection {
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        Class[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for (Constructor<?> constructor : clazz.getConstructors()) {
            if (DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
                return constructor;
            }
        }
        throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
    }

    public static Constructor<?> getConstructor(String className, PackageType packageType, Class<?>... parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException {
        return getConstructor(packageType.getClass(className), parameterTypes);
    }

    public static Object instantiateObject(Class<?> clazz, Object... arguments)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return getConstructor(clazz, DataType.getPrimitive(arguments)).newInstance(arguments);
    }

    public static Object instantiateObject(String className, PackageType packageType, Object... arguments)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        return instantiateObject(packageType.getClass(className), arguments);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        Class[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for (Method method : clazz.getMethods()) {
            if ((method.getName().equals(methodName)) && (DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes))) {
                return method;
            }
        }
        throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
    }

    public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes)
            throws NoSuchMethodException, ClassNotFoundException {
        return getMethod(packageType.getClass(className), methodName, parameterTypes);
    }

    public static Object invokeMethod(Object instance, String methodName, Object... arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return getMethod(instance.getClass(), methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
    }

    public static Object invokeMethod(Object instance, Class<?> clazz, String methodName, Object... arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        return getMethod(clazz, methodName, DataType.getPrimitive(arguments)).invoke(instance, arguments);
    }

    public static Object invokeMethod(Object instance, String className, PackageType packageType, String methodName, Object... arguments)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        return invokeMethod(instance, packageType.getClass(className), methodName, arguments);
    }

    public static Field getField(Class<?> clazz, boolean declared, String fieldName)
            throws NoSuchFieldException, SecurityException {
        Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Field getField(String className, PackageType packageType, boolean declared, String fieldName)
            throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        return getField(packageType.getClass(className), declared, fieldName);
    }

    public static Object getValue(Object instance, Class<?> clazz, boolean declared, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return getField(clazz, declared, fieldName).get(instance);
    }

    public static Object getValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        return getValue(instance, packageType.getClass(className), declared, fieldName);
    }

    public static Object getValue(Object instance, boolean declared, String fieldName)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        return getValue(instance, instance.getClass(), declared, fieldName);
    }

    public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        getField(clazz, declared, fieldName).set(instance, value);
    }

    public static void setValue(Object instance, String className, PackageType packageType, boolean declared, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException {
        setValue(instance, packageType.getClass(className), declared, fieldName, value);
    }

    public static void setValue(Object instance, boolean declared, String fieldName, Object value)
            throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        setValue(instance, instance.getClass(), declared, fieldName, value);
    }

    public static enum PackageType {
        MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()), CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()), CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"), CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"), CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"), CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"), CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"), CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"), CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"), CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"), CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"), CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"), CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"), CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"), CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"), CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"), CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"), CRAFTBUKKIT_UPDATER(CRAFTBUKKIT, "updater"), CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

        private final String path;

        private PackageType(String path) {
            this.path = path;
        }

        private PackageType(PackageType parent, String path) {
            this(parent + "." + path);
        }

        public String getPath() {
            return this.path;
        }

        public Class<?> getClass(String className)
                throws ClassNotFoundException {
            return Class.forName(this + "." + className);
        }

        public String toString() {
            return this.path;
        }

        public static String getServerVersion() {
            return Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }
    }

    public static enum DataType {
        BYTE(Byte.TYPE, Byte.class), SHORT(Short.TYPE, Short.class), INTEGER(Integer.TYPE, Integer.class), LONG(Long.TYPE, Long.class), CHARACTER(Character.TYPE, Character.class), FLOAT(Float.TYPE, Float.class), DOUBLE(Double.TYPE, Double.class), BOOLEAN(Boolean.TYPE, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP;
        private final Class<?> primitive;
        private final Class<?> reference;

        static {
            CLASS_MAP = new HashMap();
            for (DataType type : values()) {
                CLASS_MAP.put(type.primitive, type);
                CLASS_MAP.put(type.reference, type);
            }
        }

        private DataType(Class<?> primitive, Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        public Class<?> getPrimitive() {
            return this.primitive;
        }

        public Class<?> getReference() {
            return this.reference;
        }

        public static DataType fromClass(Class<?> clazz) {
            return (DataType) CLASS_MAP.get(clazz);
        }

        public static Class<?> getPrimitive(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getPrimitive();
        }

        public static Class<?> getReference(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getReference();
        }

        public static Class<?>[] getPrimitive(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class[] types = new Class[length];
            for (int index = 0; index < length; index++) {
                types[index] = getPrimitive(classes[index]);
            }
            return types;
        }

        public static Class<?>[] getReference(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class[] types = new Class[length];
            for (int index = 0; index < length; index++) {
                types[index] = getReference(classes[index]);
            }
            return types;
        }

        public static Class<?>[] getPrimitive(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class[] types = new Class[length];
            for (int index = 0; index < length; index++) {
                types[index] = getPrimitive(objects[index].getClass());
            }
            return types;
        }

        public static Class<?>[] getReference(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class[] types = new Class[length];
            for (int index = 0; index < length; index++) {
                types[index] = getReference(objects[index].getClass());
            }
            return types;
        }

        public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
            if ((primary == null) || (secondary == null) || (primary.length != secondary.length)) {
                return false;
            }
            for (int index = 0; index < primary.length; index++) {
                Class<?> primaryClass = primary[index];
                Class<?> secondaryClass = secondary[index];
                if ((!primaryClass.equals(secondaryClass)) && (!primaryClass.isAssignableFrom(secondaryClass))) {
                    return false;
                }
            }
            return true;
        }
    }

    public static enum PacketType {
        HANDSHAKING_IN_SET_PROTOCOL("PacketHandshakingInSetProtocol"), LOGIN_IN_ENCRYPTION_BEGIN("PacketLoginInEncryptionBegin"), LOGIN_IN_START("PacketLoginInStart"), LOGIN_OUT_DISCONNECT("PacketLoginOutDisconnect"), LOGIN_OUT_ENCRYPTION_BEGIN("PacketLoginOutEncryptionBegin"), LOGIN_OUT_SUCCESS("PacketLoginOutSuccess"), PLAY_IN_ABILITIES("PacketPlayInAbilities"), PLAY_IN_ARM_ANIMATION("PacketPlayInArmAnimation"), PLAY_IN_BLOCK_DIG("PacketPlayInBlockDig"), PLAY_IN_BLOCK_PLACE("PacketPlayInBlockPlace"), PLAY_IN_CHAT("PacketPlayInChat"), PLAY_IN_CLIENT_COMMAND("PacketPlayInClientCommand"), PLAY_IN_CLOSE_WINDOW("PacketPlayInCloseWindow"), PLAY_IN_CUSTOM_PAYLOAD("PacketPlayInCustomPayload"), PLAY_IN_ENCHANT_ITEM("PacketPlayInEnchantItem"), PLAY_IN_ENTITY_ACTION("PacketPlayInEntityAction"), PLAY_IN_FLYING("PacketPlayInFlying"), PLAY_IN_HELD_ITEM_SLOT("PacketPlayInHeldItemSlot"), PLAY_IN_KEEP_ALIVE("PacketPlayInKeepAlive"), PLAY_IN_LOOK("PacketPlayInLook"), PLAY_IN_POSITION("PacketPlayInPosition"), PLAY_IN_POSITION_LOOK("PacketPlayInPositionLook"), PLAY_IN_SET_CREATIVE_SLOT("PacketPlayInSetCreativeSlot "), PLAY_IN_SETTINGS("PacketPlayInSettings"), PLAY_IN_STEER_VEHICLE("PacketPlayInSteerVehicle"), PLAY_IN_TAB_COMPLETE("PacketPlayInTabComplete"), PLAY_IN_TRANSACTION("PacketPlayInTransaction"), PLAY_IN_UPDATE_SIGN("PacketPlayInUpdateSign"), PLAY_IN_USE_ENTITY("PacketPlayInUseEntity"), PLAY_IN_WINDOW_CLICK("PacketPlayInWindowClick"), PLAY_OUT_ABILITIES("PacketPlayOutAbilities"), PLAY_OUT_ANIMATION("PacketPlayOutAnimation"), PLAY_OUT_ATTACH_ENTITY("PacketPlayOutAttachEntity"), PLAY_OUT_BED("PacketPlayOutBed"), PLAY_OUT_BLOCK_ACTION("PacketPlayOutBlockAction"), PLAY_OUT_BLOCK_BREAK_ANIMATION("PacketPlayOutBlockBreakAnimation"), PLAY_OUT_BLOCK_CHANGE("PacketPlayOutBlockChange"), PLAY_OUT_CHAT("PacketPlayOutChat"), PLAY_OUT_CLOSE_WINDOW("PacketPlayOutCloseWindow"), PLAY_OUT_COLLECT("PacketPlayOutCollect"), PLAY_OUT_CRAFT_PROGRESS_BAR("PacketPlayOutCraftProgressBar"), PLAY_OUT_CUSTOM_PAYLOAD("PacketPlayOutCustomPayload"), PLAY_OUT_ENTITY("PacketPlayOutEntity"), PLAY_OUT_ENTITY_DESTROY("PacketPlayOutEntityDestroy"), PLAY_OUT_ENTITY_EFFECT("PacketPlayOutEntityEffect"), PLAY_OUT_ENTITY_EQUIPMENT("PacketPlayOutEntityEquipment"), PLAY_OUT_ENTITY_HEAD_ROTATION("PacketPlayOutEntityHeadRotation"), PLAY_OUT_ENTITY_LOOK("PacketPlayOutEntityLook"), PLAY_OUT_ENTITY_METADATA("PacketPlayOutEntityMetadata"), PLAY_OUT_ENTITY_STATUS("PacketPlayOutEntityStatus"), PLAY_OUT_ENTITY_TELEPORT("PacketPlayOutEntityTeleport"), PLAY_OUT_ENTITY_VELOCITY("PacketPlayOutEntityVelocity"), PLAY_OUT_EXPERIENCE("PacketPlayOutExperience"), PLAY_OUT_EXPLOSION("PacketPlayOutExplosion"), PLAY_OUT_GAME_STATE_CHANGE("PacketPlayOutGameStateChange"), PLAY_OUT_HELD_ITEM_SLOT("PacketPlayOutHeldItemSlot"), PLAY_OUT_KEEP_ALIVE("PacketPlayOutKeepAlive"), PLAY_OUT_KICK_DISCONNECT("PacketPlayOutKickDisconnect"), PLAY_OUT_LOGIN("PacketPlayOutLogin"), PLAY_OUT_MAP("PacketPlayOutMap"), PLAY_OUT_MAP_CHUNK("PacketPlayOutMapChunk"), PLAY_OUT_MAP_CHUNK_BULK("PacketPlayOutMapChunkBulk"), PLAY_OUT_MULTI_BLOCK_CHANGE("PacketPlayOutMultiBlockChange"), PLAY_OUT_NAMED_ENTITY_SPAWN("PacketPlayOutNamedEntitySpawn"), PLAY_OUT_NAMED_SOUND_EFFECT("PacketPlayOutNamedSoundEffect"), PLAY_OUT_OPEN_SIGN_EDITOR("PacketPlayOutOpenSignEditor"), PLAY_OUT_OPEN_WINDOW("PacketPlayOutOpenWindow"), PLAY_OUT_PLAYER_INFO("PacketPlayOutPlayerInfo"), PLAY_OUT_POSITION("PacketPlayOutPosition"), PLAY_OUT_REL_ENTITY_MOVE("PacketPlayOutRelEntityMove"), PLAY_OUT_REL_ENTITY_MOVE_LOOK("PacketPlayOutRelEntityMoveLook"), PLAY_OUT_REMOVE_ENTITY_EFFECT("PacketPlayOutRemoveEntityEffect"), PLAY_OUT_RESPAWN("PacketPlayOutRespawn"), PLAY_OUT_SCOREBOARD_DISPLAY_OBJECTIVE("PacketPlayOutScoreboardDisplayObjective"), PLAY_OUT_SCOREBOARD_OBJECTIVE("PacketPlayOutScoreboardObjective"), PLAY_OUT_SCOREBOARD_SCORE("PacketPlayOutScoreboardScore"), PLAY_OUT_SCOREBOARD_TEAM("PacketPlayOutScoreboardTeam"), PLAY_OUT_SET_SLOT("PacketPlayOutSetSlot"), PLAY_OUT_SPAWN_ENTITY("PacketPlayOutSpawnEntity"), PLAY_OUT_SPAWN_ENTITY_EXPERIENCE_ORB("PacketPlayOutSpawnEntityExperienceOrb"), PLAY_OUT_SPAWN_ENTITY_LIVING("PacketPlayOutSpawnEntityLiving"), PLAY_OUT_SPAWN_ENTITY_PAINTING("PacketPlayOutSpawnEntityPainting"), PLAY_OUT_SPAWN_ENTITY_WEATHER("PacketPlayOutSpawnEntityWeather"), PLAY_OUT_SPAWN_POSITION("PacketPlayOutSpawnPosition"), PLAY_OUT_STATISTIC("PacketPlayOutStatistic"), PLAY_OUT_TAB_COMPLETE("PacketPlayOutTabComplete"), PLAY_OUT_TILE_ENTITY_DATA("PacketPlayOutTileEntityData"), PLAY_OUT_TRANSACTION("PacketPlayOutTransaction"), PLAY_OUT_UPDATE_ATTRIBUTES("PacketPlayOutUpdateAttributes"), PLAY_OUT_UPDATE_HEALTH("PacketPlayOutUpdateHealth"), PLAY_OUT_UPDATE_SIGN("PacketPlayOutUpdateSign"), PLAY_OUT_UPDATE_TIME("PacketPlayOutUpdateTime"), PLAY_OUT_WINDOW_ITEMS("PacketPlayOutWindowItems"), PLAY_OUT_WORLD_EVENT("PacketPlayOutWorldEvent"), PLAY_OUT_WORLD_PARTICLES("PacketPlayOutWorldParticles"), STATUS_IN_PING("PacketStatusInPing"), STATUS_IN_START("PacketStatusInStart"), STATUS_OUT_PONG("PacketStatusOutPong"), STATUS_OUT_SERVER_INFO("PacketStatusOutServerInfo");

        private static final Map<String, PacketType> NAME_MAP;
        private final String name;
        private Class<?> packet;

        static {
            NAME_MAP = new HashMap();
            for (PacketType type : values()) {
                NAME_MAP.put(type.name, type);
            }
        }

        private PacketType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Class<?> getPacket()
                throws ClassNotFoundException {
            return this.packet == null ? (this.packet = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass(this.name)) : this.packet;
        }
    }
}