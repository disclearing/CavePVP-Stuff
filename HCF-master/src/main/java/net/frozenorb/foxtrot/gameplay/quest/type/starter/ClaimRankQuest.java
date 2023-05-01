package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import cc.fyre.neutron.listener.events.FreeRankEvent;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ClaimRankQuest extends Quest {
    @Override
    public String getQuestID() {
        return "ClaimRankStarter";
    }

    @Override
    public String getQuestDisplayName() {
        return "Claim Rank";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("To get started, be sure to claim");
        toReturn.add("a free rank by typing " + ChatColor.RED + "/freerank");
        toReturn.add("and reading the instructions!");

        return toReturn;
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @EventHandler
    private void onClaimRank(FreeRankEvent event) {
        completeQuest(event.getPlayer());
    }
}
