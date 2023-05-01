package cc.fyre.universe.server.fetch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public enum ServerStatus {

    ONLINE("Online", ChatColor.GREEN + "Online"),
    OFFLINE("Offline", ChatColor.RED + "Offline"),
    WHITELISTED("Whitelisted", ChatColor.WHITE + "Whitelisted");

    @Getter private String name;
    @Getter private String displayName;
}
