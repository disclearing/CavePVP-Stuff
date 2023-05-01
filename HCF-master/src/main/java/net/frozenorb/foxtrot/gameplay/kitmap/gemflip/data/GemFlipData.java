package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class GemFlipData {

    private AtomicInteger won, lost;

    public GemFlipData(int won, int lost) {
        this.won = new AtomicInteger(won);
        this.lost = new AtomicInteger(lost);
    }

    public double getRatio() {
        if(lost.get() == 0)
            return won.get();

        return won.get() / lost.doubleValue();
    }

    public boolean isEmpty() {
        return won.get() <= 0 && lost.get() <= 0;
    }
}
