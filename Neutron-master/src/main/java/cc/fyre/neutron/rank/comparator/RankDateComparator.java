package cc.fyre.neutron.rank.comparator;

import cc.fyre.neutron.rank.Rank;

import java.util.Comparator;

public class RankDateComparator implements Comparator<Rank> {

    @Override
    public int compare(Rank rank,Rank otherRank) {
        return Long.compare(rank.getWeight().get(),otherRank.getWeight().get());
    }

}
