package cc.fyre.neutron.profile.event;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.IPunishmentType;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.util.BukkitEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PunishmentEvent extends BukkitEvent {

    @Getter private Profile profile;
    @Getter private IPunishment punishment;

    public Player getPlayer() {
        return this.profile.getPlayer();
    }
}
