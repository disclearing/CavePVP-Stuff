package cc.fyre.neutron.profile.event;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.util.BukkitEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class GrantExpireEvent extends BukkitEvent {

    @Getter private Profile profile;
    @Getter private Grant grant;
    @Getter private Grant newGrant;

    public Player getPlayer() {
        return this.profile.getPlayer();
    }
}
