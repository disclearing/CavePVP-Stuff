package cc.fyre.neutron.command.prefix;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prefix.Prefix;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PrefixCreateCommand {

    @Command(
            names = {"prefix create"},
            permission = "neutron.command.prefix.create"
    )
    public static void execute(CommandSender sender,@Parameter(name = "name")String name) {

        Prefix prefix = Neutron.getInstance().getPrefixHandler().fromName(name);

        if (prefix != null) {
            sender.sendMessage(ChatColor.RED + "Prefix " + ChatColor.WHITE + prefix.getName() + ChatColor.RED + " already exists.");
            return;
        }

        prefix = new Prefix(UUID.randomUUID(),name,UUIDUtils.uuid(sender.getName()));

        Neutron.getInstance().getPrefixHandler().getCache().put(prefix.getUuid(),prefix);

        prefix.save();

        sender.sendMessage(ChatColor.GOLD + "Created a new prefix: " + ChatColor.WHITE + prefix.getName());
    }


}
