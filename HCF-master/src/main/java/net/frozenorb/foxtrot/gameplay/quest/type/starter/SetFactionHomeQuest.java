package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetFactionHomeQuest extends Quest {
    @Override
    public String getQuestID() {
        return "SetHome";
    }

    @Override
    public String getQuestDisplayName() {
        return "Set Faction Home";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Stand on your claim and type &c/f sethome");
        toReturn.add("You will be then allowed to teleport to it");
        toReturn.add("at any place, any time as long as you're not");
        toReturn.add("in Combat by simply typing &c/f home");

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
        return 6;
    }
}
