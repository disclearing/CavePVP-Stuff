package cc.fyre.neutron.prevention;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prevention.impl.Prevention;
import cc.fyre.neutron.prevention.packets.PreventionCreatePacket;
import cc.fyre.neutron.prevention.packets.PreventionResolvePacket;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.Proton;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PreventionListener implements Listener {
    private String noseeperm = "neutron.coreprotect.protect";
    private List<String> blockedCommands = new ArrayList<>();
    public PreventionListener() {
        blockedCommands.add("/coreprotect:core lookup");
        blockedCommands.add("/coreprotect:core l");
        blockedCommands.add("/coreprotect:co lookup");
        blockedCommands.add("/coreprotect:co l");
        blockedCommands.add("/coreprotect:coreprotect lookup");
        blockedCommands.add("/coreprotect:coreprotect l");
        blockedCommands.add("/coreprotect lookup");
        blockedCommands.add("/coreprotect l");
        blockedCommands.add("/co lookup");
        blockedCommands.add("/co l");
        blockedCommands.add("/core lookup");
        blockedCommands.add("/core l");
    }
    @EventHandler
    private void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        String command = event.getMessage();
        boolean checked = false;
        for(String bc : blockedCommands) {
            if(command.contains(bc)) {
                checked = true;
            }
        }//Yes i know theres a better way to do this

        boolean isCommandLog = false;
        if(checked) {
            String[] args = command.split(":");
            for(String s : args) {
                for(String ss : s.split(" ")) {
                    if (ss.contains("command")) {
                        isCommandLog = true;
                    } else {
                        isCommandLog = true;
                        boolean go = false;
                        for (String bc : blockedCommands) {
                            if (!ss.contains(bc)) {
                                go = true;
                            }
                        }
                        if (go) {
                            UUID uuid = Proton.getInstance().getUuidCache().uuid(ss);
                            if(uuid != null) {
                                final Profile target = Neutron.getInstance().getProfileHandler().fromUuid(
                                        uuid, true);
                                if (target != null && isCommandLog) {
                                    if (target.getEffectivePermissions().contains(noseeperm) && !player.hasPermission(noseeperm)) {
                                        event.setCancelled(true);
                                        player.sendMessage(ChatColor.RED + "No Permission");
                                        Bukkit.getScheduler().runTaskAsynchronously(Neutron.getInstance(), new Runnable() {
                                            @Override
                                            public void run() {
                                                Proton.getInstance().getPidginHandler().sendPacket(new PreventionCreatePacket(player.getUniqueId(),
                                                        command, System.currentTimeMillis(), false));
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPLayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission(noseeperm) && Neutron.getInstance().getPreventionHandler().shouldAlert()) {
            new FancyMessage(ChatColor.GREEN + "You have an unresolved alert").command("/unresolvedissues").send(player);
        }
    }
}
