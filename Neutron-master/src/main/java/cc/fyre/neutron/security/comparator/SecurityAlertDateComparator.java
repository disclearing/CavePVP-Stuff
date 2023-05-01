package cc.fyre.neutron.security.comparator;

import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.security.SecurityAlert;

import java.util.Comparator;

public class SecurityAlertDateComparator implements Comparator<SecurityAlert> {

    @Override
    public int compare(SecurityAlert securityAlert,SecurityAlert otherAlert) {
        return Long.compare(securityAlert.getTimeAt(),otherAlert.getTimeAt());
    }
}
