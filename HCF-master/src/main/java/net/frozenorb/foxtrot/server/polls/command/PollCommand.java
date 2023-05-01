package net.frozenorb.foxtrot.server.polls.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.polls.menu.MultiplePollsMenu;
import net.frozenorb.foxtrot.server.polls.menu.PollListMenu;
import net.frozenorb.foxtrot.server.polls.menu.VoteMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PollCommand {

    @Command(names = {"poll editor", "polls editor"}, permission = "op")
    public static void execute(Player player) {
        new PollListMenu().openMenu(player);
    }

    @Command(names = {"poll vote", "polls vote", "polls", "poll"}, permission = "")
    public static void vote(Player player) {

        if (Foxtrot.getInstance().getPollHandler().getActivePolls().size() == 1) {
            new VoteMenu(Foxtrot.getInstance().getPollHandler().getActivePolls().get(0)).openMenu(player);
        } else if (Foxtrot.getInstance().getPollHandler().getActivePolls().size() > 1) {
            new MultiplePollsMenu().openMenu(player);
        } else {
            player.sendMessage(ChatColor.RED + "There are no active polls!");
        }
    }

}
