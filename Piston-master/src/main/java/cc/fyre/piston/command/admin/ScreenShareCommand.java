package cc.fyre.piston.command.admin;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.piston.PistonConstants;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.BLUE;

public class ScreenShareCommand {

    @Command(
            names = {"screenshare", "ss"},
            permission = "command.freeze"
    )
    public static void screenshare(CommandSender sender, @Parameter(name = "player") Player player) {
        if (!sender.isOp() && player.isOp()) {
            sender.sendMessage(ChatColor.RED + "You may not freeze that player!");
            return;
        }

        if (player.getName().equalsIgnoreCase("SimplyTrash") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick SimplyTrash");
            return;
        }

        Piston.getInstance().getServerHandler().freeze(player);
        sender.sendMessage(ChatColor.GOLD + "You froze and dispatched a message to " + player.getDisplayName() + "!");

        String displayName = NeutronConstants.CONSOLE_NAME;
        final String targetDisplayName = Neutron.getInstance().getProfileHandler().findDisplayName(player.getUniqueId());

        if (sender instanceof Player) {
            displayName = Neutron.getInstance().getProfileHandler().findDisplayName(((Player) sender).getUniqueId());
        }

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.STAFF_PERMISSION,
                BLUE + "[SC]" + AQUA + "[" + UniverseAPI.getServerName() + "] " + ChatColor.translate(displayName
                        + " &7has frozen &f" + targetDisplayName + "&7.")
        ));

        new BukkitRunnable() {

            public void run() {
                if (!player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
                    this.cancel();
                    return;
                }
                for (int i = 0; i < 3; ++i) {
                    player.sendMessage("");
                }
                ScreenShareCommand.sendDangerSign(player, "", ChatColor.DARK_RED + "Do " + ChatColor.BOLD + "NOT" + ChatColor.DARK_RED + " log out!", ChatColor.RED + "If you do, you will be banned!", ChatColor.YELLOW + "Please download " + ChatColor.BOLD + "TeamSpeak 3" + ChatColor.YELLOW + " and join", ChatColor.YELLOW + "ts.cavepvp.org", "", "");
                player.sendMessage("");
            }
        }.runTaskTimer(Proton.getInstance(), 0L, 100L);
    }

    public static void sendDangerSign(Player player, String... args) {
        String[] lines = new String[]{"", "", "", "", "", "", ""};
        System.arraycopy(args, 0, lines, 0, args.length);
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588\u2588\u2588" + ChatColor.RESET + (lines[0].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[0]).toString()));
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588\u2588" + ChatColor.RESET + (lines[1].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[1]).toString()));
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588" + ChatColor.RESET + (lines[2].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[2]).toString()));
        player.sendMessage(ChatColor.WHITE + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588\u2588" + ChatColor.RESET + (lines[3].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[3]).toString()));
        player.sendMessage(ChatColor.WHITE + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588" + ChatColor.RESET + (lines[4].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[4]).toString()));
        player.sendMessage(ChatColor.WHITE + "\u2588" + ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.WHITE + "\u2588" + ChatColor.RESET + (lines[5].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[5]).toString()));
        player.sendMessage(ChatColor.RED + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.BLACK + "\u2588" + ChatColor.GOLD + "\u2588\u2588\u2588" + ChatColor.RED + "\u2588" + ChatColor.RESET + (lines[6].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[6]).toString()));
        player.sendMessage(ChatColor.RED + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588" + ChatColor.RESET + (lines[6].isEmpty() ? "" : new StringBuilder().append(" ").append(lines[7]).toString()));
    }

}

