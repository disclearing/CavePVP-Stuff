package net.frozenorb.foxtrot;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.shampaggon.crackshot.CSUtility;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import lombok.Getter;
import lombok.Setter;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.frozenorb.foxtrot.brewer.FancyBrewerModule;
import net.frozenorb.foxtrot.chat.tips.TipsHandler;
import net.frozenorb.foxtrot.chat.trivia.TriviaHandler;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.ability.type.MidasTouch;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.boosters.NetworkBoosterHandler;
import net.frozenorb.foxtrot.gameplay.bosses.BossHandler;
import net.frozenorb.foxtrot.gameplay.cavesays.CaveSaysHandler;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItemHandler;
import net.frozenorb.foxtrot.gameplay.content.ContentHandler;
import net.frozenorb.foxtrot.gameplay.coupondrops.CouponDropsHandler;
import net.frozenorb.foxtrot.gameplay.events.EventHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryHandler;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEventsHandler;
import net.frozenorb.foxtrot.gameplay.events.outposts.OutpostHandler;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.grounds.SpawnerGroundsHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.bounty.BountyManager;
import net.frozenorb.foxtrot.gameplay.kitmap.daily.DailyKitHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.GemHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.GemFlipHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.partner.PartnerCrateHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler;
import net.frozenorb.foxtrot.gameplay.lettingIn.LettingInHandler;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDropHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer.ChallengeSerializer;
import net.frozenorb.foxtrot.gameplay.loot.crate.CrateHandler;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBoxesHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChestHandler;

import net.frozenorb.foxtrot.gameplay.loot.redeem.RedeemCreatorHandler;
import net.frozenorb.foxtrot.gameplay.loot.shop.ShopHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import net.frozenorb.foxtrot.gameplay.loot.voteparty.VotePartyHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.mastery.MasteryUpgradeHandler;
import net.frozenorb.foxtrot.gameplay.quest.QuestHandler;
import net.frozenorb.foxtrot.gameplay.totem.TotemHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.nametag.modsuite.InventorySeeHandler;
import net.frozenorb.foxtrot.packetborder.PacketBorderThread;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.*;
import net.frozenorb.foxtrot.persist.maps.toggle.*;
import net.frozenorb.foxtrot.serialization.*;
import net.frozenorb.foxtrot.server.ListenerHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.keyalls.KeyAllHandler;
import net.frozenorb.foxtrot.server.customTimer.CustomTimerHandler;
import net.frozenorb.foxtrot.server.deathban.DeathbanArenaHandler;
import net.frozenorb.foxtrot.server.polls.PollHandler;
import net.frozenorb.foxtrot.server.voucher.VoucherHandler;
import net.frozenorb.foxtrot.tab.FoxtrotTabLayoutProvider;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.PersistableLocation;
import net.frozenorb.foxtrot.util.RegenUtils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticServer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Foxtrot extends JavaPlugin {

    public static String MONGO_DB_NAME = "HCTeams";

    public static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
            .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
            .registerTypeHierarchyAdapter(BlockVector.class, new BlockVectorAdapter())
            .registerTypeHierarchyAdapter(PotionAdapter.class, new PotionAdapter())
            .registerTypeAdapter(Challenge.class, new ChallengeSerializer())
            .setPrettyPrinting()
            .serializeNulls().create();

    @Getter public static Foxtrot instance;
    @Getter public MongoClient mongoPool;
    @Getter private BuycraftPlugin buycraftPlugin;
    @Getter private EconomyHandler economyHandler;
    @Getter private DailyKitHandler dailyKitHandler;
    @Getter private ContentHandler contentHandler;
    @Getter private LettingInHandler lettingInHandler;
    @Getter private OutpostHandler outpostHandler;
    @Getter private PvPClassHandler pvpClassHandler;
    @Getter private ItemBoxesHandler itemBoxesHandler;
    @Getter private ClickItemHandler clickItemHandler;
    @Getter private ArmorClassHandler armorClassHandler;
    @Getter private QuestHandler questHandler;
    @Getter private BattlePassHandler battlePassHandler;
    @Getter private WorldEditPlugin worldEdit;
    @Getter private KeyAllHandler keyAllHandler;
    @Getter private CouponDropsHandler couponDropsHandler;
    @Getter private TeamHandler teamHandler;
    @Getter public ServerHandler serverHandler;
    @Getter private MapHandler mapHandler;
    @Getter private TipsHandler tipsHandler;
    @Getter private CitadelHandler citadelHandler;
    @Getter private EventHandler eventHandler;
    @Getter private GemFlipHandler gemFlipHandler;
    @Getter private CaveSaysHandler caveSaysHandler;
    @Getter private TokensHandler tokensHandler;
    @Getter private ConquestHandler conquestHandler;
    @Getter private DeathbanArenaHandler deathbanArenaHandler;
    @Getter private PollHandler pollHandler;
    @Getter private VoucherHandler voucherHandler;
    @Getter private TreasureChestHandler treasureChestHandler;
    @Getter private MiniEventsHandler miniEventsHandler;
    @Getter private SpawnerGroundsHandler spawnerGroundsHandler;
    @Getter private MasteryUpgradeHandler masteryUpgradeHandler;
    @Getter private CavernHandler cavernHandler;
    @Getter private CaveNiteHandler caveNiteHandler;
    @Getter private GlowHandler glowHandler;
    @Getter private CrateHandler crateHandler;
    @Getter private BossHandler bossHandler;
    @Getter private NetworkBoosterHandler networkBoosterHandler;
    @Getter private RedeemCreatorHandler redeemCreatorHandler;
    @Getter private CustomTimerHandler customTimerHandler;
    @Getter private ShopHandler shopHandler;
    @Getter private TotemHandler totemHandler;
    @Getter private PlaytimeMap playtimeMap;
    @Getter private OppleMap oppleMap;
    @Getter private DeathbanMap deathbanMap;
    @Getter private PvPTimerMap PvPTimerMap;
    @Getter private SaleTimersScoreboardMap saleTimersScoreboardMap;
    @Getter private StartingPvPTimerMap startingPvPTimerMap;
    @Getter private DeathsMap deathsMap;
    @Getter private KillsMap killsMap;
    @Getter private FactionFilterMap factionFilterMap;
    @Getter private KitmapTokensMap kitmapTokensMap;
    @Getter private AirDropHandler airDropHandler;
    @Getter private GemHandler gemHandler;
    @Getter private PartnerCrateHandler partnerCrateHandler;
    @Getter private BountyManager bountyManager;
    @Getter private KillstreakMap killstreakMap;
    @Getter private KillTagMap killTagMap;
    @Getter private TeamColorMap teamColorMap;
    @Getter private EnemyColorMap enemyColorMap;
    @Getter private ArcherTagColorMap archerTagColorMap;
    @Getter private FocusColorMap focusColorMap;
    @Getter private TeamFocusColorMap teamFocusColorMap;
    @Getter private AnnoyingBroadcastMap annoyingBroadcastMap;
    @Getter private ChatModeMap chatModeMap;
    @Getter private FishingKitMap fishingKitMap;
    @Getter private ToggleGlobalChatMap toggleGlobalChatMap;
    @Getter private ChatSpyMap chatSpyMap;
    @Getter private DiamondMinedMap diamondMinedMap;
    @Getter private GoldMinedMap goldMinedMap;
    @Getter private IronMinedMap ironMinedMap;
    @Getter private CoalMinedMap coalMinedMap;
    @Getter private RedstoneMinedMap redstoneMinedMap;
    @Getter private LapisMinedMap lapisMinedMap;
    @Getter private EmeraldMinedMap emeraldMinedMap;
    @Getter private FirstJoinMap firstJoinMap;
    @Getter private LastJoinMap lastJoinMap;
    @Getter private FriendLivesMap friendLivesMap;
    @Getter private AbilityCooldownsScoreboardMap abilityCooldownsScoreboardMap;
    @Getter private WrappedBalanceMap wrappedBalanceMap;
    @Getter private TreasureCoveHandler treasureCoveHandler;
    @Getter private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
    @Getter private ToggleDeathMessageMap toggleDeathMessageMap;
    @Getter private ToggleClaimMessageMap toggleClaimMessageMap;
    @Getter private TabListModeMap tabListModeMap;
    @Getter private CobblePickupMap cobblePickupMap;
    @Getter private MobDropsPickupMap mobDropsPickupMap;
    @Getter private KDRMap kdrMap;
    @Getter private BountyCooldownMap bountyCooldownMap;
    @Getter private ReclaimMap reclaimMap;
    @Getter private TriviaHandler triviaHandler;
    @Getter private FuryHandler furyHandler;
    @Getter private DTRDisplayMap DTRDisplayMap;
    @Getter private TeamfightModeMap teamfightModeMap;
    @Getter private LCTeamViewMap lcTeamViewMap;
    @Getter private TipsMap tipsMap;
    @Getter private CSUtility csUtility;
    @Getter private GemMap gemMap;
    @Getter private GemBoosterMap gemBoosterMap;
    @Getter @Setter private CombatLoggerListener combatLoggerListener;
    @Getter private InventorySeeHandler inventorySeeHandler;
    @Getter private FancyBrewerModule fancyBrewerModule;
    @Getter private VotePartyHandler votePartyHandler;

    @Getter
    @Setter
    // for the case of some commands in the plugin,
    // a player shouldn't be able to do them in a duel
    // thus this predicate exists to test that to avoid dep. issues
    private Predicate<Player> inDuelPredicate = (player) -> mapHandler.isKitMap() && mapHandler.getDuelHandler().isInDuel(player);

//    @Getter private BuycraftPlugin buycraftPlugin;

    @Getter
    @Setter
    private Predicate<Player> inEventPredicate = (player) ->
            mapHandler.isKitMap() &&
                    mapHandler.getGameHandler().isOngoingGame() && mapHandler.getGameHandler().getOngoingGame().isPlaying(player.getUniqueId());

    @Override
    public void onEnable() {
        if (Bukkit.getServerName().contains(" ")) {
            System.out.println("*********************************************");
            System.out.println("               ATTENTION");
            System.out.println("SET server-name VALUE IN server.properties TO");
            System.out.println("A PROPER SERVER NAME. THIS WILL BE USED AS THE");
            System.out.println("MONGO DATABASE NAME.");
            System.out.println("*********************************************");
            this.getServer().shutdown();
            return;
        }

        instance = this;
        saveDefaultConfig();

        try {

            if (!this.getConfig().getString("Mongo.Pass").equalsIgnoreCase("")) {
                final MongoCredential mongoCredential = MongoCredential.createCredential(this.getConfig().getString("Mongo.User", "admin"), this.getConfig().getString("Mongo.Database", "admin"), this.getConfig().getString("Mongo.Pass").toCharArray());
                this.mongoPool = new MongoClient(new ServerAddress(getConfig().getString("Mongo.Host", "127.0.0.1"), 27017), Collections.singletonList(mongoCredential));
            } else {
                this.mongoPool = new MongoClient(new ServerAddress(getConfig().getString("Mongo.Host", "127.0.0.1"), 27017));
            }

            MONGO_DB_NAME = this.getConfig().getString("Mongo.DBName");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (World world : Bukkit.getWorlds()) {
            world.setThundering(false);
            world.setStorm(false);

            if (world.getEnvironment() == World.Environment.NORMAL) {
                world.setTime(18000);
            }

            world.setWeatherDuration(Integer.MAX_VALUE);
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("mobGriefing", "false");
        }

        (new DTRHandler()).runTaskTimer(this, 20L, 20 * 120);
        (new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
        (new PacketBorderThread()).start();

        final Plugin plugin = this.getServer().getPluginManager().getPlugin("BuycraftX");

        if (plugin != null && plugin.isEnabled() && plugin instanceof BuycraftPlugin) {
            this.buycraftPlugin = (BuycraftPlugin) plugin;
        }

        setupHandlers();
        setupPersistence();
        new ListenerHandler(this);
        setupTasks();

        ConfigurationSerialization.registerClass(PersistableLocation.class);

        Proton.getInstance().getTabHandler().setLayoutProvider(new FoxtrotTabLayoutProvider());

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        getEventHandler().saveEvents();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            getPlaytimeMap().playerQuit(player.getUniqueId(), false);
            player.setMetadata("loggedout", new FixedMetadataValue(this, true));
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        for (Entity e : this.combatLoggerListener.getCombatLoggers()) {
            if (e != null) {
                e.remove();
            }
        }

        if (this.mapHandler.isKitMap() && this.mapHandler.getGameHandler().getOngoingGame() != null) {
            final Game ongoingGame = this.mapHandler.getGameHandler().getOngoingGame();

            for (Player player : ongoingGame.getPlayers()) {
                ongoingGame.removePlayer(player);
            }

            for (Player spectator : ongoingGame.getSpectators()) {
                ongoingGame.removeSpectator(spectator);
            }

            ongoingGame.endGame();
        }

        for (Map.Entry<Location, Material> locationMaterialEntry : MidasTouch.cache.entrySet()) {
            final Block block = locationMaterialEntry.getKey().getBlock();

            block.removeMetadata("MIDAS_TOUCH", Foxtrot.getInstance());
            block.setType(MidasTouch.cache.remove(block.getLocation()));
        }

        RegenUtils.resetAll();

        Proton.getInstance().runRedisCommand((jedis) -> {
            jedis.save();
            return null;
        });

        this.saveData();
        if (this.mapHandler.isKitMap()) {
            this.gemFlipHandler.onDisable();
        }

        if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null) {
            if (Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
                Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
            }
        }
    }
    public void saveData() {
        RedisSaveTask.save(null, false);
        Foxtrot.getInstance().getServerHandler().save();

        if (!getConfig().getBoolean("kits", false) && !getConfig().getBoolean("teams", false)) {
            this.crateHandler.saveData();
            this.treasureCoveHandler.saveTreasureInfo();
            this.networkBoosterHandler.saveBoosters();
            this.caveNiteHandler.saveLocations();
        }

        // This shouldn't be here but whatever
        if (mapHandler.isKitMap()) {
            mapHandler.getKitUpgradesHandler().saveUpgrades();
            bountyManager.save();
            dailyKitHandler.saveData();
        }

        this.fancyBrewerModule.shutdown(this);
        this.airDropHandler.saveLootTable();
        this.customTimerHandler.saveData();
        this.mapHandler.getAbilityHandler().saveStatistics();
        this.tipsHandler.saveTips();
        this.voucherHandler.saveData();
        this.treasureChestHandler.saveData();
        this.outpostHandler.saveData();
        this.keyAllHandler.saveData();

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            Foxtrot.getInstance().getMapHandler().getStatsHandler().save();
        }
    }


    private void setupHandlers() {
        this.serverHandler = new ServerHandler();
        this.mapHandler = new MapHandler();

        if (this.getMapHandler().isKitMap()) {
            new WorldCreator("kits_events").environment(World.Environment.NORMAL).createWorld();
        } else {
            new WorldCreator("Spawn").environment(World.Environment.NORMAL).createWorld();
            new WorldCreator("Deathban").environment(World.Environment.NORMAL).createWorld();
            new WorldCreator("sg").environment(World.Environment.NORMAL).createWorld();
        }

        this.eventHandler = new EventHandler();
        this.mapHandler.load();
        this.economyHandler = new EconomyHandler(this);

        this.teamHandler = new TeamHandler();
        LandBoard.getInstance().loadFromTeams();

        this.citadelHandler = new CitadelHandler();
        this.pvpClassHandler = new PvPClassHandler();
        this.conquestHandler = new ConquestHandler();
        this.outpostHandler = new OutpostHandler(this);
        this.shopHandler = new ShopHandler(this);
        this.contentHandler = new ContentHandler(this);
        this.totemHandler = new TotemHandler(this);

        if (mapHandler.isKitMap()) {
            this.csUtility = new CSUtility();
            (this.gemHandler = new GemHandler()).loadChances();
            this.gemFlipHandler = new GemFlipHandler();
            this.partnerCrateHandler = new PartnerCrateHandler();
            this.bountyManager = new BountyManager();
            this.dailyKitHandler = new DailyKitHandler(this);
            this.masteryUpgradeHandler = new MasteryUpgradeHandler(this);
        }

        this.tokensHandler = new TokensHandler();

        if (getConfig().getBoolean("glowstoneMountain", false)) {
            this.glowHandler = new GlowHandler();
        }

        this.customTimerHandler = new CustomTimerHandler();
        this.voucherHandler = new VoucherHandler(this);
        this.pollHandler = new PollHandler();
        this.tipsHandler = new TipsHandler(this);
        this.inventorySeeHandler = new InventorySeeHandler();
        this.fancyBrewerModule = new FancyBrewerModule();
        this.fancyBrewerModule.init(this);
        this.deathbanArenaHandler = new DeathbanArenaHandler(this);
        this.questHandler = new QuestHandler(this);
        this.armorClassHandler = new ArmorClassHandler(this);
        this.treasureChestHandler = new TreasureChestHandler(this);

        if (!this.serverHandler.isTeams() && !this.mapHandler.isKitMap()) {
            this.crateHandler = new CrateHandler(this);
            this.redeemCreatorHandler = new RedeemCreatorHandler();
            this.itemBoxesHandler = new ItemBoxesHandler(this);
            this.clickItemHandler = new ClickItemHandler(this);
            this.furyHandler = new FuryHandler(this);
            this.battlePassHandler = new BattlePassHandler();
            this.treasureCoveHandler = new TreasureCoveHandler();
            this.lettingInHandler = new LettingInHandler(this);
            this.votePartyHandler = new VotePartyHandler(this);
            this.caveNiteHandler = new CaveNiteHandler(this);
            this.spawnerGroundsHandler = new SpawnerGroundsHandler(this);
        }

        this.bossHandler = new BossHandler(this);
        this.miniEventsHandler = new MiniEventsHandler(this);
        this.caveSaysHandler = new CaveSaysHandler(this);
        this.networkBoosterHandler = new NetworkBoosterHandler(this);
        this.couponDropsHandler = new CouponDropsHandler(this);
        this.triviaHandler = new TriviaHandler(this);
        this.airDropHandler = new AirDropHandler(this);
        this.keyAllHandler = new KeyAllHandler(this);

        if (getConfig().getBoolean("cavern", false)) {
            cavernHandler = new CavernHandler();
        }

        Proton.getInstance().getCommandHandler().registerAll(this);

        DeathMessageHandler.init();
    }

    private void setupPersistence() {
        (playtimeMap = new PlaytimeMap()).loadFromRedis();
        (oppleMap = new OppleMap()).loadFromRedis();
        (deathbanMap = new DeathbanMap()).loadFromRedis();
        (PvPTimerMap = new PvPTimerMap()).loadFromRedis();
        (saleTimersScoreboardMap = new SaleTimersScoreboardMap()).loadFromRedis();
        (startingPvPTimerMap = new StartingPvPTimerMap()).loadFromRedis();
        (deathsMap = new DeathsMap()).loadFromRedis();
        (factionFilterMap = new FactionFilterMap()).loadFromRedis();
        (kitmapTokensMap = new KitmapTokensMap()).loadFromRedis();
        (killsMap = new KillsMap()).loadFromRedis();
        (killstreakMap = new KillstreakMap()).loadFromRedis();
        (killTagMap = new KillTagMap()).loadFromRedis();
        (chatModeMap = new ChatModeMap()).loadFromRedis();
        (teamColorMap = new TeamColorMap()).loadFromRedis();
        (enemyColorMap = new EnemyColorMap()).loadFromRedis();
        (archerTagColorMap = new ArcherTagColorMap()).loadFromRedis();
        (teamFocusColorMap = new TeamFocusColorMap()).loadFromRedis();
        (focusColorMap = new FocusColorMap()).loadFromRedis();
        (toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
        (fishingKitMap = new FishingKitMap()).loadFromRedis();
        (friendLivesMap = new FriendLivesMap()).loadFromRedis();
        (chatSpyMap = new ChatSpyMap()).loadFromRedis();
        (diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
        (goldMinedMap = new GoldMinedMap()).loadFromRedis();
        (ironMinedMap = new IronMinedMap()).loadFromRedis();
        (coalMinedMap = new CoalMinedMap()).loadFromRedis();
        (redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
        (annoyingBroadcastMap = new AnnoyingBroadcastMap()).loadFromRedis();
        (lapisMinedMap = new LapisMinedMap()).loadFromRedis();
        (emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
        (DTRDisplayMap = new DTRDisplayMap()).loadFromRedis();
        (teamfightModeMap = new TeamfightModeMap()).loadFromRedis();
        (lcTeamViewMap = new LCTeamViewMap()).loadFromRedis();
        (tipsMap = new TipsMap()).loadFromRedis();
        (firstJoinMap = new FirstJoinMap()).loadFromRedis();
        (lastJoinMap = new LastJoinMap()).loadFromRedis();
        (wrappedBalanceMap = new WrappedBalanceMap()).loadFromRedis();
        (toggleFoundDiamondsMap = new ToggleFoundDiamondsMap()).loadFromRedis();
        (toggleDeathMessageMap = new ToggleDeathMessageMap()).loadFromRedis();
        (toggleClaimMessageMap = new ToggleClaimMessageMap()).loadFromRedis();
        (abilityCooldownsScoreboardMap = new AbilityCooldownsScoreboardMap()).loadFromRedis();
        (tabListModeMap = new TabListModeMap()).loadFromRedis();
        (cobblePickupMap = new CobblePickupMap()).loadFromRedis();
        (mobDropsPickupMap = new MobDropsPickupMap()).loadFromRedis();
        (reclaimMap = new ReclaimMap()).loadFromRedis();
        (kdrMap = new KDRMap()).loadFromRedis();
        (bountyCooldownMap = new BountyCooldownMap()).loadFromRedis();
        (gemMap = new GemMap()).loadFromRedis();
        (gemBoosterMap = new GemBoosterMap()).loadFromRedis();
    }

    private void setupTasks() {
        // unlocks claims at 10 minutes left of SOTW timer
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!CustomTimerCreateCommand.isSOTWTimer()) {
                    return;
                }

                long endsAt = CustomTimerCreateCommand.getCustomTimers().get("&a&lSOTW");
                long remaining = endsAt - System.currentTimeMillis();

                int seconds = (int) (remaining/1000);

                if (sotwReminders.contains(seconds)) {
                    for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                        PlayerUtil.sendTitle(onlinePlayer, "&a&lSOTW", "&f" + TimeUtils.formatIntoDetailedString(seconds) + " &7remaining!");
                    }
                }

                if (remaining <= TimeUnit.MINUTES.toMillis(10L)) {
                    for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                        if (team.isClaimLocked()) {
                            team.setClaimLocked(false);
                            team.sendMessage(CC.YELLOW + "Your faction's claims have been unlocked due to SOTW ending in 10 minutes!");
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20*60*5, 20*60*5);
    }

    public StatisticServer getStatisticServer() {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return StatisticServer.KITS;
        }

        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            return StatisticServer.CLANS;
        }

        return StatisticServer.FASTS;
    }

    private final List<Integer> sotwReminders = Arrays.asList(300, 600, 900, 1800, 3600, 7200);

}
