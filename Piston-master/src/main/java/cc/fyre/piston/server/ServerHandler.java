package cc.fyre.piston.server;

import cc.fyre.piston.Piston;
import cc.fyre.piston.PistonConstants;
import cc.fyre.proton.Proton;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ServerHandler {

    @Getter @Setter private Piston instance;

    @Getter @Setter private boolean frozen;

    public ServerHandler(Piston instance) {
        this.instance = instance;

        this.frozen = false;
    }

    public void freeze(Player player) {

        player.setMetadata(PistonConstants.FREEZE_METADATA,new FixedMetadataValue(Proton.getInstance(),true));

        player.sendMessage(ChatColor.RED + "You have been frozen by a staff member.");

        this.instance.getServer().getScheduler().runTaskLater(Proton.getInstance(),() -> this.unfreeze(player.getUniqueId()), 20L * TimeUnit.HOURS.toSeconds(2L));

        final Location location = player.getLocation();

        int tries = 0;

        while (1.0 <= location.getY() && !location.getBlock().getType().isSolid() && tries++ < 100) {

            location.subtract(0.0, 1.0, 0.0);

            if (!(location.getY() <= 0.0)) {
                continue;
            }

        }

        if (100 <= tries) {
            this.instance.getLogger().info("Hit the 100 try limit on the freeze command.");
        }

        location.setY(location.getBlockY());

        player.teleport(location.add(0.0,1.0,0.0));
    }

    public void unfreeze(UUID uuid) {

        final Player player = this.instance.getServer().getPlayer(uuid);

        if (player == null) {
            return;
        }

        player.removeMetadata(PistonConstants.FREEZE_METADATA,Proton.getInstance());
        player.sendMessage(ChatColor.GREEN + "You have been unfrozen by a staff member.");
    }

}