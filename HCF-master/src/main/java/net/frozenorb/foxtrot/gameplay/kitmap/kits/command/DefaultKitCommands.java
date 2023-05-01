package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.KitListener;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.listener.KitEditorListener;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.menu.KitMainMenu;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DefaultKitCommands {
    @Command(names = {"selectkit"}, permission = "")
    public static void kitMenu(Player player) {
        new KitMainMenu().openMenu(player);
    }

    @Command(names = {"diamond"}, permission = "")
    public static void execute(Player player) {
        KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Diamond"));
    }

    @Command(names = {"refill"}, permission = "")
    public static void refill(Player player) {

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(ChatColor.RED + "You can only do this on Kitmap!");
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You may only do this in Spawn!");
            return;
        }

        KitListener.openRefillInventory(player.getLocation(), player);
    }

    @Command(names = {"witherskeleton"}, permission = "")
    public static void reaper (Player player) {
        KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("WitherSkeleton"));
    }

    @Command(names = {"archer"}, permission = "")
    public static void archer(Player player) {
        KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Archer"));
    }

    @Command(names = {"miner"}, permission = "")
    public static void miner(Player player) {
        KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Miner"));
    }

    @Command(names = {"rogue"}, permission = "")
    public static void rogue(Player player) {
        KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Rogue"));
    }

    @Command(names = {"bard"}, permission = "")
    public static void bard(Player player) {
        KitListener.attemptApplyKit(player, Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Bard"));
    }
}
