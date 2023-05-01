package net.frozenorb.foxtrot.gameplay.quest;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class QuestHandler {
    private Foxtrot instance;

    @Getter private List<Quest> quests = new ArrayList<>();
    @Getter private Map<UUID, String> playerQuests = new HashMap<>();
    @Getter private Map<UUID, Integer> questStatus = new HashMap<>();

    public QuestHandler(Foxtrot instance) {
        this.instance = instance;
    }

    public Quest findCurrentQuest(Player player) {
        if (playerQuests.containsKey(player.getUniqueId())) {
            return this.findQuestByName(playerQuests.get(player.getUniqueId()));
        }

        return this.findQuestByName("ClaimRankStarter");
    }

    public List<Quest> findQuestByWeight(int weight) {
        return this.quests.stream().filter(it -> it.getWeight() == weight).collect(Collectors.toList());
    }

    public void completeQuest(Player player, String questId) {
//        final Quest currentQuest = this.findCurrentQuest(player);
//
//        if (currentQuest.getQuestID().equalsIgnoreCase(questId)) {
//            currentQuest.completeQuest(player);
//        }
    }

    public Quest findNextQuest(Player player) {
        final Quest currentQuest = this.findCurrentQuest(player);

        int nextWeight = currentQuest.getWeight()+1;

        for (Quest loopQuest : this.findQuestByWeight(nextWeight)) {

            if (loopQuest.getWeight() != nextWeight) {
                continue;
            }

            if (loopQuest.canDoQuest(player)) {
                return loopQuest;
            }
        }

        return null;
    }

    public Quest findQuestByName(String name) {
        return this.quests.stream().filter(it -> it.getQuestID().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
