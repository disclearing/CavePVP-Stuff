package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DepositMoneyQuest extends Quest {
    @Override
    public String getQuestID() {
        return "DepositMoney";
    }

    @Override
    public String getQuestDisplayName() {
        return "Deposit Money";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Deposit money into your faction");
        toReturn.add("by typing &c/f d all&f!");
        toReturn.add("Money in your faction is necessary");
        toReturn.add("to get a good claim.");

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
