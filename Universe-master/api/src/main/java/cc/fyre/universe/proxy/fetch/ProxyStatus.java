package cc.fyre.universe.proxy.fetch;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public enum ProxyStatus {

    ONLINE("Online"),
    OFFLINE("Offline");

    @Getter private String name;
}
