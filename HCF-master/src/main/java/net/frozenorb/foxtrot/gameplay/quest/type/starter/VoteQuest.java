package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;

import java.util.ArrayList;
import java.util.List;

public class VoteQuest extends Quest {
    @Override
    public String getQuestID() {
        return "Vote";
    }

    @Override
    public String getQuestDisplayName() {
        return "Vote for the Server";
    }

    @Override
    public List<String> getDescription() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Type &c/vote &fand read the instructions for free keys!");

        return toReturn;
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 4;
    }
}
