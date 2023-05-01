package net.frozenorb.foxtrot.gameplay.loot.battlepass.tier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.reward.Reward;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class Tier {

    private final int number;
    private final int requiredExperience;
    private Reward premiumReward;
    private Reward freeReward;

    public Tier newReward(boolean premium, Consumer<Reward> consumer) {
        if (premium) {
            premiumReward = new Reward(this);
            consumer.accept(premiumReward);
        } else {
            freeReward = new Reward(this);
            consumer.accept(freeReward);
        }
        return this;
    }

}
