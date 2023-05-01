package org.cavepvp.profiles.playerProfiles.impl.stats;

import lombok.Getter;
import lombok.Setter;

public class Statistics {

    @Getter @Setter private StatisticServer statisticServer;
    @Getter @Setter private int kills;
    @Getter @Setter private int deaths;
    @Getter @Setter private int kothsCaptured;

    // Bunkers
    @Getter @Setter private int wins;
    @Getter @Setter private int losses;
    @Getter @Setter private int gamesPlayed;

    // Fasts
    @Getter @Setter private int citadelsCaptured;
    @Getter @Setter private int mapsPlayed;

    public Statistics(StatisticServer statisticServer) {
        this.statisticServer = statisticServer;
        this.kills = 0;
        this.deaths = 0;
        this.wins = 0;
        this.losses = 0;
        this.kothsCaptured = 0;
        this.citadelsCaptured = 0;
        this.gamesPlayed = 0;
        this.mapsPlayed = 0;
    }

    public void setStatistic(StatisticType statisticType, int amount) {
        switch (statisticType) {
            case KILLS:
                this.kills = amount;
            case WINS:
                this.wins = amount;
            case DEATHS:
                this.deaths = amount;
            case LOSSES:
                this.losses = amount;
            case MAPS_PLAYED:
                this.mapsPlayed = amount;
            case GAMES_PLAYED:
                this.gamesPlayed = amount;
            case KOTH_CAPTURES:
                this.kothsCaptured = amount;
            case CITADELS_CAPTURED:
                this.citadelsCaptured = amount;
        }
    }

    public void addStatistic(StatisticType statisticType, int amount) {
        switch (statisticType) {
            case KILLS:
                this.kills += amount;
                break;
            case WINS:
                this.wins += amount;
                break;
            case DEATHS:
                this.deaths += amount;
                break;
            case LOSSES:
                this.losses += amount;
                break;
            case MAPS_PLAYED:
                this.mapsPlayed += amount;
                break;
            case GAMES_PLAYED:
                this.gamesPlayed += amount;
                break;
            case KOTH_CAPTURES:
                this.kothsCaptured += amount;
                break;
            case CITADELS_CAPTURED:
                this.citadelsCaptured += amount;
                break;
        }
    }
}
