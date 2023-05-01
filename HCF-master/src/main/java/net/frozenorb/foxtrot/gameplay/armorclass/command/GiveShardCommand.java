package net.frozenorb.foxtrot.gameplay.armorclass.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveShardCommand {

    @Command(names = {"armorclass giveshard"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "class")ArmorClass armorClass, @Parameter(name = "amount")int amount) {
        target.getInventory().addItem(Foxtrot.getInstance().getArmorClassHandler().createShard(target, armorClass, amount));
        target.sendMessage(ChatColor.translate("&aYou have been given " + amount + "x " + armorClass.getDisplayName() + " &ashards!"));
        sender.sendMessage(ChatColor.translate("&aYou have given " + target.getName() + " " + amount + "x " + armorClass.getDisplayName() + " &ashards!"));
    }

}
