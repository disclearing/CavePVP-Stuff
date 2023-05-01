package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

public class AntiCleanListener implements Listener {

    public static final Map<Team, Team> CACHE = new HashMap<>();
    public static final Map<Team, Long> TIME_CACHE = new HashMap<>();

    @EventHandler
    private void onEntityDamageByEntityNormal(EntityDamageByEntityEvent event) {
        if (!DisableCommand.antiClean) return;
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        Player victim = (Player) event.getEntity();

        if (damager == null) return;
        if (damager == victim) return;

        final Team victimTeam = Foxtrot.getInstance().getTeamHandler().getTeam(victim);
        final Team damagerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(damager);

        World.Environment env = victim.getWorld().getEnvironment();

        if (env == World.Environment.NORMAL) return;

        boolean ignore = false;

        for (Event e : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (e.getType() != EventType.KOTH) continue;
            KOTH koth = (KOTH) e;
            if (!koth.isActive()) continue;

            if ((env == World.Environment.NETHER && koth.getWorld().equals("world_nether"))
                    || (env == World.Environment.THE_END && koth.getWorld().equals("world_the_end"))) {
                ignore = true;
                break;
            }
        }

        if (ignore) return;

        // No need to check TIME_CACHE here because FoxtrotScoreGetter removes the team when the anti-clean tag expires
        // which is pretty ugly but that's how SimplyTrash did it, and it works ¯\_(ツ)_/¯

        Team victimOpponentTeam = victimTeam == null ? null : CACHE.get(victimTeam);
        Team damagerOpponentTeam = damagerTeam == null ? null : CACHE.get(damagerTeam);

        if (victimOpponentTeam != null && victimOpponentTeam != damagerTeam) {
            damager.sendMessage(ChatColor.translate("&cYou may not hit &f" + victim.getName() + " &cas they have an &5&lAnti-Clean &ctag!"));
            event.setCancelled(true);
        } else if (damagerOpponentTeam != null && damagerOpponentTeam != victimTeam) {
            damager.sendMessage(ChatColor.translate("&cYou may not hit &f" + victim.getName() + " &cas you have an &5&lAnti-Clean &ctag and they aren't the faction you were fighting!"));
            event.setCancelled(true);
        }

        damager.setMetadata("anti_clean", new FixedMetadataValue(Foxtrot.getInstance(), true));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = PlayerUtils.getDamageSource(event.getDamager());
        if (damager == null) return;

        if (!damager.hasMetadata("anti_clean")) return;

        damager.removeMetadata("anti_clean", Foxtrot.getInstance());

        Player victim = (Player) event.getEntity();

        final Team victimTeam = Foxtrot.getInstance().getTeamHandler().getTeam(victim);
        final Team damagerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(damager);

        if (victimTeam == null) return;
        if (damagerTeam == null) return;

        if (4 > victimTeam.getOnlineMembers().size()) return;
        if (4 > damagerTeam.getOnlineMembers().size()) return;

        if (!CACHE.containsKey(damagerTeam)) {
            damagerTeam.sendMessage(ChatColor.translate("&5&lAnti-Clean &ctag has been activated because &f" + damager.getName() + " &chit &f[" + victimTeam.getName() + "] " + victim.getName() + "&c!"));
        }

        if (!CACHE.containsKey(victimTeam)) {
            victimTeam.sendMessage(ChatColor.translate("&5&lAnti-Clean &ctag has been activated because &f" + victim.getName() + " &cwas hit by &f[" + damagerTeam.getName() + "] " + damager.getName() + "&c!"));
        }

        CACHE.put(damagerTeam, victimTeam);
        CACHE.put(victimTeam, damagerTeam);

        TIME_CACHE.put(damagerTeam, System.currentTimeMillis() + 60000);
        TIME_CACHE.put(victimTeam, System.currentTimeMillis() + 60000);
    }
}
