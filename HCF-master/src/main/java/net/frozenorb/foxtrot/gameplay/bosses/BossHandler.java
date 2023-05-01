package net.frozenorb.foxtrot.gameplay.bosses;

import cc.fyre.proton.event.HalfHourEvent;
import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BossHandler implements Listener {
    private Foxtrot instance;
    @Getter private List<Boss> bosses = new ArrayList<>();
    @Getter @Setter private Boss activeBoss;

    public BossHandler(Foxtrot instance) {
        this.instance = instance;

        for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(),"net.frozenorb.foxtrot.gameplay.bosses.type")) {

            if (!Boss.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                this.bosses.add((Boss)clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onHour(HalfHourEvent event) {
        if (event.getHour() % 5 != 0) {
            return;
        }

        if (event.getMinute() > 20) {
            this.activateRandom();
        }
    }

    public Boss findBoss(String id) {
        return this.bosses.stream().filter(it -> it.getBossID().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public void activateRandom() {
        this.activeBoss = bosses.get(ThreadLocalRandom.current().nextInt(bosses.size()));
        this.activeBoss.activate();
    }
}
