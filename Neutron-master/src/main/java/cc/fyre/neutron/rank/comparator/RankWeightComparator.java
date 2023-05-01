package cc.fyre.neutron.rank.comparator;

import cc.fyre.neutron.rank.Rank;

import java.util.Comparator;

public class RankWeightComparator implements Comparator<Rank> {

    @Override
    public int compare(Rank rank,Rank otherRank) {
        return Integer.compare(rank.getWeight().get(),otherRank.getWeight().get());
    }
}
