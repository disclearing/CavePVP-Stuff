package net.frozenorb.foxtrot.gameplay.extra.stats;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.command.StatsTopCommand;

import java.util.UUID;

public class StatsEntry {

    @Getter
    private final UUID owner;

    @Getter
    @Setter
    private int kills;

    @Getter
    @Setter
    private int deaths;

    @Getter
    @Setter
    private int killstreak;

    @Getter
    @Setter
    private int kothCaptures;

    @Getter
    @Setter
    private int caveSaysCompleted;

    @Getter
    private int highestKillstreak;

    public StatsEntry(UUID owner) {
        this.owner = owner;
    }

    public void addKill() {
        kills++;
        killstreak++;

        if (highestKillstreak < killstreak) {
            highestKillstreak = killstreak;
        }
    }

    public void addDeath() {
        deaths++;
        killstreak = 0;
        Foxtrot.getInstance().getKillstreakMap().setKillstreak(owner, 0);
    }

    public void addKothCapture() {
        kothCaptures++;
    }

    public void addCaveSaysCompleted() {
        caveSaysCompleted++;
    }

    public void clear() {
        kills = 0;
        deaths = 0;
        killstreak = 0;
        highestKillstreak = 0;
        kothCaptures = 0;
    }

    public double getKD() {
        if (getDeaths() == 0) {
            return 0;
        }

        return (double) getKills() / (double) getDeaths();
    }

    public Number get(StatsTopCommand.StatsObjective objective) {
        switch (objective) {
            case KILLS:
                return getKills();
            case DEATHS:
                return getDeaths();
            case KD:
                return getKD();
            case HIGHEST_KILLSTREAK:
                return getHighestKillstreak();
            case KOTH_CAPTURES:
                return getKothCaptures();
            case CAVE_SAYS_COMPLETED:
                return getCaveSaysCompleted();
            default:
                return 0;
        }
    }
}
