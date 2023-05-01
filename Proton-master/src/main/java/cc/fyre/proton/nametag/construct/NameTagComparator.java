package cc.fyre.proton.nametag.construct;

import cc.fyre.proton.nametag.provider.NameTagProvider;
import net.minecraft.util.com.google.common.primitives.Ints;

import java.util.Comparator;

public class NameTagComparator implements Comparator<NameTagProvider> {

    public int compare(NameTagProvider a,NameTagProvider b) {
        return Ints.compare(b.getWeight(), a.getWeight());
    }

}
