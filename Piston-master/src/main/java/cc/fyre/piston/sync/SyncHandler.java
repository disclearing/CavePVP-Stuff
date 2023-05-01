package cc.fyre.piston.sync;

import cc.fyre.piston.Piston;
import lombok.Getter;

import java.util.*;

public class SyncHandler {
    @Getter private Map<UUID, String> players = new HashMap<>();

    private Piston instance;

    public SyncHandler(Piston instance) {
        this.instance = instance;


    }

}
