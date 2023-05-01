package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class WrappedBalanceMap extends PersistMap<Double> {

    public WrappedBalanceMap() {
        super("WrappedBalances", "Balance");
        //Basic.get().getEconomyManager().registerListener(new FoxEconomyListener());
    }

    @Override
    public String getRedisValue(Double balance) {
        return (String.valueOf(balance));
    }

    @Override
    public Double getJavaObject(String str) {
        return (Double.parseDouble(str));
    }

    @Override
    public Object getMongoValue(Double balance) {
        return (balance);
    }

    public double getBalance(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setBalance(UUID update, double balance) {
        updateValueAsync(update, balance);
    }

}