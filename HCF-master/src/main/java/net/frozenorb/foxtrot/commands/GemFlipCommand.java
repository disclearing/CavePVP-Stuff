package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GemFlipCommand {

    @Command(names = {"gemflip"}, permission = "")
    public static void execute(Player player) {
        player.sendMessage(ChatColor.RED + "SimplyTrash has disabled Gem Flips. They are finished trust me, we coded them however we are very scared of a potential dupe glitch because" +
                "a lot of you are very weird and SimplyTrash will unfortunately not be on to patch any. By the way, this is him typing this so yeah.");
    }

}
