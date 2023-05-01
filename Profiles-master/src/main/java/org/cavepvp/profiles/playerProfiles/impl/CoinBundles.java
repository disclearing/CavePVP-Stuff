package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CoinBundles {
    ONE("", 100),
    TWO("", 200),
    THREE("", 300),
    FOUR("", 400),
    FIVE("", 500);

    @Getter String displayName;
    @Getter int coins;

}
