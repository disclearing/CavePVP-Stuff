package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.visit;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;

public class VisitEndChallenge extends Challenge {

    public VisitEndChallenge(String id, String name, int experience, boolean daily) {
        super(id, name, experience, daily);
    }

    @Override
    public Type getAbstractType() {
        return VisitEndChallenge.class;
    }

    @Override
    public String getText() {
        return "Visit the End";
    }

    @Override
    public boolean meetsCompletionCriteria(Player player) {
        return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).isVisitedEnd();
    }

}
