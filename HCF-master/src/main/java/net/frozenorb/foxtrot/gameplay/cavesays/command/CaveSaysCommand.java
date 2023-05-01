package net.frozenorb.foxtrot.gameplay.cavesays.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.cavesays.CaveSaysHandler;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.entity.Player;

public class CaveSaysCommand {
    @Command(names = {"cavesays start", "cavesays activate"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "taskName", defaultValue = "R_A_N_D_O_M")String taskName) {
        final CaveSaysHandler caveSaysHandler = Foxtrot.getInstance().getCaveSaysHandler();

        if (taskName.equalsIgnoreCase("R_A_N_D_O_M")) {
            caveSaysHandler.activateRandom();
            return;
        }

        final Task task = caveSaysHandler.findTask(taskName);

        caveSaysHandler.setActiveTask(task);
        task.activate();
    }

    @Command(names = {"cavesays stop", "cavesays deactivate"}, permission = "op")
    public static void execute(Player player) {
        final CaveSaysHandler caveSaysHandler = Foxtrot.getInstance().getCaveSaysHandler();

        caveSaysHandler.getActiveTask().deactivate(null);
    }
}
