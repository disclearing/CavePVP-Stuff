package cc.fyre.neutron.command.rank;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.RankEditorMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RankCreateCommand {

    @Command(
            names = {"rank create"},
            permission = "neutron.command.rank.create"
    )
    public static void execute(CommandSender sender,@Parameter(name = "name")String name) {

        Rank rank = Neutron.getInstance().getRankHandler().fromName(name);

        if (rank != null) {
            sender.sendMessage(ChatColor.RED + "Rank " + rank.getFancyName() + ChatColor.RED + " already exists.");
            return;
        }

        rank = new Rank(UUID.randomUUID(),name,UUIDUtils.uuid(sender.getName()));

        Neutron.getInstance().getRankHandler().getCache().put(rank.getUuid(),rank);

        rank.save();

        sender.sendMessage(ChatColor.GOLD + "Created a new rank: " + rank.getFancyName());

        if (sender instanceof Player) {
            new RankEditorMenu().openMenu((Player)sender);
        }

    }

}
