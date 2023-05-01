package cc.fyre.universe.proxy.fetch;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public enum ProxyRegion {

    EUROPE("Europe","EU"),
    AUSTRALIA("Australia","AU"),
    UNITED_STATES("United States","US");

    @Getter private String name;
    @Getter private String shortName;
}
