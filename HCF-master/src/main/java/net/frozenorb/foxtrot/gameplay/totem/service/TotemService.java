package net.frozenorb.foxtrot.gameplay.totem.service;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.totem.Totem;
import net.frozenorb.foxtrot.gameplay.totem.TotemHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class TotemService extends BukkitRunnable {
    private Foxtrot instance;
    private TotemHandler totemHandler;

    @Override
    public void run() {
        for (Map.Entry<UUID, Totem> entry : new HashMap<>(this.totemHandler.getCache()).entrySet()) {

            if (entry.getValue().getExpiresAt() <= System.currentTimeMillis()) {
                totemHandler.getCache().remove(entry.getKey());
                final Location location = entry.getValue().getLocation();

                location.getBlock().setType(Material.AIR);
                location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                continue;
            }

            final Team placedBy = this.instance.getTeamHandler().getTeam(entry.getValue().getPlacedBy());

            if (placedBy == null) {
                continue;
            }

            for (Player onlineMember : placedBy.getOnlineMembers()) {

                if (!onlineMember.getLocation().getWorld().getName().equalsIgnoreCase(entry.getValue().getLocation().getWorld().getName())) {
                    continue;
                }

                onlineMember.addPotionEffect(new PotionEffect(entry.getValue().getPotionEffectType(), 20*4, 0), true);
            }
        }
    }
}
