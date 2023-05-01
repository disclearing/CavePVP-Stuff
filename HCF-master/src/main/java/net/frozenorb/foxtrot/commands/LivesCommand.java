package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import net.minecraft.util.com.google.common.io.Files;
import net.frozenorb.foxtrot.persist.maps.FriendLivesMap;
import net.frozenorb.foxtrot.server.deathban.DeathbanArenaHandler;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class LivesCommand {

    @Command(names={ "lives" }, permission="")
    public static void lives(CommandSender commandSender) {
        
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "Bad console.");
            return;
        }

        Player sender = (Player) commandSender;
        
        int shared = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        sender.sendMessage(ChatColor.RED + "Lives: " + ChatColor.WHITE + shared);
    }

    @Command(names={ "Revive" }, permission="foxtrot.revive")
    public static void revive(CommandSender sender, @Parameter(name="player") UUID player, @Parameter(name="reason", wildcard=true) String reason) {
        if (reason.equals(".")) {
            sender.sendMessage(ChatColor.RED + ". is not a good reason...");
            return;
        }

        if (Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player)) {
            File logTo = new File(new File("foxlogs"), "adminrevives.log");

            try {
                logTo.createNewFile();
                Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + sender.getName() + " revived " + UUIDUtils.name(player) + " for " + reason + "\n", logTo, Charset.defaultCharset());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (sender instanceof Player) {
                boolean urgent = UUIDUtils.name(player).equalsIgnoreCase("z5");

                Neutron.getInstance().getSecurityHandler().addSecurityAlert(((Player) sender).getUniqueId(), player, AlertType.REVIVES_ROLLBACKS, urgent, "Reason: " + reason);
            }

            Foxtrot.getInstance().getDeathbanArenaHandler().revive(player);
            sender.sendMessage(ChatColor.GREEN + "Revived " + UUIDUtils.name(player) + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

    @Command(names = {"holiday revive", "rank revive"}, permission = "command.rankrevive")
    public static void execute(Player player, @Parameter(name = "player") UUID target) {
        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(target)) {
            player.sendMessage(ChatColor.RED + "That player isn't deathbanned!");
            return;
        }

        final DeathbanArenaHandler deathbanArenaHandler = Foxtrot.getInstance().getDeathbanArenaHandler();

        long lifeCooldown = deathbanArenaHandler.getLifeCooldown().getOrDefault(target, 0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown-System.currentTimeMillis())/1000;

            player.sendMessage(ChatColor.translate(UUIDUtils.name(target) + " &cmay not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference) + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        player.sendMessage(ChatColor.translate("&6Successfully used your &b&lEar&a&lth Rank &6to revive &f" + UUIDUtils.name(target) + "&6."));
        Foxtrot.getInstance().getDeathbanArenaHandler().revive(target);
    }

    @Command(names = {"cave revive", "cave revive"}, permission = "command.caverevive")
    public static void cave(Player player, @Parameter(name = "player") UUID target) {
        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(target)) {
            player.sendMessage(ChatColor.RED + "That player isn't deathbanned!");
            return;
        }

        final DeathbanArenaHandler deathbanArenaHandler = Foxtrot.getInstance().getDeathbanArenaHandler();

        long lifeCooldown = deathbanArenaHandler.getLifeCooldown().getOrDefault(target, 0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown-System.currentTimeMillis())/1000;

            player.sendMessage(ChatColor.translate(UUIDUtils.name(target) + " &cmay not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference) + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        player.sendMessage(ChatColor.translate("&6Successfully used your &4&LCave Rank &6to revive &f" + UUIDUtils.name(target) + "&6."));
        Foxtrot.getInstance().getDeathbanArenaHandler().revive(target);
    }
}
