package org.cavepvp.profiles.playerProfiles;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.impl.*;
import org.cavepvp.profiles.playerProfiles.impl.socialmedia.SocialMediaType;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticServer;
import org.cavepvp.profiles.playerProfiles.impl.stats.Statistics;

import java.util.*;

public class PlayerProfile {

    @Getter
    @SerializedName("_id") private UUID uuid;

    @Getter @Setter private String name;

    @Getter private int totalKills;

    @Getter private int totalDeaths;

    @Getter @Setter private int reputation;
    @Getter @Setter private double playerReputation = 0;

    @Getter @Setter private int placing;

    @Getter @Setter private Preferences preferences2;
    @Getter @Setter private ModLayout modLayout;

    @Getter private List<UUID> friends = new ArrayList<>();
    @Getter private List<UUID> friendRequests = new ArrayList<>();
    @Getter @Setter private List<CoinPurchase> coinPurchases = new ArrayList<>();
    @Getter private Map<StatisticServer, Statistics> statistics = new HashMap<>();
    @Getter private List<Notification> notifications = new ArrayList<>();
    @Getter @Setter private List<NotificationType> enabledNotificatons = new ArrayList<>();
    @Getter private Map<SocialMediaType, String> socialMedia = new HashMap<>();

    PlayerProfile(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.totalKills = 0;
        this.preferences2 = new Preferences();
        this.totalDeaths = 0;

        for (StatisticServer value : StatisticServer.values()) {
            statistics.put(value, new Statistics(value));
        }
    }
    
    public void recalculateKillsDeaths() {
        int kills = 0;
        int deaths = 0;

        for (Statistics value : this.statistics.values()) {
            kills += value.getKills();
            deaths += value.getDeaths();
        }

        this.totalKills = kills;
        this.totalDeaths = deaths;

        this.save();
    }

    public int getCoins() {
        int coins = 0;

        for (CoinPurchase coinPurchase : new ArrayList<>(this.getSortedCoinPurchases())) {
            int coinsLeft = coinPurchase.coinsRemaining();

            if (coinsLeft <= 0) {
                this.save();
                continue;
            }

            coins += coinsLeft;
        }

        return coins;
    }

    public static final Comparator<CoinPurchase> COMPARATOR = Comparator.comparingDouble(CoinPurchase::getCostPerCoin);

    public List<CoinPurchase> getSortedCoinPurchases() {
        List<CoinPurchase> toReturn = new ArrayList<>();

        for (CoinPurchase coinPurchase : new ArrayList<>(coinPurchases)) {

            if (coinPurchase.coinsRemaining() == 0) {
                continue;
            }

            toReturn.add(coinPurchase);
        }

        toReturn.sort(COMPARATOR);

        return toReturn;
    }

    public boolean save() {
        return Profiles.getInstance().getPlayerProfileHandler().update(this).wasAcknowledged();
    }
}
