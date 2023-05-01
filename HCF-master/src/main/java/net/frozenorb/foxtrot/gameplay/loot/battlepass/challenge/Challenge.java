package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@AllArgsConstructor
@Getter
public abstract class Challenge {

    private String id;
    private String name;
    private int experience;
    private boolean daily;

    public abstract Type getAbstractType();

    public abstract String getText();

    public boolean hasStarted(Player player) {
        return false;
    }

    public abstract boolean meetsCompletionCriteria(Player player);

    public String getProgressText(Player player) {
        return null;
    }

    public BattlePassProgress getProgress(Player player) {
        return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
    }

    public void completed(Player player, BattlePassProgress progress) {
        final String formattedExperience = Formats.formatNumber(experience);
        player.sendMessage(BattlePassHandler.CHAT_PREFIX + CC.YELLOW + "You've completed the " + CC.GOLD + name + " " + CC.YELLOW + (daily ? "daily" : "premium") + " challenge! " + CC.GRAY + "(" + CC.GREEN + "+" + formattedExperience + " XP" + CC.GRAY + ")");

        final Tier currentTier = progress.getCurrentTier();

        progress.completeChallenge(this);

        final Tier newTier = progress.getCurrentTier();

        if (!currentTier.equals(newTier)) {
            boolean newRewards = newTier.getFreeReward() != null;

            if (newTier.getPremiumReward() != null && progress.isPremium()) {
                newRewards = true;
            }

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

            if (newRewards) {
                player.sendMessage(BattlePassHandler.CHAT_PREFIX + "Congratulations! You've reached Tier " + newTier.getNumber() + "! You have new rewards waiting to be collected.");
            } else {
                player.sendMessage(BattlePassHandler.CHAT_PREFIX + "Congratulations! You've reached Tier " + newTier.getNumber() + "!");
            }
        }
    }

}
