package cc.fyre.neutron.profile.attributes.grant.comparator;

import cc.fyre.neutron.profile.attributes.grant.Grant;

import java.util.Comparator;

public class GrantDateComparator implements Comparator<Grant> {

    @Override
    public int compare(Grant grant,Grant otherGrant) {
        return Long.compare(grant.getExecutedAt(),otherGrant.getExecutedAt());
    }
}