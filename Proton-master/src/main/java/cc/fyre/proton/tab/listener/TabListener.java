package cc.fyre.proton.tab.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.tab.construct.TabAdapter;
import cc.fyre.proton.tab.construct.TabLayout;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TabListener implements Listener {

    private static Field usernameField;

    static {
        try {
            usernameField = PacketPlayOutPlayerInfo.class.getDeclaredField("username");
            usernameField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Proton.getInstance().getTabHandler().addPlayer(event.getPlayer());
            }
        }.runTaskLater(Proton.getInstance(),10L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Proton.getInstance().getTabHandler().removePlayer(event.getPlayer());
        TabLayout.remove(event.getPlayer());
    }

}
