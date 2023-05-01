package cc.fyre.neutron.prefix.comparator;

import cc.fyre.neutron.prefix.Prefix;

import java.util.Comparator;

public class PrefixWeightComparator implements Comparator<Prefix> {

    @Override
    public int compare(Prefix prefix,Prefix otherPrefix) {
        return Integer.compare(prefix.getWeight().get(),otherPrefix.getWeight().get());
    }

}
