package cc.fyre.neutron.command.rank;

import cc.fyre.neutron.rank.menu.editor.RankEditorMenu;
import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankEditorCommand {

    @Command(
            names = {"rank editor"},
            permission = "neutron.command.rank.editor",
            hidden = true
    )
    public static void execute(Player player) {
        new RankEditorMenu().openMenu(player);
    }

}
