package net.frozenorb.foxtrot.gameplay.quest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum QuestType {
    STARTER("Starter", "airdrops give {player} 5"),
    INTERMEDIATE("Intermediate", "mcrate give {player} Seasonal 2"),
    ADVANCED("Advanced", "mcrate give {player} Seasonal 10");

    @Getter String displayName;
    @Getter String winningCommand;
}
