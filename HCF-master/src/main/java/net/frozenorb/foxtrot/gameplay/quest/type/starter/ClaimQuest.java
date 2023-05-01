package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClaimQuest extends Quest {
    @Override
    public String getQuestID() {
        return "Claim";
    }

    @Override
    public String getQuestDisplayName() {
        return "Get a Claim";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Type &c/wild &fto be teleported");
        toReturn.add("1,000 blocks down road where you can claim,");
        toReturn.add("then type &c/f claim &ffor the claim wand and");
        toReturn.add("find an open area that isn't unclaimed to take.");
        toReturn.add("Type &c/f map &fto view all nearby claims!");

        return toReturn;
    }

    @Override
    public boolean canDoQuest(Player player) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return false;
        }

        return team.isOwner(player.getUniqueId());
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 5;
    }
}
