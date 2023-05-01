package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SpawnerType {
    ZOMBIE("Zombie"),
    SKELETON("Skeleton"),
    SPIDER("Spider"),
    CAVE_SPIDER("Cave Spider");

    @Getter String displayName;

}
