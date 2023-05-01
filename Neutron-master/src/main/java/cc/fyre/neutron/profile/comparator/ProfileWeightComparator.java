package cc.fyre.neutron.profile.comparator;

import cc.fyre.neutron.profile.Profile;

import java.util.Comparator;

public class ProfileWeightComparator implements Comparator<Profile> {

    @Override
    public int compare(Profile profile,Profile otherProfile) {
        return Integer.compare(profile.getActiveGrant().getRank().getWeight().get(),otherProfile.getActiveGrant().getRank().getWeight().get());
    }

}
