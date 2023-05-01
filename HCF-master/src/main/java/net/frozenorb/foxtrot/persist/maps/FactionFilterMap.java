package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.FilterType;

import java.util.UUID;

public class FactionFilterMap extends PersistMap<FilterType> {

    public FactionFilterMap() {
        super("FactionFilter", "FactionFilter");
    }

    @Override
    public String getRedisValue(FilterType filterType) {
        return (filterType.name());
    }

    @Override
    public FilterType getJavaObject(String str) {
        return (FilterType.valueOf(str));
    }

    @Override
    public Object getMongoValue(FilterType filterType) {
        return (filterType.name());
    }

    public FilterType getFilterType(UUID check) {
        return (contains(check) ? getValue(check) : FilterType.LOWEST_DTR);
    }

    public void setFilterType(UUID update, FilterType filterType) {
        updateValueAsync(update, filterType);
    }

}