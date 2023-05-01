package net.frozenorb.foxtrot.gameplay.loot.battlepass;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.*;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitActiveKOTHChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitEndChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitGlowstoneMountain;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit.VisitNetherChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.listener.ChallengeListeners;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer.ChallengeSerializer;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.daily.DailyChallenges;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.listener.BattlePassListeners;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.util.CC;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BattlePassHandler {

    public static final String CHAT_PREFIX = CC.GRAY + "[" + CC.GOLD + "BattlePass" + CC.GRAY + "] ";
    private static final JsonWriterSettings JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Challenge.class, new ChallengeSerializer()).serializeNulls().create();

    private Map<Integer, Tier> tiers = new HashMap<>();
    private Map<String, Challenge> challenges = new HashMap<>();
    @Getter
    private DailyChallenges dailyChallenges;

    private MongoCollection<Document> mongoCollection = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("BattlePass");
    private Map<UUID, BattlePassProgress> progress = new HashMap<>();

    @Getter
    @Setter
    private boolean adminDisabled = false;

    public BattlePassHandler() {
        loadTiers();
        loadChallenges();
        loadDailyChallenges();

        Bukkit.getServer().getPluginManager().registerEvents(new BattlePassListeners(), Foxtrot.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new ChallengeListeners(), Foxtrot.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    useProgress(player.getUniqueId(), progress -> {
                        if (progress.isRequiresSave()) {
                            saveProgress(progress);
                        }
                    });
                }

                saveDailyChallenges();
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L * 60L, 20L * 60L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= dailyChallenges.getExpiresAt()) {
                    generateNewDailyChallenges();
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L * 5L, 20L * 5L);
    }

    public List<Tier> getTiers() {
        return new ArrayList<>(tiers.values());
    }

    public Tier getTier(int number) {
        return tiers.get(number);
    }

    public List<Tier> getSortedTiers() {
        return tiers.values().stream().sorted(Comparator.comparingInt(Tier::getRequiredExperience)).collect(Collectors.toList());
    }

    public Tier getFirstUnreachedTier(int experience) {
        List<Tier> tiers = getSortedTiers();

        for (Tier next : tiers) {
            if (experience < next.getRequiredExperience()) {
                return next;
            }
        }

        return tiers.stream().findFirst().orElse(null);
    }

    public int getMaxTier() {
        return 14;
    }

    public Collection<Challenge> getAllChallenges() {
        List<Challenge> challenges = new ArrayList<>();
        challenges.addAll(getChallenges());
        challenges.addAll(dailyChallenges.getChallenges());
        return challenges;
    }

    public Collection<Challenge> getChallenges() {
        return challenges.values();
    }

    public Challenge getChallenge(String id) {
        return challenges.get(id.toLowerCase());
    }

    public BattlePassProgress getProgress(UUID uuid) {
        return progress.get(uuid);
    }

    public void useProgress(Player player, Consumer<BattlePassProgress> use) {
        useProgress(player.getUniqueId(), use);
    }

    public void useProgress(UUID uuid, Consumer<BattlePassProgress> use) {
        BattlePassProgress progress = this.progress.get(uuid);
        if (progress != null) {
            use.accept(progress);
        }
    }

    public void loadProgress(UUID uuid) {
        BattlePassProgress data = fetchProgress(uuid);
        progress.put(uuid, data);
    }

    public BattlePassProgress getOrLoadProgress(UUID uuid) {
        if (progress.containsKey(uuid)) {
            return getProgress(uuid);
        } else {
            return fetchProgress(uuid);
        }
    }

    public BattlePassProgress fetchProgress(UUID uuid) {
        Document document = mongoCollection.find(new Document("uuid", uuid.toString())).first();

        BattlePassProgress progress;
        if (document == null) {
            progress = new BattlePassProgress(uuid, dailyChallenges.getIdentifier());
        } else {
            progress = GSON.fromJson(document.toJson(JSON_WRITER_SETTINGS), BattlePassProgress.class);
        }

        if (!progress.getDailyChallengesId().equals(dailyChallenges.getIdentifier())) {
            progress.resetDailyChallengeData(dailyChallenges.getIdentifier());
        }

        progress.fillDefaults();

        return progress;
    }

    public void saveProgress(BattlePassProgress progress) {
        Document document = Document.parse(GSON.toJson(progress));
        mongoCollection.replaceOne(new Document("uuid", progress.getUuid().toString()), document, new ReplaceOptions().upsert(true));
    }

    public void unloadProgress(UUID uuid) {
        BattlePassProgress data = progress.remove(uuid);
        if (data != null && data.isRequiresSave()) {
            saveProgress(data);
        }
    }

    public void clearProgress(UUID uuid) {
        mongoCollection.deleteOne(new Document("uuid", uuid.toString()));

        if (progress.containsKey(uuid)) {
            progress.put(uuid, new BattlePassProgress(uuid, dailyChallenges.getIdentifier()));
        }
    }

    public void wipe() {
        mongoCollection.drop();
        progress.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            progress.put(player.getUniqueId(), new BattlePassProgress(player.getUniqueId(), dailyChallenges.getIdentifier()));
        }
    }

    public void checkCompletionsAsync(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkCompletions(player);
            }
        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public void checkCompletions(Player player) {
        useProgress(player.getUniqueId(), progress -> {
            if (progress.isPremium()) {
                for (Challenge challenge : getChallenges()) {
                    if (progress.hasCompletedChallenge(challenge)) {
                        continue;
                    }

                    if (challenge.meetsCompletionCriteria(player)) {
                        challenge.completed(player, progress);
                    }
                }
            }

            for (Challenge challenge : dailyChallenges.getChallenges()) {
                if (progress.hasCompletedChallenge(challenge)) {
                    continue;
                }

                if (challenge.meetsCompletionCriteria(player)) {
                    challenge.completed(player, progress);
                }
            }
        });
    }

    private void loadDailyChallenges() {
        File dailyChallengesFile = new File(Foxtrot.getInstance().getDataFolder(), "data/battle-pass-daily-challenges.json");
        if (dailyChallengesFile.exists()) {
            try (Reader reader = Files.newReader(dailyChallengesFile, Charsets.UTF_8)) {
                dailyChallenges = GSON.fromJson(reader, DailyChallenges.class);
            } catch (Exception e) {
                Foxtrot.getInstance().getLogger().severe("Failed to load BattlePass Daily Challenges!");
                e.printStackTrace();
            }
        } else {
            generateNewDailyChallenges();
        }
    }

    private void saveDailyChallenges() {
        try {
            Files.write(GSON.toJson(dailyChallenges), new File(Foxtrot.getInstance().getDataFolder(), "data/battle-pass-daily-challenges.json"), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNewDailyChallenges() {
        dailyChallenges = new DailyChallenges();
        dailyChallenges.generate();

        for (Player player : Bukkit.getOnlinePlayers()) {
            useProgress(player.getUniqueId(), progress -> progress.resetDailyChallengeData(dailyChallenges.getIdentifier()));
        }

        saveDailyChallenges();
    }

    private void loadTiers() {
        Arrays.asList(
                new Tier(1, 20)
                        .newReward(true, reward -> reward.addText(CC.DARK_PURPLE + CC.BOLD + "5x Legendary Keys").addCommand("cr givekey {playerName} Legendary 5"))
                        .newReward(false, reward -> reward.addText(CC.DARK_PURPLE + CC.BOLD + "3x Legendary Keys").addCommand("cr givekey {playerName} Legendary 3")),

                new Tier(2, 40)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "16x Golden Apples").addCommand("give 322 16 {playerName}")),

                new Tier(3, 60)
                        .newReward(true, reward -> reward.addText(CC.PINK + CC.BOLD + "1x God Apple").addCommand("give 322:1 1 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.GOLD + CC.BOLD + "8x Golden Apples").addCommand("give 322 8 {playerName}")),

                new Tier(4, 80)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "5x Halloween Keys").addCommand("cr givekey {playerName} Seasonal 5")),

                new Tier(5, 100)
                        .newReward(true, reward -> reward.addText(CC.GREEN + CC.BOLD + "5x Med Kits").addCommand("ability MedKit 5 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.GOLD + CC.BOLD + "2x Halloween Keys").addCommand("cr givekey {playerName} Seasonal 2")),

                new Tier(6, 120)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "16x Anti-Blockups").addCommand("ability antiblockup 5 {playerName}")),

                new Tier(7, 140)
                        .newReward(true, reward -> reward.addText(CC.GREEN + CC.BOLD + "15,000 XP").addCommand("minecraft:xp 15000 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.GREEN + CC.BOLD + "7,500 XP").addCommand("minecraft:xp 7500 {playerName}")),

                new Tier(8, 160)
                        .newReward(true, reward -> reward.addText(CC.DARK_RED + CC.BOLD + "Cave Clickable Kit").addCommand("clickablekit give Cave {playerName}")),

                new Tier(9, 180)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "2x Portable Bard").addCommand("ability portablebard 2 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.GOLD + CC.BOLD + "5x Anti-Blockups").addCommand("ability antiblockup 5 {playerName}")),

                new Tier(10, 200)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "5x Reinforce Lootboxes").addCommand("crates give {playerName} Reinforce 5")),

                new Tier(11, 220)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "5x Midas Touch").addCommand("ability midastouch 5 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.translate("&b&lUltimate Diamond Kit")).addCommand("clickablekit give UltimateDiamond {playerName}")),

                new Tier(12, 240)
                        .newReward(true, reward -> reward.addText(CC.DARK_AQUA + CC.BOLD + "Random Spawner").addCommand("clickitem give random-spawner 1 {playerName}")),

                new Tier(13, 260)
                        .newReward(true, reward -> reward.addText(CC.PINK + CC.BOLD + "16x Eggports").addCommand("ability Eggport 16 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.PINK + CC.BOLD + "16x Eggports").addCommand("ability Eggport 16 {playerName}")),

                new Tier(14, 280)
                        .newReward(true, reward -> reward.addText(CC.AQUA + CC.BOLD + "1x Airdrops").addCommand("airdrops give {playerName} 1")),

                new Tier(15, 300)
                        .newReward(true, reward -> reward.addText(CC.RED + CC.BOLD + "Boss Summoner").addCommand("clickitem give boss-summoner 1 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.GOLD + CC.BOLD + "3x Reinforce Lootbox").addCommand("crates give {playerName} Reinforce 3")),

                new Tier(16, 320)
                        .newReward(true, reward -> reward.addText(CC.LIGHT_PURPLE + CC.BOLD + "8x Partner Keys").addCommand("cr givekey {playerName} Partner 8")),

                new Tier(17, 340)
                        .newReward(true, reward -> reward.addText(CC.DARK_RED + CC.BOLD + "8x Random Owner Keys").addCommand("randomownerkeys {playerName} 8"))
                        .newReward(false, reward -> reward.addText(CC.GREEN + CC.BOLD + "5x Med Kits").addCommand("ability MedKit 5 {playerName}")),

                new Tier(18, 360)
                        .newReward(true, reward -> reward.addText(CC.DARK_RED + CC.BOLD + "1x September Mystery Box").addCommand("mcrate give {playerName} Seasonal 1")),

                new Tier(19, 380)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "3x Fall Lootbox").addCommand("crates give {playerName} Seasonal 3"))
                        .newReward(false, reward -> reward.addText(CC.GOLD + CC.BOLD + "3x Fall Lootbox").addCommand("crates give {playerName} Seasonal 3")),

                new Tier(20, 400)
                        .newReward(true, reward -> reward.addText(CC.GOLD + CC.BOLD + "Halloween Clickable Kit").addCommand("clickablekit give NewSeasonal {playerName}")),

                new Tier(21, 420)
                        .newReward(true, reward -> reward.addText(CC.LIGHT_PURPLE + CC.BOLD + "16x Random Ability Items").addCommand("clickitem give random-ability-item 16 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.GOLD + CC.BOLD + "2x Portable Bard").addCommand("ability portablebard 2 {playerName}")),

                new Tier(22, 440)
                        .newReward(true, reward -> reward.addText(CC.DARK_RED + CC.BOLD + "Omega Chest").addCommand("chest give Omega {playerName} 1"))
                        .newReward(false, reward -> reward.addText(CC.BLUE + CC.BOLD + "Random Armor Class").addCommand("clickitem give random-armor-class 1 {playerName}")),

                new Tier(23, 460)
                        .newReward(true, reward -> reward.addText(CC.BLUE + CC.BOLD + "Random Armor Class").addCommand("clickitem give random-armor-class 1 {playerName}"))
                        .newReward(false, reward -> reward.addText(CC.AQUA + CC.BOLD + "3x Air Drops").addCommand("airdrops give {playerName} 3")),

                new Tier(24, 480)
                        .newReward(true, reward -> reward.addText(CC.DARK_RED + CC.BOLD + "2x September Mystery Box").addCommand("mcrate give {playerName} Seasonal 2"))
                        .newReward(false, reward -> reward.addText(CC.DARK_RED + CC.BOLD + "1x September Mystery Box").addCommand("mcrate give {playerName} Seasonal 1")),

                new Tier(25, 500)
                        .newReward(true, reward -> reward.addText(CC.AQUA + CC.BOLD + "10x Airdrops", CC.YELLOW + CC.BOLD + "7d VIP Subscription", CC.translate("&4&l2x Omega Chests")).addCommand("chest give Omega {playerName} 2").addCommand("airdrops give {playerName} 10").addCommand("vouchers addrank {playerName} VIP 7d &e&l7d VIP Subscription"))
                        .newReward(false, reward -> reward.addText(CC.AQUA + CC.BOLD + "5x Airdrops", CC.translate("&4&l1x Omega Chests")).addCommand("airdrops give {playerName} 5").addCommand("chest give Omega {playerName} 1").addCommand("reclaimreset {playerName}"))
        ).forEach(tier -> tiers.put(tier.getNumber(), tier));
    }

    private void loadChallenges() {
        Arrays.asList(
                new OreChallenge("mine-50", "Mine 50 Diamonds", 5, false, OreChallenge.OreType.DIAMOND, 50),
                new OreChallenge("mine-100", "Mine 100 Diamonds", 10, false, OreChallenge.OreType.DIAMOND, 100),
                new OreChallenge("mine-250", "Mine 250 Diamonds", 15, false, OreChallenge.OreType.DIAMOND, 250),

                new ValuablesSoldChallenge("valuables-5000", "Sell $5,000 of Valuables", 10, false, 5000),

                new AttemptCaptureKOTHChallenge("attempt-cap-koth", "Attempt to Capture a KOTH", 10, false),

                new KillstreakChallenge("killstreak-3", "Reach a Killstreak of 3", 10, false, 3),
                new KillstreakChallenge("killstreak-5", "Reach a Killstreak of 5", 15, false, 5),

                new ArcherTagsChallenge("archer-tags-10", "Archer Tag 10 Players", 5, false, 10),

                new MakeFactionRaidableChallenge("make-fac-raidable", "Make Faction Raidable", 20, false),

                new UsePartnerItemChallenge("use-partner-items-3", "Use 3 Partner Items", 5, false, 3),
                new UsePartnerItemChallenge("use-partner-items-5", "Use 5 Partner Items", 10, false, 5),
                new UsePartnerItemChallenge("use-partner-items-10", "Use 10 Partner Items", 15, false, 10),

                new PlayTimeChallenge("playtime-1hr", "Reach 1 Hour of Play Time", 5, false, TimeUnit.HOURS.toMillis(1L)),
                new PlayTimeChallenge("playtime-3hr", "Reach 3 Hours of Play Time", 5, false, TimeUnit.HOURS.toMillis(3L)),
                new PlayTimeChallenge("playtime-6hr", "Reach 6 Hours of Play Time", 10, false, TimeUnit.HOURS.toMillis(6L)),
                new PlayTimeChallenge("playtime-12hr", "Reach 12 Hours of Play Time", 15, false, TimeUnit.HOURS.toMillis(12L)),

                new VisitNetherChallenge("visit-nether", "Visit the Nether", 5, false),
                new VisitEndChallenge("visit-end", "Visit the End", 5, false),
                new VisitActiveKOTHChallenge("visit-active-koth", "Visit an active KOTH", 10, false),
                new VisitGlowstoneMountain("visit-glowstone", "Visit the Glowstone Mountain", 10, false),

                new MineBlockChallenge("mine-sand-512", "Mine 512 Sand", 5, false, Material.SAND, 512),
                new MineBlockChallenge("mine-logs-256", "Mine 256 Logs", 5, false, Material.LOG, 256),
                new MineBlockChallenge("mine-glowstone-32", "Mine 32 Glowstone", 5, false, Material.GLOWSTONE, 32),

                new KillEntityChallenge("kill-players-10", "Kill 10 Players", 15, false, EntityType.PLAYER, 10),
                new KillEntityChallenge("kill-creepers-30", "Kill 30 Creepers", 10, false, EntityType.CREEPER, 30),
                new KillEntityChallenge("kill-endermen-20", "Kill 20 Endermen", 10, false, EntityType.ENDERMAN, 20),
                new KillEntityChallenge("kill-cows-30", "Kill 30 Cows", 5, false, EntityType.COW, 30)
        ).forEach(challenge -> challenges.put(challenge.getId(), challenge));
    }

}