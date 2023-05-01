package net.frozenorb.foxtrot.gameplay.grounds;

import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.grounds.listener.GroundsListener;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

public class SpawnerGroundsHandler implements Listener {
    private Foxtrot instance;

    @Getter
    private List<GroundsArea> areas = new ArrayList<>();

    public SpawnerGroundsHandler(Foxtrot instance) {
        this.instance = instance;

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(),"net.frozenorb.foxtrot.gameplay.grounds.area")) {

                if (!GroundsArea.class.isAssignableFrom(clazz)) {
                    continue;
                }

                try {
                    final GroundsArea groundsArea = (GroundsArea)clazz.newInstance();
                    groundsArea.startGuardService();

                    System.out.println("Added " + groundsArea.getGroundsID());
                    this.areas.add(groundsArea);
                } catch (InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }, 20*5);

        this.instance.getServer().getPluginManager().registerEvents(new GroundsListener(), this.instance);
        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    public GroundsArea findArea(String id) {
        return this.areas.stream().filter(it -> it.getGroundsID().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onHour(HourEvent event) {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));

        final int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.SATURDAY) {
            this.resetAll();
        }

        if (day == Calendar.SUNDAY && event.getHour() % 2 == 0) {
            this.resetAll();
        }

        if ((day == Calendar.TUESDAY || day == Calendar.MONDAY) && event.getHour() % 3 == 0) {
            this.resetAll();
        }

        if ((day == Calendar.THURSDAY || day == Calendar.WEDNESDAY) && event.getHour() % 4 == 0) {
            this.resetAll();
        }
    }

    public void resetAll() {
        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &4&lSpawner Grounds"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7█████" + " &7All spawners have regenerated!"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7█" + "&4███" + "&7█"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7███" + "&4█" + "&7█ &cType &f/grounds"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &cto view all locations"));
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage("");
        }

        this.getAreas().forEach(GroundsArea::reset);
    }
}
