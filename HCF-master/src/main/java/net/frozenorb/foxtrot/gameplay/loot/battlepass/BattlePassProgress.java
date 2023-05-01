package net.frozenorb.foxtrot.gameplay.loot.battlepass;

import com.google.gson.annotations.JsonAdapter;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.KillEntityChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.MineBlockChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer.ChallengeSetSerializer;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.serialize.TierSetSerializer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

@Getter
public class BattlePassProgress {

    private final UUID uuid;
    private boolean requiresSave;
    private boolean premium;
    private int experience = 0;

    @JsonAdapter(ChallengeSetSerializer.class)
    protected Set<Challenge> completedChallenges = new HashSet<>();

    protected Map<Material, Integer> blocksMined = new HashMap<>();
    protected Map<EntityType, Integer> entityKills = new HashMap<>();

    // daily
    @Setter
    private UUID dailyChallengesId;

    @JsonAdapter(ChallengeSetSerializer.class)
    protected Set<Challenge> completedDailyChallenges = new HashSet<>();

    private Map<Material, Integer> dailyBlocksMined = new HashMap<>();
    private Map<EntityType, Integer> dailyEntityKills = new HashMap<>();

    @Setter private int valuablesSold;
    @Setter private int archerTags;
    @Setter private boolean madeFactionRaidable;
    @Setter private int partnerItemsUsed;
    @Setter private boolean visitedNether;
    @Setter private boolean visitedEnd;
    @Setter private boolean visitActiveKoth;
    @Setter private boolean visitTreasureCove;
    @Setter private boolean attemptCaptureKoth;
    @Setter private boolean visitGlowstoneMountain;

    @JsonAdapter(TierSetSerializer.class)
    private Set<Tier> claimedRewardsPremium = new HashSet<>();
    @JsonAdapter(TierSetSerializer.class)
    private Set<Tier> claimedRewardsFree = new HashSet<>();

    public BattlePassProgress(UUID uuid, UUID dailyChallengesId) {
        this.uuid = uuid;
        this.dailyChallengesId = dailyChallengesId;

        fillDefaults();
    }

    public void fillDefaults() {
        for (Challenge challenge : Foxtrot.getInstance().getBattlePassHandler().getAllChallenges()) {
            if (challenge instanceof KillEntityChallenge) {
                entityKills.putIfAbsent(((KillEntityChallenge) challenge).getEntityType(), 0);
            } else if (challenge instanceof MineBlockChallenge) {
                blocksMined.putIfAbsent(((MineBlockChallenge) challenge).getMaterial(), 0);
            }
        }
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
        this.requiresSave = true;
    }

    public void setExperience(int experience) {
        this.experience += experience;
    }

    public void addExperience(int experience) {
        this.experience += experience;
    }

    public boolean isTierUnlocked(Tier tier) {
        return experience >= tier.getRequiredExperience();
    }

    public int getBlocksMined(Material material, boolean daily) {
        return (daily ? dailyBlocksMined : blocksMined).getOrDefault(material, 0);
    }

    public void incrementBlocksMined(Material material) {
        blocksMined.put(material, blocksMined.getOrDefault(material, 0) + 1);
        dailyBlocksMined.put(material, dailyBlocksMined.getOrDefault(material, 0) + 1);
        requiresSave = true;
    }

    public boolean isTrackingBlock(Material material) {
        return blocksMined.containsKey(material) || dailyBlocksMined.containsKey(material);
    }

    public int getEntitiesKilled(EntityType entityType, boolean daily) {
        return (daily ? dailyEntityKills : entityKills).getOrDefault(entityType, 0);
    }

    public void incrementEntitiesKilled(EntityType entityType) {
        entityKills.put(entityType, entityKills.getOrDefault(entityType, 0) + 1);
        dailyEntityKills.put(entityType, dailyEntityKills.getOrDefault(entityType, 0) + 1);
        requiresSave = true;
    }

    public boolean isTrackingKillsForEntity(EntityType entityType) {
        return entityKills.containsKey(entityType) || dailyEntityKills.containsKey(entityType);
    }

    /**
     * Gets the highest tier the player has not completed, or the first tier if none have been completed.
     */
    public Tier getCurrentTier() {
        return Foxtrot.getInstance().getBattlePassHandler().getFirstUnreachedTier(experience);
    }

    public boolean hasCompletedChallenge(Challenge challenge) {
        if (challenge.isDaily()) {
            return completedDailyChallenges.contains(challenge);
        } else {
            return completedChallenges.contains(challenge);
        }
    }

    public void completeChallenge(Challenge challenge) {
        if (challenge.isDaily()) {
            completedDailyChallenges.add(challenge);
        } else {
            completedChallenges.add(challenge);
        }

        experience += challenge.getExperience();
        requiresSave = true;
    }

    public void resetDailyChallengeData(UUID uuid) {
        dailyChallengesId = uuid;
        dailyEntityKills.clear();
        dailyBlocksMined.clear();
        completedDailyChallenges.clear();
        requiresSave = true;
    }

    public void requiresSave() {
        requiresSave = true;
    }

}
