package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

@Getter
public class UsePartnerItemChallenge extends Challenge {

    private int uses;

    public UsePartnerItemChallenge(String id, String name, int experience, boolean daily, int uses) {
        super(id, name, experience, daily);

        this.uses = uses;
    }

    @Override
    public Type getAbstractType() {
        return UsePartnerItemChallenge.class;
    }

    @Override
    public String getText() {
        return "Use " + uses + " Partner Items";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getPartnerItemsUsed() >= uses;
    }

    @Override
    public String getProgressText(Player player) {
        int amount = Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).getPartnerItemsUsed();
        int remaining = uses - amount;
        return "You need to use " + Formats.formatNumber(remaining) + " more Partner Items.";
    }

}
