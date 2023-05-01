package cc.fyre.neutron.profile.attributes.punishment.comparator;

import cc.fyre.neutron.profile.attributes.punishment.IPunishment;

import java.util.Comparator;

public class PunishmentDateComparator implements Comparator<IPunishment> {

    @Override
    public int compare(IPunishment iPunishment,IPunishment otherIPunishment) {
        return Long.compare(iPunishment.getExecutedAt(),otherIPunishment.getExecutedAt());
    }
}
