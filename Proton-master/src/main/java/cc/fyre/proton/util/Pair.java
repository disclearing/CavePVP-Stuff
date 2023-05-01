package cc.fyre.proton.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xanderume@gmail (JavaProject)
 */

@AllArgsConstructor
public class Pair<K,V>{

    @Getter private K key;
    @Getter private V value;

}
