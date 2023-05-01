package net.frozenorb.foxtrot.gameplay.cavesays;

import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.util.ClassUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CaveSaysHandler implements Listener {
    private Foxtrot instance;

    @Getter private List<Task> tasks = new ArrayList<>();

    @Getter @Setter private Task activeTask;

    public CaveSaysHandler(Foxtrot instance) {
        this.instance = instance;

        for (Class<?> clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(),"net.frozenorb.foxtrot.gameplay.cavesays.type")) {

            if (!Task.class.isAssignableFrom(clazz)) {
                continue;
            }

            try {
                this.tasks.add((Task)clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
    }

    public Task findTask(String id) {
        return this.tasks.stream().filter(it -> it.getTaskID().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onHour(HourEvent event) {
        if (event.getHour() % 2 == 0) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, this::activateRandom, 20*40);
        }
    }

    public void activateRandom() {
        this.activeTask = tasks.get(ThreadLocalRandom.current().nextInt(tasks.size()));
        this.activeTask.activate();
    }
}
