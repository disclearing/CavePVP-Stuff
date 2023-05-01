package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import cc.fyre.neutron.listener.events.FreeRankEvent;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class RedeemQuest extends Quest {
    @Override
    public String getQuestID() {
        return "Redeem";
    }

    @Override
    public String getQuestDisplayName() {
        return "Redeem Creator";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Which YouTuber/Streamer/Tiktoker lead you to Cave?");
        toReturn.add("Type &c/redeem [their name] &fto receive free keys!");

        return toReturn;
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 3;
    }
}
