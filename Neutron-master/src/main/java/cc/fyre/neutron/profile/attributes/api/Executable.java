package cc.fyre.neutron.profile.attributes.api;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface Executable {

    UUID getExecutor();

    Long getExecutedAt();

    String getExecutedReason();

    default String getExecutedByFancyName() {

        if (this.getExecutor().equals(UUIDCache.CONSOLE_UUID)) {
            return NeutronConstants.CONSOLE_NAME;
        }

        final Player player = Neutron.getInstance().getServer().getPlayer(this.getExecutor());

        if (player != null) {
            return player.getDisplayName();
        }

        return Neutron.getInstance().getProfileHandler().fromUuid(this.getExecutor(),true).getFancyName();
    }

}
