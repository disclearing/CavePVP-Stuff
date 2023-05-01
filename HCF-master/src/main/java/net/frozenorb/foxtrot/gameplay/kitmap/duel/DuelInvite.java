package net.frozenorb.foxtrot.gameplay.kitmap.duel;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public class DuelInvite {

    @NonNull private UUID sender;
    @NonNull private UUID target;
    @NonNull private int wager;

    private long sent = System.currentTimeMillis();

    public boolean hasExpired() {
        return System.currentTimeMillis() >= sent + TimeUnit.SECONDS.toMillis(30);
    }
}
