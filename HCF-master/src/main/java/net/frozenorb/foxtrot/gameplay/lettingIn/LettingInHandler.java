package net.frozenorb.foxtrot.gameplay.lettingIn;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.lettingIn.listener.LettingInListener;
import org.bson.types.ObjectId;

import java.util.*;

public class LettingInHandler {
    private Foxtrot instance;

    @Getter private Map<ObjectId, Integer> cache = new HashMap<>();

    public LettingInHandler(Foxtrot instance) {
        this.instance = instance;

        this.instance.getServer().getPluginManager().registerEvents(new LettingInListener(this.instance, this), this.instance);
    }
}