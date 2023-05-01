package cc.fyre.proton.combatlogger;

import cc.fyre.proton.Proton;
import cc.fyre.proton.combatlogger.listener.CombatLoggerListener;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatLoggerHandler {

    @Getter private final Map<UUID,CombatLogger> combatLoggerMap = new HashMap<>();

    public CombatLoggerHandler() {
        Proton.getInstance().getServer().getPluginManager().registerEvents(new CombatLoggerListener(),Proton.getInstance().getInstance());
    }

}
