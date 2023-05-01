package cc.fyre.neutron.profile.attributes.grant.comparator;

import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.rank.comparator.RankWeightComparator;

import java.util.Comparator;

public class GrantWeightComparator implements Comparator<Grant> {

    @Override
    public int compare(Grant grant,Grant otherGrant) {
        return new RankWeightComparator().compare(grant.getRank(),otherGrant.getRank());
    }
}
