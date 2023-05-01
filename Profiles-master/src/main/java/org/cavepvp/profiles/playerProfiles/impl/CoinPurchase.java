package org.cavepvp.profiles.playerProfiles.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
public class CoinPurchase {
    @Getter private UUID buyer;
    @Getter @Setter private UUID executor;
    @Getter @Setter private String txId;
    @Getter @Setter private long purchasedAt;
    @Getter @Setter private int coinsSpent;
    @Getter @Setter private String type;
    @Getter @Setter private int coinsPurchased;
    @Getter @Setter private boolean voucher;
    @Getter @Setter private double amountSpent;

    public int coinsRemaining() {
        return coinsPurchased-coinsSpent;
    }

    public double getCostPerCoin() {
        return amountSpent/coinsPurchased;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final CoinPurchase coinPurchase = (CoinPurchase) object;

        return txId.equals(coinPurchase.getTxId()) && coinPurchase.getCoinsPurchased() == coinsPurchased && coinPurchase.getBuyer().toString().equalsIgnoreCase(buyer.toString());
    }


}
