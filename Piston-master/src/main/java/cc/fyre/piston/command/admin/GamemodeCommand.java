package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    @Command(
            names = {"gamemode", "gm"},
            permission = "command.gamemode"
    )
    public static void execute(Player player,@Parameter(name = "gamemode",defaultValue = "1") GameMode gameMode,@Parameter(name = "player",defaultValue = "self") Player target) {

        target.setGameMode(gameMode);

        target.sendMessage(ChatColor.GOLD + "Gamemode: " + ChatColor.WHITE + gameMode.name());

        if (player != target) {

            player.sendMessage(ChatColor.GOLD + "You have set " + target.getDisplayName() + ChatColor.GOLD + "'s gamemode to: " + ChatColor.WHITE + gameMode.name());

        }

    }

    @Command(
            names = {"gamemodec", "gmc", "gm1"},
            permission = "command.gamemode"
    )
    public static void gmc(Player player,@Parameter(name = "player",defaultValue = "self") Player target) {

        GameMode gameMode = GameMode.CREATIVE;

        target.setGameMode(gameMode);

        target.sendMessage(ChatColor.GOLD + "Gamemode: " + ChatColor.WHITE + gameMode.name());

        if (player != target) {

            player.sendMessage(ChatColor.GOLD + "You have set " + target.getDisplayName() + ChatColor.GOLD + "'s gamemode to: " + ChatColor.WHITE + gameMode.name());

        }

    }

    @Command(
            names = {"gamemodes", "gm0", "gms"},
            permission = "command.gamemode"
    )
    public static void gms(Player player,@Parameter(name = "player",defaultValue = "self") Player target) {

        GameMode gameMode = GameMode.SURVIVAL;

        target.setGameMode(gameMode);

        target.sendMessage(ChatColor.GOLD + "Gamemode: " + ChatColor.WHITE + gameMode.name());

        if (player != target) {

            player.sendMessage(ChatColor.GOLD + "You have set " + target.getDisplayName() + ChatColor.GOLD + "'s gamemode to: " + ChatColor.WHITE + gameMode.name());

        }

    }

    @Command(
            names = {"gamemodea", "gm2", "gma"},
            permission = "command.gamemode"
    )
    public static void gma(Player player,@Parameter(name = "player",defaultValue = "self") Player target) {

        GameMode gameMode = GameMode.ADVENTURE;

        target.setGameMode(gameMode);

        target.sendMessage(ChatColor.GOLD + "Gamemode: " + ChatColor.WHITE + gameMode.name());

        if (player != target) {

            player.sendMessage(ChatColor.GOLD + "You have set " + target.getDisplayName() + ChatColor.GOLD + "'s gamemode to: " + ChatColor.WHITE + gameMode.name());

        }

    }

}
