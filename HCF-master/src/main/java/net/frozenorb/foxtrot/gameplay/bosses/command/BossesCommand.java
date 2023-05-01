package net.frozenorb.foxtrot.gameplay.bosses.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.Boss;
import net.frozenorb.foxtrot.gameplay.bosses.BossHandler;
import org.bukkit.entity.Player;

public class BossesCommand {
    @Command(names = {"boss spawn", "boss summon"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "taskName", defaultValue = "R_A_N_D_O_M")String taskName) {
        final BossHandler bossHandler = Foxtrot.getInstance().getBossHandler();

        if (taskName.equalsIgnoreCase("R_A_N_D_O_M")) {
            bossHandler.activateRandom();
            return;
        }

        final Boss boss = bossHandler.findBoss(taskName);

        bossHandler.setActiveBoss(boss);
        boss.activate();
    }

    @Command(names = {"boss stop", "boss deactivate"}, permission = "op")
    public static void execute(Player player) {
        final BossHandler bossHandler = Foxtrot.getInstance().getBossHandler();

        bossHandler.getActiveBoss().deactivate(null);
    }
}
