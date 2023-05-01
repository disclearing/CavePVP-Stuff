package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.Universe;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FakePlayersCommand {
    @Command(names = {"minecraftlol addplayers"}, hidden = true, permission = "op")
    public static void execute(Player player, @Parameter(name = "amount")int amount) {
        Universe.fakePlayers += amount;
        player.sendMessage(ChatColor.GOLD + "Fake players now at " + Universe.fakePlayers);
    }
    @Command(names = {"minecraftlol removeplayers"}, hidden = true, permission = "op")
    public static void remove(Player player, @Parameter(name = "amount")int amount) {
        Universe.fakePlayers -= amount;
        player.sendMessage(ChatColor.GOLD + "Fake players now at " + Universe.fakePlayers);
    }
    @Command(names = {"minecraftlol setplayers"}, hidden = true, permission = "op")
    public static void set(Player player, @Parameter(name = "amount")int amount) {
        Universe.fakePlayers = amount;
        player.sendMessage(ChatColor.GOLD + "Fake players now at " + Universe.fakePlayers);
    }

}
