package net.frozenorb.foxtrot.gameplay.loot.battlepass.reward;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class Reward {

    private final UUID uuid = UUID.randomUUID();
    private final Tier tier;
    private List<String> text = new ArrayList<>();
    private List<String> commands = new ArrayList<>();

    public Reward addText(String... lines) {
        text.addAll(Arrays.asList(lines));
        return this;
    }

    public Reward addCommand(String command) {
        commands.add(command);
        return this;
    }

    public boolean isFreeReward() {
        return tier.getFreeReward() == this;
    }

    public boolean hasClaimed(BattlePassProgress progress) {
        return (isFreeReward() && progress.getClaimedRewardsFree().contains(tier)) || (!isFreeReward() && progress.getClaimedRewardsPremium().contains(tier));
    }

    public void execute(Player player) {
        for (String command : commands) {
            String processedCommand = command
                    .replace("{playerName}", player.getName())
                    .replace("{playerUuid}", player.getUniqueId().toString())
                    .replace("{tier}", String.valueOf(tier.getNumber()));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reward reward = (Reward) o;
        return uuid == reward.uuid && tier.getNumber() == reward.tier.getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, text, commands);
    }

}
