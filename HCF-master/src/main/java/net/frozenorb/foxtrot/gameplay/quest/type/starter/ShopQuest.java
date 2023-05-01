package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.listener.event.ReclaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class ShopQuest extends Quest {
    @Override
    public String getQuestID() {
        return "Shop";
    }

    @Override
    public String getQuestDisplayName() {
        return "Shop";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Open up the shop by typing &c/shop");
        toReturn.add("You should also open any keys you have");
        toReturn.add("and sell all the valuables you gain in the &c/shop");

        return toReturn;
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 2;
    }
}
