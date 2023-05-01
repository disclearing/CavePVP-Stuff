package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamClearCommand {
    @Command(names={ "team clear", "t clear", "f clear", "faction clear", "fac clear" }, permission="foxtrot.command.clearinv")
    public static void execute(Player sender, @Parameter(name="team") Team team) {
        if (team.getOnlineMembers().size() == 0) {
            sender.sendMessage(ChatColor.RED + "That team has no online players!");
            return;
        }

        sender.sendMessage(ChatColor.translate("&aPerforming a inventory/effect clear on the faction " + team.getName() + "..."));
        sender.sendMessage(CC.GRAY + CC.HORIZONTAL_SEPARATOR);
        team.getOnlineMembers().forEach(it -> {
            sender.sendMessage(ChatColor.GREEN + "Cleared inventory for player " + ChatColor.WHITE + it.getName() + ChatColor.GREEN + ".");
            it.getInventory().clear();
            it.setHealth(20);
            it.setFoodLevel(20);
            it.getInventory().setArmorContents(null);
            it.getActivePotionEffects().forEach(potionEffect -> it.removePotionEffect(potionEffect.getType()));
            it.updateInventory();
        });

        sender.sendMessage(CC.GRAY + CC.HORIZONTAL_SEPARATOR);
    }
}
