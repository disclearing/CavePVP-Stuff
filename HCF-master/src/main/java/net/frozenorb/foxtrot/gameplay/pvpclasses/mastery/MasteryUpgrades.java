package net.frozenorb.foxtrot.gameplay.pvpclasses.mastery;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MasteryUpgrades {
    LEVEL_ZERO("Level 0", 0, 0, 0 , 0),
    LEVEL_ONE("Level 1", 250, 250, 0, 1),
    LEVEL_TWO("Level 2", 500, 500, 0, 2),
    LEVEL_THREE("Level 3", 750, 1000, 0, 3),
    LEVEL_FOUR("Level 4", 1000, 1500, 0, 4),
    LEVEL_FIVE("Level 5", 1500, 2500, 5, 5);

    @Getter String displayName;
    @Getter int scoreToLevelUp;
    @Getter int gemPrize;
    @Getter int treasureChests;
    @Getter int priority;

    public static MasteryUpgrades getUpgradeByScore(int score) {
        MasteryUpgrades chosen = null;

        for (MasteryUpgrades value : values()) {

            if (value.scoreToLevelUp > score) {
                continue;
            }

            if (chosen != null && chosen.priority > value.priority) {
                continue;
            }

            chosen = value;
        }

        return chosen;
    }

}
