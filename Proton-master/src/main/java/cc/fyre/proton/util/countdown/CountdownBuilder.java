package cc.fyre.proton.util.countdown;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class CountdownBuilder {

    private final int seconds;
    private String message;
    private List<Integer> broadcastAt = new ArrayList<>();
    private Runnable tickHandler;
    private Runnable broadcastHandler;
    private Runnable finishHandler;
    private Predicate<Player> messageFilter;

    CountdownBuilder(int seconds) {
        Preconditions.checkArgument(seconds >= 0, "Seconds cannot must be greater than or equal to 0!");
        this.seconds = seconds;
    }

    public CountdownBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public CountdownBuilder broadcastAt(int amount, TimeUnit unit) {
        this.broadcastAt.add((int)unit.toSeconds((long)amount));
        return this;
    }

    public CountdownBuilder onTick(Runnable tickHandler) {
        this.tickHandler = tickHandler;
        return this;
    }

    public CountdownBuilder onBroadcast(Runnable broadcastHandler) {
        this.broadcastHandler = broadcastHandler;
        return this;
    }

    public CountdownBuilder onFinish(Runnable finishHandler) {
        this.finishHandler = finishHandler;
        return this;
    }

    public CountdownBuilder withMessageFilter(Predicate<Player> messageFilter) {
        this.messageFilter = messageFilter;
        return this;
    }

    public Countdown start() {
        Preconditions.checkNotNull(this.message, "Message cannot be null!");
        if (this.broadcastAt.isEmpty()) {
            this.broadcastAt(10, TimeUnit.MINUTES);
            this.broadcastAt(5, TimeUnit.MINUTES);
            this.broadcastAt(4, TimeUnit.MINUTES);
            this.broadcastAt(3, TimeUnit.MINUTES);
            this.broadcastAt(2, TimeUnit.MINUTES);
            this.broadcastAt(1, TimeUnit.MINUTES);
            this.broadcastAt(30, TimeUnit.SECONDS);
            this.broadcastAt(15, TimeUnit.SECONDS);
            this.broadcastAt(10, TimeUnit.SECONDS);
            this.broadcastAt(5, TimeUnit.SECONDS);
            this.broadcastAt(4, TimeUnit.SECONDS);
            this.broadcastAt(3, TimeUnit.SECONDS);
            this.broadcastAt(2, TimeUnit.SECONDS);
            this.broadcastAt(1, TimeUnit.SECONDS);
        }

        return new Countdown(this.seconds, this.message, this.tickHandler, this.broadcastHandler, this.finishHandler, this.messageFilter, this.convertIntegers(this.broadcastAt));
    }

    private int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];

        for(int i = 0; i < ret.length; ++i) {
            ret[i] = integers.get(i);
        }

        return ret;
    }


}
