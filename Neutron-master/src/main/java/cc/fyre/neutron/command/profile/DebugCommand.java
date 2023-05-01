package cc.fyre.neutron.command.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Date;
import java.util.UUID;

public class DebugCommand {

    @Command(
            names = {"profile debug"},
            permission = "neutron.command.profile.debug"
    )
    public static void execute(CommandSender sender,@Parameter(name = "uuid")UUID uuid) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        sender.sendMessage(ChatColor.BLUE + NeutronConstants.CHAT_BAR);
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "UUID: " + ChatColor.WHITE + profile.getUuid().toString());
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Name: " + ChatColor.WHITE + profile.getName());
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Fancy Name: " + profile.getFancyName());
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Active Grant: " + profile.getActiveRank().getFancyName());
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Active Prefix: " + ChatColor.WHITE + (profile.getActivePrefix() == null ? "None":profile.getActivePrefix().getDisplay()));
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Chat Color: " + profile.getChatColor() + profile.getChatColor().name());
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Online: " + ChatColor.WHITE + (profile.getServerProfile().isOnline() ? "Yes":"No"));
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "First Login: " + ChatColor.WHITE + TimeUtils.formatIntoCalendarString(new Date(profile.getServerProfile().getFirstLogin())));
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Last Login: " + ChatColor.WHITE + TimeUtils.formatIntoCalendarString(new Date(profile.getServerProfile().getLastLogin())));
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Current Server: " + ChatColor.WHITE + profile.getServerProfile().getCurrentServer());
        sender.sendMessage(ChatColor.RED + "-> " + ChatColor.GOLD + "Last Server: " + ChatColor.WHITE + profile.getServerProfile().getLastServer());
        sender.sendMessage(ChatColor.BLUE + NeutronConstants.CHAT_BAR);
    }

}
