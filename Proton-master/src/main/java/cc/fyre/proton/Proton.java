package cc.fyre.proton;

import cc.fyre.proton.autoreboot.AutoRebootHandler;
import cc.fyre.proton.border.BorderHandler;
import cc.fyre.proton.bossbar.BossBarHandler;
import cc.fyre.proton.combatlogger.CombatLoggerHandler;
import cc.fyre.proton.command.CommandHandler;
import cc.fyre.proton.event.HalfHourEvent;
import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.hologram.HologramHandler;
import cc.fyre.proton.nametag.NameTagHandler;
import cc.fyre.proton.pidgin.PidginHandler;
import cc.fyre.proton.redis.RedisCommand;
import cc.fyre.proton.scoreboard.ScoreboardHandler;
import cc.fyre.proton.serialization.*;
import cc.fyre.proton.tab.TabHandler;
import cc.fyre.proton.util.EnchantmentGlow;
import cc.fyre.proton.util.ItemUtils;
import cc.fyre.proton.util.TPSUtils;
import cc.fyre.proton.uuid.UUIDCache;
import cc.fyre.proton.visibility.VisibilityHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Proton extends JavaPlugin {

    @Getter
    private static Proton instance;

    @Getter
    private JedisPool localJedisPool;
    @Getter
    private JedisPool backboneJedisPool;
    @Getter
    private long backboneLastError;
    @Getter
    private long redisLastError;

    @Getter
    private CommandHandler commandHandler;
    @Getter
    private HologramHandler hologramHandler;

    @Getter
    private TabHandler tabHandler;
    @Getter
    private NameTagHandler nameTagHandler;
    @Getter
    private ScoreboardHandler scoreboardHandler;

    //    @Getter private EconomyHandler economyHandler;
    @Getter
    private AutoRebootHandler autoRebootHandler;

    //    @Getter private DeathMessageHandler deathMessageHandler;
    @Getter
    private CombatLoggerHandler combatLoggerHandler;

    @Getter
    private BorderHandler borderHandler;
    @Getter
    private BossBarHandler bossBarHandler;
    @Getter
    private VisibilityHandler visibilityHandler;

    @Getter
    private PidginHandler pidginHandler;
    @Getter
    private UUIDCache uuidCache;

    public static final Gson GSON = new com.google.gson.GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public static final Gson PLAIN_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeAdapter(BlockVector.class, new BlockVectorAdapter())
            .serializeNulls()
            .create();

    @Override
    public void onEnable() {

        instance = this;

        this.saveDefaultConfig();

        try {
            getLogger().info("Connecting to local redis");
            this.localJedisPool = new JedisPool(
                    new JedisPoolConfig(),
                    this.getConfig().getString("Redis.Host"),
                    this.getConfig().getInt("Redis.Port", 6379),
                    20000,
                    this.getConfig().getString("Redis.Pass", null),
                    this.getConfig().getInt("Redis.DbId", 5)
            );
            getLogger().info("Connected to local redis");

        } catch (Exception ex) {
            this.localJedisPool = null;

            System.out.println("*********************************************");
            System.out.println("               REDIS");
            System.out.println("-> FAILED TO CONNECT TO LOCAL POOL");
            System.out.println("-> INSTANCE: " + this.getConfig().getString("Redis.Host"));
            System.out.println("*********************************************");
        }
        getLogger().info("Connecting to backbone redis");

        try {
            this.backboneJedisPool = new JedisPool(
                    new JedisPoolConfig(),
                    this.getConfig().getString("Backbone.Host"),
                    this.getConfig().getInt("Backbone.Port", 6379),
                    20000,
                    this.getConfig().getString("Backbone.Pass", null),
                    this.getConfig().getInt("Backbone.DbId", 0)
            );
            getLogger().info("Connected to local redis");

        } catch (Exception ex) {
            this.backboneJedisPool = null;

            System.out.println("*********************************************");
            System.out.println("               REDIS");
            System.out.println("-> FAILED TO CONNECT TO BACKBONE POOL");
            System.out.println("-> INSTANCE: " + this.getConfig().getString("Redis.Host"));
            System.out.println("*********************************************");
        }

        this.commandHandler = new CommandHandler();
        this.hologramHandler = new HologramHandler();

        this.tabHandler = new TabHandler();
        this.nameTagHandler = new NameTagHandler();
        this.scoreboardHandler = new ScoreboardHandler();

//        this.economyHandler = new EconomyHandler();
        this.autoRebootHandler = new AutoRebootHandler();

//        this.deathMessageHandler = new DeathMessageHandler();
        this.combatLoggerHandler = new CombatLoggerHandler();

        this.borderHandler = new BorderHandler();
        this.bossBarHandler = new BossBarHandler();
        this.visibilityHandler = new VisibilityHandler();

        this.pidginHandler = new PidginHandler("pidgin", this.backboneJedisPool);

        this.uuidCache = new UUIDCache();

        ItemUtils.load();

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSUtils(), 1L, 1L);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.setupHourEvents();

    }

    public void onDisable() {
//        this.economyHandler.save();
        this.hologramHandler.save();
    }

    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {

        Jedis jedis = this.localJedisPool.getResource();

        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();

            this.redisLastError = System.currentTimeMillis();

            if (jedis != null) {
                this.localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {

            if (jedis != null) {
                this.localJedisPool.returnResource(jedis);
            }

        }

        return result;
    }

    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {

        Jedis jedis = this.backboneJedisPool.getResource();

        T result = null;

        try {
            result = redisCommand.execute(jedis);
        } catch (Exception e) {
            e.printStackTrace();
            this.backboneLastError = System.currentTimeMillis();

            if (jedis != null) {
                this.backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        } finally {

            if (jedis != null) {
                this.backboneJedisPool.returnResource(jedis);
            }

        }

        return result;
    }

    private void setupHourEvents() {

        final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("Proton - Hour Event Thread").setDaemon(true).build());

        final int minOfHour = Calendar.getInstance().get(12);
        final int minToHour = 60 - minOfHour;
        final int minToHalfHour = minToHour >= 30 ? minToHour : 30 - minOfHour;

        executor.scheduleAtFixedRate(() -> Proton.getInstance().getServer().getScheduler().runTask(this, () -> Proton.getInstance().getServer().getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(11)))), (long) minToHour, 60L, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(() -> Proton.getInstance().getServer().getScheduler().runTask(this, () -> Proton.getInstance().getServer().getPluginManager().callEvent(new HalfHourEvent(Calendar.getInstance().get(11), Calendar.getInstance().get(12)))), (long) minToHalfHour, 30L, TimeUnit.MINUTES);
    }

}
