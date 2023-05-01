package cc.fyre.neutron.profile.attributes.api;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author xanderume@gmail (JavaProject)
 */
public interface Pardonable {

    UUID getPardoner();

    Long getPardonedAt();

    String getPardonedReason();

    default String getPardonedByFancyName() {

        if (this.getPardoner().equals(UUIDCache.CONSOLE_UUID)) {
            return NeutronConstants.CONSOLE_NAME;
        }

        final Player player = Neutron.getInstance().getServer().getPlayer(this.getPardoner());

        if (player != null) {
            return player.getDisplayName();
        }

        return Neutron.getInstance().getProfileHandler().fromUuid(this.getPardoner(),true).getFancyName();
    }

    boolean isPardoned();
}
