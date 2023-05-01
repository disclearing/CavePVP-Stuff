package cc.fyre.proton.scoreboard;

import cc.fyre.proton.Proton;
import cc.fyre.proton.scoreboard.config.ScoreboardConfiguration;
import lombok.Getter;
import lombok.Setter;
import cc.fyre.proton.scoreboard.listener.ScoreboardListener;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScoreboardHandler {

    @Getter private Map<String,Scoreboard> boards = new ConcurrentHashMap<>();
    @Getter @Setter private ScoreboardConfiguration configuration = null;

    public ScoreboardHandler() {
        new ScoreboardThread().start();
        Proton.getInstance().getServer().getPluginManager().registerEvents(new ScoreboardListener(), Proton.getInstance());
    }

    public void create(Player player) {
        if (configuration != null) {
            boards.put(player.getName(), new Scoreboard(player));
        }

    }

    public void updateScoreboard(Player player) {

        final Scoreboard board = boards.get(player.getName());

        if (board != null) {
            board.update();
        }

    }


    public void remove(Player player) {
        boards.remove(player.getName());
    }

}