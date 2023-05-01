package net.frozenorb.foxtrot.team.commands.pvp;

import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.server.deathban.DeathbanArenaHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PvPReviveCommand {

    @Command(names = {"pvptimer revive", "timer revive", "pvp revive", "pvptimer revive", "timer revive", "pvp revive", "lives revive"}, permission = "")
    public static void pvpRevive(Player sender, @Parameter(name = "player") UUID player) {
        int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());

        if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives <= 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        final DeathbanArenaHandler deathbanArenaHandler = Foxtrot.getInstance().getDeathbanArenaHandler();

        long lifeCooldown = deathbanArenaHandler.getLifeCooldown().getOrDefault(player, 0L);

        if (lifeCooldown > System.currentTimeMillis()) {
            int difference = (int) (lifeCooldown-System.currentTimeMillis())/1000;

            sender.sendMessage(ChatColor.translate(UUIDUtils.name(player) + " &cmay not use a life for another &f" + TimeUtils.formatIntoDetailedString(difference) + " &cas you died in either Nether, End or an Event!"));
            return;
        }

        // Use a friend life.
        Foxtrot.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " with a friend life!");

        deathbanArenaHandler.revive(player);
    }

}