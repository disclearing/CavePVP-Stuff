package cc.fyre.neutron.rank.menu.editor.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.RankEditorMenu;
import cc.fyre.proton.menu.menus.ConfirmMenu;

import cc.fyre.proton.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */


public class RankDeleteMenu extends ConfirmMenu {

    public RankDeleteMenu(Rank rank,Player player) {
        super("Delete rank?",(Callback<Boolean>)value -> {

            if (!value) {
                player.sendMessage(ChatColor.RED + "Cancelled deleting rank " + rank.getFancyName() + ChatColor.RED + ".");
            } else {

                if (Neutron.getInstance().getRankHandler().getCache().get(rank.getUuid()) != null) {
                    rank.delete();
                }

                player.sendMessage(ChatColor.GREEN + "Deleted rank " + rank.getFancyName() + ChatColor.GREEN + ".");
            }

            new RankEditorMenu().openMenu(player);
        });
    }

}
