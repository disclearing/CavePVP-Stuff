package cc.fyre.proton.scoreboard.construct;

import org.bukkit.entity.Player;

import java.util.LinkedList;

public interface ScoreGetter {

    String[] getScores(LinkedList<String> linkedList,Player player);

}