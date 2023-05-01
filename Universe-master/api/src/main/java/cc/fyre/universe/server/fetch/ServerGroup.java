package cc.fyre.universe.server.fetch;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public enum ServerGroup {

    HUB("Hub"),
    HCF("HCF"),
    KIT_MAP("KitMap"),
    PRACTICE("Practice"),
    BUNKERS("Bunkers"),
    BUNKERS_LOBBY("Bunkers-Lobby"),
    UHC("UHC"),
    EARTH("EARTH"),
    ONE_BLOCK("ONEBLOCK"),
    MINI_GAMES("MINI-GAMES"),
    DEV("DEV");

    @Getter private String name;
}
