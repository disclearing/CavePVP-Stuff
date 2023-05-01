package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import cc.fyre.neutron.listener.events.FreeRankEvent;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.listener.event.ReclaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ReclaimQuest extends Quest {
    @Override
    public String getQuestID() {
        return "Reclaim";
    }

    @Override
    public String getQuestDisplayName() {
        return "Reclaim";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Receive free keys by typing " + ChatColor.RED + "/reclaim");

        return toReturn;
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 1;
    }

    @EventHandler
    private void onReclaim(ReclaimEvent event) {
        completeQuest(event.getPlayer());
    }
}
