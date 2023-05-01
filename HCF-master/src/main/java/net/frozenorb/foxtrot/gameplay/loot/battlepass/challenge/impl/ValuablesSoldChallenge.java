package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class ValuablesSoldChallenge extends Challenge {

    private int amount;

    public ValuablesSoldChallenge(String id, String name, int experience, boolean daily, int amount) {
        super(id, name, experience, daily);

        this.amount = amount;
    }

    @Override
    public Type getAbstractType() {
        return ValuablesSoldChallenge.class;
    }

    @Override
    public String getText() {
        return "Sell $" + Formats.formatNumber(amount) + " worth of valuables";
    }

    @Override
    public boolean hasStarted(Player player) {
        return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getValuablesSold() > 0;
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getValuablesSold() >= amount;
    }

    @Override
    public String getProgressText(Player player) {
        int amount = Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getValuablesSold();
        int remaining = this.amount - amount;
        return "You need to sell $" + Formats.formatNumber(remaining) + " more worth of valuables.";
    }

}
