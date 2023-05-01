package net.frozenorb.foxtrot.listener;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.Proton;
import cc.fyre.proton.nametag.construct.NameTagInfo;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketServerRule;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.nethandler.client.obj.ServerRule;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointAdd;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.nametag.FoxtrotNametagProvider;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class LunarClientListener implements Listener {

    private Foxtrot instance;
    private World world;

    private LCWaypoint spawnWaypoint;

    public static Map<UUID, Map<String, Double>> teamViewer = new HashMap<>();

    public LunarClientListener(Foxtrot instance) {
        this.instance = instance;

        this.world = instance.getServer().getWorld("world");
        this.spawnWaypoint = new LCWaypoint(ChatColor.GREEN + "Spawn" + ChatColor.WHITE, this.world.getBlockAt(0, 76, 0).getLocation(), java.awt.Color.GREEN.getRGB(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onTeleport(PlayerTeleportEvent event) {
        if (DisableCommand.teamView || event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        this.sendTeamUpdatePacket(player, to);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onMove(PlayerMoveEvent event) {
        if (DisableCommand.teamView || event.isCancelled()) {
            return;
        }

        if (CustomTimerCreateCommand.isSOTWTimer()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        this.sendTeamUpdatePacket(player, to);
    }

    public void sendTeamUpdatePacket(Player player, Location to) {
        final Map<String, Double> coords = new HashMap<>();

        coords.put("x", to.getX());
        coords.put("y", to.getY() + 4);
        coords.put("z", to.getZ());

        if (teamViewer.containsKey(player.getUniqueId())) {
            teamViewer.replace(player.getUniqueId(), coords);
        } else {
            teamViewer.put(player.getUniqueId(), coords);
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return;
        }

        updateTeammates(player);

        team.getOnlineMembers().forEach(LunarClientListener::updateTeammates);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Team team = instance.getTeamHandler().getTeam(player);

        LunarClientAPI.getInstance().sendPacket(player, new LCPacketServerRule(ServerRule.LEGACY_COMBAT, true));

        updateNametag(player);

        final Location playerLocation = player.getLocation();

        if (!DisableCommand.teamView) {
            final Map<String, Double> coords = new HashMap<>();

            coords.put("x", playerLocation.getX());
            coords.put("y", playerLocation.getY() + 4);
            coords.put("z", playerLocation.getZ());

            teamViewer.put(player.getUniqueId(), coords);
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            LunarClientAPI.getInstance().sendWaypoint(player, this.spawnWaypoint);

            if (team == null) {
                return;
            }

            if (!DisableCommand.teamView) {
                team.getOnlineMembers().forEach(LunarClientListener::updateTeammates);
            }

            if (team.getHQ() != null) {
                if (team.getHomeWaypoint() == null) {
                    team.setHomeWaypoint(new LCWaypoint(ChatColor.BLUE + "HQ" + ChatColor.WHITE, team.getHQ(), java.awt.Color.BLUE.getRGB(), true));
                }

                LunarClientAPI.getInstance().sendWaypoint(player, team.getHomeWaypoint());
            }

            if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
                final Location location = team.getFocusedTeam().getHQ();

                if (team.getFocusWaypoint() == null) {
                    team.setFocusWaypoint(new LCWaypoint(ChatColor.RED + team.getFocusedTeam().getName() + "'s HQ" + ChatColor.WHITE, location, Color.RED.getRGB(), true));
                }

                LunarClientAPI.getInstance().sendWaypoint(player, team.getFocusWaypoint());
            }
        }, 40L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        final Player player = event.getPlayer();

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            LunarClientAPI.getInstance().resetNametag(player, onlinePlayer);
            LunarClientAPI.getInstance().resetNametag(onlinePlayer, player);
        }

        teamViewer.remove(player.getUniqueId());

        LunarClientAPI.getInstance().removeWaypoint(player, this.spawnWaypoint);

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team == null) {
            return;
        }

        if (team.getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, team.getHomeWaypoint());
        }

        if (team.getRallyPoint() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, team.getRallyWaypoint());
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(player, team.getFocusWaypoint());
        }
    }

    @EventHandler
    private void onPotion(PotionEffectAddEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (event.getEffect().getType().getName().equalsIgnoreCase("INVISIBILITY")) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> updateNametag(player), 5);
        }
    }

    @EventHandler
    private void onRemove(PotionEffectRemoveEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (event.getEffect().getType().getName().equalsIgnoreCase("INVISIBILITY")) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> updateNametag(player), 5);
        }
    }

    @EventHandler
    private void onExpire(PotionEffectExpireEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (event.getEffect().getType().getName().equalsIgnoreCase("INVISIBILITY")) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> updateNametag(player), 5);
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {

            if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                if (team != null && team.getRallyPoint() != null && team.getRallyPoint().getWorld().getEnvironment() == World.Environment.NETHER) {
                    if (team.getRallyWaypoint() == null) {
                        team.setRallyWaypoint(new LCWaypoint(ChatColor.GOLD + "Rally" + ChatColor.WHITE, team.getRallyPoint(), java.awt.Color.ORANGE.getRGB(), true));
                    }

                    LunarClientAPI.getInstance().sendWaypoint(player, team.getRallyWaypoint());
                }
            }

            if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
                return;
            }

            if (team != null && team.getRallyPoint() != null && team.getRallyPoint().getWorld().getEnvironment() == World.Environment.THE_END) {
                if (team.getRallyWaypoint() == null) {
                    team.setRallyWaypoint(new LCWaypoint(ChatColor.GOLD + "Rally" + ChatColor.WHITE, team.getRallyPoint(), java.awt.Color.ORANGE.getRGB(), true));
                }

                LunarClientAPI.getInstance().sendWaypoint(player, team.getRallyWaypoint());
            }
        }, 40L);
    }

    public static java.util.List<String> fetchNameTag(Player target, Player viewer) {
        final NameTagInfo nameTagInfo = new FoxtrotNametagProvider().fetchNameTag(target, viewer);
        String nameTag = nameTagInfo.getPrefix() + target.getDisguisedName();
        List<String> tag = new ArrayList<>();

        if (ModHandler.INSTANCE.isInModMode(target.getUniqueId())) {
            tag.add(ChatColor.GRAY + "[Mod Mode]");
            tag.add(nameTag);
            return tag;
        }

        if (target.hasMetadata("SHOW_HEARTS")) {
            double hearts = Math.round(target.getHealth());
            hearts /= 2;

            tag.add(ChatColor.translate("&c" + hearts + " Hearts"));
        }

        if (Foxtrot.getInstance().getBountyManager() != null && Foxtrot.getInstance().getBountyManager().getBounty(target) != null) {
            tag.add(ChatColor.RED + "Bounty: " + ChatColor.GREEN + Foxtrot.getInstance().getBountyManager().getBounty(target).getGems() + " Gems");
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (team == null) {
            return Collections.singletonList(nameTag);
        }

        String dtrFormat = team.formatDTR();

        if (Foxtrot.getInstance().getDTRDisplayMap().isHearts(viewer.getUniqueId())) {
            dtrFormat = team.getDTRColor().toString() + ((int) Math.ceil(team.getDTR())) + "❤" + team.getDTRSuffix();
        }

        tag.add(ChatColor.GOLD + "[" + team.getName(viewer) + " " + dtrFormat + ChatColor.GOLD + "]");

        tag.add(nameTag);
        return tag;
    }

    public static void updateTeammates(Player viewer) {
        if (DisableCommand.teamView) {
            return;
        }

        if (!LunarClientAPI.getInstance().isRunningLunarClient(viewer)) {
            return;
        }

        if (!Foxtrot.getInstance().getLcTeamViewMap().isLCTeamView(viewer.getUniqueId())) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(viewer);

        final Map<UUID, Map<String, Double>> players = new HashMap<>();

        if (team == null) {
            players.put(viewer.getUniqueId(), teamViewer.get(viewer.getUniqueId()));
            LunarClientAPI.getInstance().sendTeammates(viewer, new LCPacketTeammates(viewer.getUniqueId(), 10, players));
            return;
        }


        for (Player it : team.getOnlineMembers()) {
            if (!it.getWorld().getName().equalsIgnoreCase(viewer.getWorld().getName())) {
                continue;
            }

            players.put(it.getUniqueId(), teamViewer.get(it.getUniqueId()));
        }

        LunarClientAPI.getInstance().sendTeammates(viewer, new LCPacketTeammates(viewer.getUniqueId(), 10, players));
    }

    public static void updateNametag(Player player) {
        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            updateTeammates(player);
        }

        Proton.getInstance().getNameTagHandler().reloadOthersFor(player);
        Proton.getInstance().getNameTagHandler().reloadPlayer(player);

        Foxtrot.getInstance().getServer().getOnlinePlayers().forEach(it -> {
            final List<String> loopPlayerNameTag = fetchNameTag(it, player);

            LunarClientAPI.getInstance().overrideNametag(it, loopPlayerNameTag, player);

            final List<String> targetNameTag = fetchNameTag(player, it);

            LunarClientAPI.getInstance().overrideNametag(player, targetNameTag, it);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onRespawn(PlayerRespawnEvent event) {
        updateNametag(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDeath(PlayerDeathEvent event) {
        updateNametag(event.getEntity());
    }
}