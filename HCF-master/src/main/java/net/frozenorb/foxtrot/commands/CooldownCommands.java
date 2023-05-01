package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class CooldownCommands {

    @Command(names={ "enderpearl remove" }, permission="foxtrot.command.enderpearl")
    public static void remove(Player sender, @Parameter(name="player", defaultValue="self") Player player) {
        EnderpearlCooldownHandler.removeCooldown(player);
        sender.sendMessage(CC.RED + "Successfully removed the cooldown!");
    }

    @Command(names={ "enderpearl add" }, permission="foxtrot.command.enderpearl")
    public static void add(Player sender, @Parameter(name="player", defaultValue="self") Player player) {
        EnderpearlCooldownHandler.resetEnderpearlTimer(player);
        sender.sendMessage(CC.GREEN + "Successfully added the cooldown!");
    }

    @Command(names = {"nopartneritemcooldown"}, permission = "command.nopartneritemcooldown")
    public static void execute(Player player) {
        if (player.hasMetadata("NO_COOLDOWN")) {
            player.sendMessage(ChatColor.RED + "You now have partner item cooldowns again.");
            player.removeMetadata("NO_COOLDOWN", Foxtrot.getInstance());
            return;
        }

        player.setMetadata("NO_COOLDOWN", new FixedMetadataValue(Foxtrot.getInstance(), true));
        player.sendMessage(ChatColor.GREEN + "You now have no partner item cooldown");
    }

    @Command(names={ "GoppleReset" }, permission="foxtrot.gopplereset")
    public static void goppleReset(Player sender, @Parameter(name="player") UUID player) {
        Foxtrot.getInstance().getOppleMap().resetCooldown(player);
        sender.sendMessage(ChatColor.RED + "Cooldown reset!");
    }

    @Command(names={ "spawntag remove" }, permission="foxtrot.command.enderpearl")
    public static void removeTag(Player sender, @Parameter(name="player", defaultValue="self") Player player) {
        SpawnTagHandler.removeTag(player);
        sender.sendMessage(ChatColor.RED + "Successfully removed the cooldown!");
    }

    @Command(names={ "spawntag add" }, permission="foxtrot.command.enderpearl")
    public static void addTag(Player sender, @Parameter(name="player", defaultValue="self") Player player) {
        SpawnTagHandler.addOffensiveSeconds(player, 30);
        sender.sendMessage(ChatColor.GREEN + "Successfully added the cooldown!");
    }

}
