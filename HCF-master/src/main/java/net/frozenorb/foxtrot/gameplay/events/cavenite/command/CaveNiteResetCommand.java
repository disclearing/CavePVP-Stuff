package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CaveNiteResetCommand {

    @Command(names = {"cavenite reset"}, permission = "op")
    public static void reset(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        player.sendMessage(ChatColor.translate("&cReset cavenite"));

        Foxtrot.getInstance().getCaveNiteHandler().setGameState(CaveNiteState.INACTIVE);
        Foxtrot.getInstance().getCaveNiteHandler().setScatterIn(0);
        Foxtrot.getInstance().getCaveNiteHandler().setStarted(0);
        Foxtrot.getInstance().getCaveNiteHandler().setStartIn(0);
    }

    @Command(names = {"cavenite resetall"}, permission = "op")
    public static void resetAll(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        player.sendMessage(ChatColor.translate("&cReset Cave Nite All"));

        Foxtrot.getInstance().getCaveNiteHandler().setGameState(CaveNiteState.INACTIVE);
        Foxtrot.getInstance().getCaveNiteHandler().setScatterIn(0);
        Foxtrot.getInstance().getCaveNiteHandler().setStarted(0);
        Foxtrot.getInstance().getCaveNiteHandler().setStartIn(0);
        Foxtrot.getInstance().getCaveNiteHandler().getChestLocations().clear();
        Foxtrot.getInstance().getCaveNiteHandler().getChestLoot().clear();
        Foxtrot.getInstance().getCaveNiteHandler().getLocations().clear();
        Foxtrot.getInstance().getCaveNiteHandler().getPlayersRemaining().clear();
        Foxtrot.getInstance().getCaveNiteHandler().getSpectators().clear();
    }
}
