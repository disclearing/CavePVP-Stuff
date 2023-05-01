package net.frozenorb.foxtrot.gameplay.events.mini;

import cc.fyre.proton.event.HalfHourEvent;
import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.type.*;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.King;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.Shooter;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.Stabber;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MiniEventsHandler implements Listener {
    private Foxtrot instance;

    @Getter
    private List<MiniEvent> events = new ArrayList<>();

    @Getter @Setter
    private MiniEvent activeEvent;

    public MiniEventsHandler(Foxtrot instance) {
        this.instance = instance;

        if (this.instance.getMapHandler().isKitMap()) {
            this.events.add(new King());
            this.events.add(new Shooter());
            this.events.add(new Stabber());
        } else {
            this.events.add(new MiningFrenzy());
            this.events.add(new Raider());
        }
        this.events.add(new Traveller());
        this.events.add(new Rage());
        this.events.add(new Mayhem());

        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    public MiniEvent findEvent(String id) {
        return this.events.stream().filter(it -> it.getEventID().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onHour(HourEvent event) {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis()));

        final int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {

            if (event.getHour() != 16) {
                return;
            }

            if (day == Calendar.SUNDAY) {
                this.activeEvent = this.findEvent("Rage");
            }

            if (day == Calendar.MONDAY) {
                this.activeEvent = this.findEvent("King");
            }

            if (day == Calendar.TUESDAY) {
                this.activeEvent = this.findEvent("Mayhem");
            }

            if (day == Calendar.WEDNESDAY) {
                this.activeEvent = this.findEvent("Traveller");
            }

            if (day == Calendar.THURSDAY) {
                this.activeEvent = this.findEvent("Stabber");
            }

            if (day == Calendar.FRIDAY) {
                this.activeEvent = this.findEvent("Shooter");
            }

            this.activeEvent.activate();
            return;
        }

        // Sunday 3 PM EST
        if (day == Calendar.SUNDAY && event.getHour() == 15) {
            this.activeEvent = this.findEvent("Rage");
        }

        // Monday 4 PM EST
        if (day == Calendar.MONDAY && event.getHour() == 16) {
            this.activeEvent = this.findEvent("Raider");
        }

        // Tuesday 12 PM EST
        if (day == Calendar.TUESDAY && event.getHour() == 12) {
            this.activeEvent = this.findEvent("Mayhem");
        }

        // Wednesday 4 PM EST
        if (day == Calendar.WEDNESDAY && event.getHour() == 16) {
            this.activeEvent = this.findEvent("Traveller");
        }

        // Thursday 2 PM EST
        if (day == Calendar.THURSDAY && event.getHour() == 14) {
            this.activeEvent = this.findEvent("Rage");
        }

        if (this.activeEvent == null) {
            return;
        }

        this.activeEvent.activate();
    }

    public void activateRandom() {
        this.activeEvent = this.events.get(ThreadLocalRandom.current().nextInt(this.events.size()));
        this.activeEvent.activate();
    }
}

