package cc.fyre.hub.util;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public enum UtilParticle {
    HUGE_EXPLOSION(






            "hugeexplosion"),  LARGE_EXPLODE(






        "largeexplode"),  FIREWORKS_SPARK(






        "fireworksSpark"),  BUBBLE(






        "bubble", true),  SUSPEND(






        "suspend", true),  DEPTH_SUSPEND(






        "depthSuspend"),  TOWN_AURA(






        "townaura"),  CRIT(






        "crit"),  MAGIC_CRIT(






        "magicCrit"),  SMOKE(






        "smoke"),  MOB_SPELL(






        "mobSpell"),  MOB_SPELL_AMBIENT(






        "mobSpellAmbient"),  SPELL(






        "spell"),  INSTANT_SPELL(






        "instantSpell"),  WITCH_MAGIC(






        "witchMagic"),  NOTE(






        "note"),  PORTAL(






        "portal"),  ENCHANTMENT_TABLE(






        "enchantmenttable"),  EXPLODE(






        "explode"),  FLAME(






        "flame"),  LAVA(






        "lava"),  FOOTSTEP(






        "footstep"),  SPLASH(






        "splash"),  WAKE(






        "wake"),  LARGE_SMOKE(






        "largesmoke"),  CLOUD(






        "cloud"),  RED_DUST(






        "reddust"),  SNOWBALL_POOF(






        "snowballpoof"),  DRIP_WATER(






        "dripWater"),  DRIP_LAVA(






        "dripLava"),  SNOW_SHOVEL(






        "snowshovel"),  SLIME(






        "slime"),  HEART(






        "heart"),  ANGRY_VILLAGER(






        "angryVillager"),  HAPPY_VILLAGER(






        "happyVillager");

    private static final Map<String, UtilParticle> NAME_MAP;
    private final String name;
    private final boolean requiresWater;

    static
    {
        NAME_MAP = new HashMap();
        for (UtilParticle effect : values()) {
            NAME_MAP.put(effect.name, effect);
        }
    }

    private UtilParticle(String name, boolean requiresWater)
    {
        this.name = name;
        this.requiresWater = requiresWater;
    }

    private UtilParticle(String name)
    {
        this(name, false);
    }

    public String getName()
    {
        return this.name;
    }

    public boolean getRequiresWater()
    {
        return this.requiresWater;
    }

    public static UtilParticle fromName(String name)
    {
        for (Map.Entry<String, UtilParticle> entry : NAME_MAP.entrySet()) {
            if (((String)entry.getKey()).equalsIgnoreCase(name)) {
                return (UtilParticle)entry.getValue();
            }
        }
        return null;
    }

    private static boolean isWater(Location location)
    {
        Material material = location.getBlock().getType();
        return (material == Material.WATER) || (material == Material.STATIONARY_WATER);
    }

    private static boolean isBlock(int id)
    {
        Material material = Material.getMaterial(id);
        return (material != null) && (material.isBlock());
    }

    public void display(Location center, float offsetY, float offsetZ, float speed, int amount, float offsetX, double range)
            throws IllegalArgumentException
    {
        if ((this.requiresWater) && (!isWater(center))) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new UtilParticlePacket(this.name, offsetX, offsetY, offsetZ, speed, amount).sendTo(center, range);
    }

    public void display(float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players)
            throws IllegalArgumentException
    {
        if ((this.requiresWater) && (!isWater(center))) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new UtilParticlePacket(this.name, offsetX, offsetY, offsetZ, speed, amount).sendTo(center, players);
    }

    public static void displayIconCrack(int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range)
    {
        new UtilParticlePacket("iconcrack_" + id + "_" + data, offsetX, offsetY, offsetZ, speed, amount).sendTo(center, range);
    }

    public static void displayIconCrack(int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players)
    {
        new UtilParticlePacket("iconcrack_" + id + "_" + data, offsetX, offsetY, offsetZ, speed, amount).sendTo(center, players);
    }

    public static void displayBlockCrack(int id, byte data, float offsetX, float offsetY, float offsetZ, int amount, Location center, double range)
            throws IllegalArgumentException
    {
        if (!isBlock(id)) {
            throw new IllegalArgumentException("Invalid block id");
        }
        new UtilParticlePacket("blockcrack_" + id + "_" + data, offsetX, offsetY, offsetZ, 0.0F, amount).sendTo(center, range);
    }

    public static void displayBlockCrack(int id, byte data, float offsetX, float offsetY, float offsetZ, int amount, Location center, List<Player> players)
            throws IllegalArgumentException
    {
        if (!isBlock(id)) {
            throw new IllegalArgumentException("Invalid block id");
        }
        new UtilParticlePacket("blockcrack_" + id + "_" + data, offsetX, offsetY, offsetZ, 0.0F, amount).sendTo(center, players);
    }

    public static void displayBlockDust(int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range)
            throws IllegalArgumentException
    {
        if (!isBlock(id)) {
            throw new IllegalArgumentException("Invalid block id");
        }
        new UtilParticlePacket("blockdust_" + id + "_" + data, offsetX, offsetY, offsetZ, speed, amount).sendTo(center, range);
    }

    public static void displayBlockDust(int id, byte data, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, List<Player> players)
            throws IllegalArgumentException
    {
        if (!isBlock(id)) {
            throw new IllegalArgumentException("Invalid block id");
        }
        new UtilParticlePacket("blockdust_" + id + "_" + data, offsetX, offsetY, offsetZ, speed, amount).sendTo(center, players);
    }

    public static final class UtilParticlePacket
    {
        private static Constructor<?> packetConstructor;
        private static Method getHandle;
        private static Field playerConnection;
        private static Method sendPacket;
        private static boolean initialized;
        private final String name;
        private final float offsetX;
        private final float offsetY;
        private final float offsetZ;
        private final float speed;
        private final int amount;
        private Object packet;

        public UtilParticlePacket(String name, float offsetX, float offsetY, float offsetZ, float speed, int amount)
                throws IllegalArgumentException
        {
            initialize();
            if (speed < 0.0F) {
                throw new IllegalArgumentException("The speed is lower than 0");
            }
            if (amount < 1) {
                throw new IllegalArgumentException("The amount is lower than 1");
            }
            this.name = name;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.speed = speed;
            this.amount = amount;
        }

        public static void initialize()
                throws UtilParticle.UtilParticlePacket.VersionIncompatibleException
        {
            if (initialized) {
                return;
            }
            try
            {
                int version = Integer.parseInt(Character.toString(UtilReflection.PackageType.getServerVersion().charAt(3)));
                Class<?> packetClass = UtilReflection.PackageType.MINECRAFT_SERVER.getClass(version < 7 ? "Packet63WorldParticles" : UtilReflection.PacketType.PLAY_OUT_WORLD_PARTICLES.getName());
                packetConstructor = UtilReflection.getConstructor(packetClass, new Class[0]);
                getHandle = UtilReflection.getMethod("CraftPlayer", UtilReflection.PackageType.CRAFTBUKKIT_ENTITY, "getHandle", new Class[0]);
                playerConnection = UtilReflection.getField("EntityPlayer", UtilReflection.PackageType.MINECRAFT_SERVER, false, "playerConnection");
                sendPacket = UtilReflection.getMethod(playerConnection.getType(), "sendPacket", new Class[] { UtilReflection.PackageType.MINECRAFT_SERVER.getClass("Packet") });
            }
            catch (Exception exception)
            {
                throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
            }
            initialized = true;
        }

        public static boolean isInitialized()
        {
            return initialized;
        }

        public void sendTo(Location center, Player player)
                throws UtilParticle.UtilParticlePacket.PacketInstantiationException, UtilParticle.UtilParticlePacket.PacketSendingException
        {
            if (this.packet == null) {
                try
                {
                    this.packet = packetConstructor.newInstance(new Object[0]);
                    UtilReflection.setValue(this.packet, true, "a", this.name);
                    UtilReflection.setValue(this.packet, true, "b", Float.valueOf((float)center.getX()));
                    UtilReflection.setValue(this.packet, true, "c", Float.valueOf((float)center.getY()));
                    UtilReflection.setValue(this.packet, true, "d", Float.valueOf((float)center.getZ()));
                    UtilReflection.setValue(this.packet, true, "e", Float.valueOf(this.offsetX));
                    UtilReflection.setValue(this.packet, true, "f", Float.valueOf(this.offsetY));
                    UtilReflection.setValue(this.packet, true, "g", Float.valueOf(this.offsetZ));
                    UtilReflection.setValue(this.packet, true, "h", Float.valueOf(this.speed));
                    UtilReflection.setValue(this.packet, true, "i", Integer.valueOf(this.amount));
                }
                catch (Exception exception)
                {
                    throw new PacketInstantiationException("Packet instantiation failed", exception);
                }
            }
            try
            {
                sendPacket.invoke(playerConnection.get(getHandle.invoke(player, new Object[0])), new Object[] { this.packet });
            }
            catch (Exception exception)
            {
                throw new PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", exception);
            }
        }

        public void sendTo(Location center, List<Player> players)
                throws IllegalArgumentException
        {
            if (players.isEmpty()) {
                throw new IllegalArgumentException("The player list is empty");
            }
            for (Player player : players) {
                sendTo(center, player);
            }
        }

        public void sendTo(Location center, double range)
                throws IllegalArgumentException
        {
            if (range < 1.0D) {
                throw new IllegalArgumentException("The range is lower than 1");
            }
            String worldName = center.getWorld().getName();
            double squared = range * range;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if ((player.getWorld().getName().equals(worldName)) && (player.getLocation().distanceSquared(center) <= squared)) {
                    sendTo(center, player);
                }
            }
        }

        private static final class VersionIncompatibleException
                extends RuntimeException
        {
            private static final long serialVersionUID = 3203085387160737484L;

            public VersionIncompatibleException(String message, Throwable cause)
            {
                super();
            }
        }

        private static final class PacketInstantiationException
                extends RuntimeException
        {
            private static final long serialVersionUID = 3203085387160737484L;

            public PacketInstantiationException(String message, Throwable cause)
            {
                super();
            }
        }

        private static final class PacketSendingException
                extends RuntimeException
        {
            private static final long serialVersionUID = 3203085387160737484L;

            public PacketSendingException(String message, Throwable cause)
            {
                super();
            }
        }
    }
}