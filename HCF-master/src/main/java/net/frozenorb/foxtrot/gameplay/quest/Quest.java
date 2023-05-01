package net.frozenorb.foxtrot.gameplay.quest;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Quest implements Listener {
    public Quest() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public abstract String getQuestID();
    public abstract String getQuestDisplayName();
    public abstract List<String> getDescription();
    public abstract QuestType getQuestType();
    public abstract int getWeight();
    public boolean canDoQuest(Player player) {
        return true;
    }
    public void completeQuest(Player player) {

        if (!Foxtrot.getInstance().getQuestHandler().findCurrentQuest(player).getQuestID().equalsIgnoreCase(this.getQuestID())) {
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&lQuests"));
        player.sendMessage(ChatColor.translate("&7You have completed the &f" + this.getQuestDisplayName() + " &7quest!"));
        player.sendMessage("");

        final Quest nextQuest = Foxtrot.getInstance().getQuestHandler().findNextQuest(player);

        if (nextQuest != null) {
            player.sendMessage(ChatColor.translate("&a&l* NEW QUEST *"));
            player.sendMessage(ChatColor.translate("&4&l" + nextQuest.getQuestDisplayName() + " Quest"));
            for (String description : nextQuest.getDescription()) {
                player.sendMessage(ChatColor.GRAY + description);
            }

            Foxtrot.getInstance().getQuestHandler().getPlayerQuests().remove(player.getUniqueId());
            Foxtrot.getInstance().getQuestHandler().getPlayerQuests().put(player.getUniqueId(), nextQuest.getQuestID());
        }

        if (nextQuest == null || !nextQuest.getQuestType().equals(this.getQuestType())) {
            player.sendMessage("");
            player.sendMessage(ChatColor.translate("&aYou have completed all of the " + getQuestType().getDisplayName() + " quests!"));
            player.sendMessage("");

            Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), getQuestType().getWinningCommand().replace("{name}", player.getName()));
        }

    }

}
