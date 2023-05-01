package cc.fyre.proton.scoreboard;

import cc.fyre.proton.Proton;
import org.bukkit.entity.Player;

final class ScoreboardThread extends Thread {

    public static Integer UPDATE_INTERVAL = 2;

    public ScoreboardThread() {
        super("Proton - Scoreboard Thread");
        setDaemon(false);
    }

    public void run() {
        while (true) {

            for (Player online : Proton.getInstance().getServer().getOnlinePlayers()) {

                try {
                    Proton.getInstance().getScoreboardHandler().updateScoreboard(online);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            try {
                Thread.sleep(UPDATE_INTERVAL * 50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}