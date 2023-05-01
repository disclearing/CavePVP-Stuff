package cc.fyre.proton.util.countdown;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Countdown extends BukkitRunnable {

    private final String broadcastMessage;
    private final int[] broadcastAt;
    private final Runnable tickHandler;
    private final Runnable broadcastHandler;
    private final Runnable finishHandler;
    private final Predicate<Player> messageFilter;
    private int seconds;
    private boolean first = true;

    public static CountdownBuilder of(int amount, TimeUnit unit) {
        return new CountdownBuilder((int)unit.toSeconds((long)amount));
    }

    Countdown(int seconds, String broadcastMessage, Runnable tickHandler, Runnable broadcastHandler, Runnable finishHandler, Predicate<Player> messageFilter, int... broadcastAt) {
        this.seconds = seconds;
        this.broadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);
        this.broadcastAt = broadcastAt;
        this.tickHandler = tickHandler;
        this.broadcastHandler = broadcastHandler;
        this.finishHandler = finishHandler;
        this.messageFilter = messageFilter;
        this.runTaskTimer(Proton.getInstance(), 0L, 20L);
    }

    public final void run() {

        if (!this.first) {
            --this.seconds;
        } else {
            this.first = false;
        }

        for (int i = 0; i < this.broadcastAt.length; i++) {

            final int index = this.broadcastAt[i];

            if (this.seconds == index) {

                final String message = this.broadcastMessage.replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds));

                if (this.broadcastHandler != null) {
                    this.broadcastHandler.run();
                }

                for (Player loopPlayer : Proton.getInstance().getServer().getOnlinePlayers()) {

                    if (this.messageFilter != null && !this.messageFilter.test(loopPlayer)) {
                        loopPlayer.sendMessage(message);
                    }

                }
            }
        }

        if (this.seconds == 0) {

            if (this.finishHandler != null) {
                this.finishHandler.run();
            }

            this.cancel();
        } else if (this.tickHandler != null) {
            this.tickHandler.run();
        }

    }

    public int getSecondsRemaining() {
        return this.seconds;
    }
}
