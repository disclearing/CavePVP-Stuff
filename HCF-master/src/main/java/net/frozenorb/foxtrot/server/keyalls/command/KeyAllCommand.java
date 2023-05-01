package net.frozenorb.foxtrot.server.keyalls.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import net.frozenorb.foxtrot.server.keyalls.KeyAllHandler;
import net.frozenorb.foxtrot.server.keyalls.menu.editor.EditorMainMenu;
import net.frozenorb.foxtrot.server.keyalls.menu.redeem.MultipleKeyAllMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KeyAllCommand {

    @Command(names = {"keyall", "lootboxall", "key all"}, permission = "")
    public static void execute(Player player) {
        final KeyAllHandler keyAllHandler = Foxtrot.getInstance().getKeyAllHandler();
        final List<KeyAll> availableKeyAlls = keyAllHandler.findAvailableKeyAlls(player);

        if (availableKeyAlls.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no key-alls available to you!");
            return;
        }

        if (availableKeyAlls.size() > 1) {
            new MultipleKeyAllMenu(availableKeyAlls).openMenu(player);
            return;
        }

        final KeyAll keyAll = availableKeyAlls.get(0);

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Your inventory is full!");
            return;
        }

        for (ItemStack itemStack : keyAll.getItems()) {

            if (itemStack == null) {
                continue;
            }

            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack.clone());
                continue;
            }

            player.getInventory().addItem(itemStack.clone());
        }

        keyAll.getRedeemed().add(player.getUniqueId());

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            new FancyMessage(ChatColor.translate("&4&lKey-All &8┃ &f" + player.getName() + " &7has redeemed the free &f" + keyAll.getDisplayName() + "&7! &a/keyall"))
                    .tooltip(ChatColor.GREEN + "Click here to redeem the key-all").command("/keyall").send(onlinePlayer);
        }

        player.updateInventory();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.sendMessage(ChatColor.translate("&aYou have redeemed the " + keyAll.getDisplayName() + "&a!"));
    }

    @Command(names = {"keyall setitems"}, permission = "op")
    public static void setItems(Player player, @Parameter(name = "keyall")KeyAll keyAll) {
        final List<ItemStack> items = new ArrayList<>();

        for (ItemStack content : player.getInventory().getContents()) {

            if (content == null) {
                continue;
            }

            if (content.getType().equals(Material.AIR)) {
                continue;
            }

            items.add(content.clone());
        }

        if (items.isEmpty()) {
            player.sendMessage(ChatColor.RED + "Your inventory is empty!");
            return;
        }

        keyAll.setItems(items);
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Set the item loadout!");
    }

    @Command(names = {"keyall editor"}, permission = "op")
    public static void editor(Player player) {
        new EditorMainMenu().openMenu(player);
    }

}
