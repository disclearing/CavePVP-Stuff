package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TeamCheckInvCommand {

    @Command(names={ "team checkinv", "t checkinv", "f checkinv", "faction checkinv", "fac checkinv" }, permission="foxtrot.command.checkteaminv")
    public static void teamCheckInv(Player sender, @Parameter(name="team") Team team, @Parameter(name="item") ItemStack itemStack) {
        if (team.getOnlineMembers().size() == 0) {
            sender.sendMessage(ChatColor.RED + "That team has no online players!");
            return;
        }

        final String properName = WordUtils.capitalize(itemStack.getType().name().replace("_", " "));
        sender.sendMessage(ChatColor.translate("&aPerforming a inventory lookup on the faction " + team.getName() + "..."));
        sender.sendMessage(ChatColor.translate("&7Looking up the item &f" + properName + "&7."));
        sender.sendMessage(CC.GRAY + CC.HORIZONTAL_SEPARATOR);

        if (team.getOnlineMembers().stream().anyMatch(it -> it.getInventory().contains(itemStack.getType()))) {
            team.getOnlineMembers().stream().filter(it -> it.getInventory().contains(itemStack.getType())).forEach(it -> sender.sendMessage(ChatColor.translate(it.getName() + " &ehas the item &f" + properName + "&e.")));
        } else {
            sender.sendMessage(ChatColor.translate("&cNobody in that team has &f" + properName + "&c."));
        }

        sender.sendMessage(CC.GRAY + CC.HORIZONTAL_SEPARATOR);
    }

}
