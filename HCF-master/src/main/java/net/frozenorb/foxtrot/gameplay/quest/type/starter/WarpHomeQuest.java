package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpHomeQuest extends Quest {
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
    public boolean canDoQuest(Player player) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return true;
        }

        return !team.isOwner(player.getUniqueId());
    }

    @Override
    public QuestType getQuestType() {
        return QuestType.STARTER;
    }

    @Override
    public int getWeight() {
        return 7;
    }
}
