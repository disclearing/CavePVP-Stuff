package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.listener.event.ReclaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class CreateOrJoinFactionQuest extends Quest {
    @Override
    public String getQuestID() {
        return "CreateOrJoin";
    }

    @Override
    public String getQuestDisplayName() {
        return "Create or Join a Faction";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Create a faction by typing " + ChatColor.RED + "/f create");
        toReturn.add("Or if you already have an");
        toReturn.add("invite, type &c/f join [name]&7!");

        return toReturn;
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
