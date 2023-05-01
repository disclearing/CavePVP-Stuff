package cc.fyre.piston.command.player;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TutorialCommand {

    @Command(names = {"tutorial"}, permission = "")
    public static void execute(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Tutorial");
        player.sendMessage(ChatColor.GRAY + "Watch this series to see how you can play HCF with no rank!");
        player.sendMessage(ChatColor.RED + "https://www.youtube.com/watch?v=yPEUozjywlI&list=PLxRAjs0n2S1RI6-DJwb9_fhAfhhwxUFB4");
        player.sendMessage("");
    }

}
