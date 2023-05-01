package cc.fyre.neutron.command.profile.permission;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PermissionScanCommand {

    @Command(
            names = {"profiles scan","profiles scan"},
            async = true,
            permission = "neutron.command.permission.scan"
    )
    public static void execute(CommandSender sender, @Parameter(name = "permission") String permission, @Flag(value = {"e","effective"})boolean effective) {

        new Thread(() -> {

            for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

                final Profile profile = new Profile(document);

                if (!effective && !profile.getPermissions().contains(permission) || effective && !profile.getEffectivePermissions().contains(permission)) {
                    continue;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',profile.getName() + " &7has &f" + permission + "&7."));
            }
        }).start();
    }


    @Command(
            names = {"profiles scankeyword"},
            async = true,
            permission = "neutron.command.permission.scan"
    )
    public static void star(CommandSender sender, @Parameter(name = "keyword") String permission, @Flag(value = {"e","effective"})boolean effective) {

        new Thread(() -> {

            int amount = 0;
            int has = 0;

            for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {
                amount++;

                final Profile profile = new Profile(document);

                if (profile.getPermissions() == null || profile.getEffectivePermissions() == null) {
                    continue;
                }

                for (String profilePermission : profile.getPermissions()) {
                    if (profilePermission != null && profilePermission.toLowerCase().contains(permission.toLowerCase())) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',profile.getName() + " &7has &f" + profilePermission));
                        has++;
                    }
                }

                if (effective) {
                    for (String profilePermission : profile.getEffectivePermissions()) {
                        if (profilePermission != null && profilePermission.toLowerCase().contains(permission.toLowerCase())) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&e[E] &f" + profile.getName() + " &7has &f" + profilePermission));
                            has++;
                        }
                    }
                }
            }

            sender.sendMessage(ChatColor.GREEN + "Successfully checked " + amount + "! " + has + " match keyword.");
        }).start();
    }

    @Command(
            names = {"profiles scan remove","profiles scan remove"},
            async = true,
            permission = "neutron.command.permission.scan"
    )
    public static void remove(CommandSender sender, @Parameter(name = "permission") String permission, @Flag(value = {"e","effective"})boolean effective) {

        new Thread(() -> {

            for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

                final Profile profile = new Profile(document);

                if (!effective && !profile.getPermissions().contains(permission) || effective && !profile.getEffectivePermissions().contains(permission)) {
                    continue;
                }

                if (effective) {
                    profile.getGrants().clear();
                } else {
                    profile.getPermissions().remove(permission);
                }
                profile.save();

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',profile.getName() + " &7has &f" + permission + "&7. &cREMOVED"));
            }
        }).start();
    }

}
