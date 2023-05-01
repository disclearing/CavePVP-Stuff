package net.frozenorb.foxtrot.gameplay.totem;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.totem.listener.TotemListener;
import net.frozenorb.foxtrot.gameplay.totem.service.TotemService;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;

import java.util.*;

public class TotemHandler {
    private Foxtrot instance;
    @Getter private Map<UUID, Totem> cache = new HashMap<>();
    @Getter private Map<ObjectId, Long> onCooldown = new HashMap<>();

    public TotemHandler(Foxtrot instance) {
        this.instance = instance;
        this.instance.getServer().getPluginManager().registerEvents(new TotemListener(), this.instance);

        new TotemService(instance, this).runTaskTimer(this.instance, 20, 20);
    }
}
