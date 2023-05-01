package net.frozenorb.foxtrot.gameplay.content.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class ContentCommand {

    @Command(names = {"content restrict"}, permission = "command.content.restrict")
    public static void restrict(Player player, @Parameter(name = "target")Player target) {
        if (!target.hasMetadata("RESTRICTED")) {
            target.setMetadata("RESTRICTED", new FixedMetadataValue(Foxtrot.getInstance(), true));
            player.sendMessage(ChatColor.RED + target.getName() + " can no longer place, break and use blocks.");
        } else {
            target.removeMetadata("RESTRICTED", Foxtrot.getInstance());
            player.sendMessage(ChatColor.GREEN + target.getName() + " can now place, break and use blocks.");
        }
    }

    @Command(names = {"content jump"}, permission = "command.content.jump")
    public static void execute(Player player) {

        if (!player.hasMetadata("JUMP")) {
            player.setMetadata("JUMP", new FixedMetadataValue(Foxtrot.getInstance(), true));
            player.sendMessage(ChatColor.GREEN + player.getName() + " will now take damage whenever they jump.");
        } else {
            player.removeMetadata("JUMP", Foxtrot.getInstance());
            player.sendMessage(ChatColor.RED + player.getName() + " will no longer take damage whenever they jump.");
        }
    }

}
