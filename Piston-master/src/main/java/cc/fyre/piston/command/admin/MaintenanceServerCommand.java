package cc.fyre.piston.command.admin;

import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MaintenanceServerCommand {

    @Command(names = {"maintenanceserver"}, permission = "op")
    public static void execute(CommandSender sender) {
        final Piston instance = Piston.getInstance();

        Bukkit.broadcastMessage(sender.getName() + " activated maintenance");

        if (instance.isMaintenance()) {
            sender.sendMessage("Took off maintenance!");
            instance.setMaintenance(false);
        } else {
            for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
                onlinePlayer.setMetadata("loggedout", new FixedMetadataValue(instance, true));
                onlinePlayer.kickPlayer("Server Closed");
            }

            System.out.println("");
            System.out.println("MAINTENANCE MODE ACTIVATED BY " + sender.getName());
            System.out.println("");

            instance.setMaintenance(true);
        }
    }

}
