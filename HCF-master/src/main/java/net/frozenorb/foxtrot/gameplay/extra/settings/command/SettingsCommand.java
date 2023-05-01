package net.frozenorb.foxtrot.gameplay.extra.settings.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.settings.NameTagMenu;
import net.frozenorb.foxtrot.gameplay.extra.settings.SettingsMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SettingsCommand {
    @Command(names = {"settings"}, permission = "")
    public static void execute(Player player) {

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not run this command whilst spawn tagged!");
            return;
        }

        new SettingsMenu().openMenu(player);
    }

    @Command(names = {"nametags", "nametag", "nametag settings", "nametags settings"}, permission = "")
    public static void nameTags(Player player) {

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You may not run this command whilst spawn tagged!");
            return;
        }

        new NameTagMenu().openMenu(player);
    }

    @Command(names = {"saletimers", "customtimers"}, hidden = true, permission = "op")
    public static void saleTimers(Player player) {

        if (player.hasMetadata("NO_TIMER")) {
            player.removeMetadata("NO_TIMER", Foxtrot.getInstance());
            player.sendMessage(ChatColor.GREEN + "You can now see sale timers");
        } else {
            player.setMetadata("NO_TIMER", new FixedMetadataValue(Foxtrot.getInstance(), true));
            player.sendMessage(ChatColor.RED + "You can no longer see sale timers");
        }

    }
}
