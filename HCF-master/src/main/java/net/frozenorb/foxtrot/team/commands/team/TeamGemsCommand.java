package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamGemsCommand {
    @Command(names = {"team addgems"}, permission = "op", hidden = true)
    public static void execute(CommandSender sender, @Parameter(name = "team")Team team, @Parameter(name = "gems")int gems) {
        team.setAddedGems(team.getAddedGems()+gems);
        team.recalculateGems();
        team.flagForSave();
        sender.sendMessage(ChatColor.translate("&6Added &f" + gems + " &6to &f" + team.getName() + "&6."));
    }

    @Command(names = {"team depositgems", "f depositgems", "faction depositgems", "t depositgems"}, permission = "")
    public static void depositGems(Player sender, @Parameter(name = "gems")int gems) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (gems <= 0 || Float.isNaN(gems)) {
            sender.sendMessage(ChatColor.RED + "You can't deposit 0 gems (or less)!");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command may only be used on Kitmap!");
            return;
        }

        if (!Foxtrot.getInstance().getGemMap().removeGems(sender.getUniqueId(), gems)) {
            sender.sendMessage(CC.RED + "You do not have enough gems for this!");
            return;
        }

        team.setAddedGems(team.getAddedGems()+gems);
        team.recalculateGems();
        team.flagForSave();
        team.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.DARK_GREEN + gems + " gems" + ChatColor.YELLOW + " into the team balance.");
    }

    @Command(names = {"team removegems", "team takegems"}, permission = "op", hidden = true)
    public static void remove(CommandSender sender, @Parameter(name = "team")Team team, @Parameter(name = "gems")int gems) {
        team.setRemovedGems(team.getRemovedGems()+gems);
        team.recalculateGems();
        team.flagForSave();
        sender.sendMessage(ChatColor.translate("&6Removed &f" + gems + " &6from &f" + team.getName() + "&6."));
    }
}
