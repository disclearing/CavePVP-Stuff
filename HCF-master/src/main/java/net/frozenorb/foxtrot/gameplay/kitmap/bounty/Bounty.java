package net.frozenorb.foxtrot.gameplay.kitmap.bounty;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Bounty implements Comparable<Bounty> {
    private final UUID placedBy;
    private final int gems;

    @Override
    public int compareTo(Bounty bounty) {
        return Integer.compare(gems, bounty.gems);
    }
}
