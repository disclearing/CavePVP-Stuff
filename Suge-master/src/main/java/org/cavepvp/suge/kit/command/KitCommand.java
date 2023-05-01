package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;
import org.cavepvp.suge.kit.menu.KitMainMenu;
import org.cavepvp.suge.kit.menu.kitmap.KitAllMenu;

import java.util.Optional;

public class KitCommand {
    @Command(names = {"kit", "kits"}, permission = "")
    public static void kit(Player player, @Parameter(name = "kit", defaultValue = "M_E_N_U")String kitName) {
        if (UniverseAPI.getServerName().contains("Kits")) {
            player.chat("/selectkit");
            return;
        }

        player.chat("/gkit " + kitName);
    }

    @Command(names = {"gkit", "godkit", "gkitz", "gkits", "godkits"}, permission = "")
    public static void execute(Player player, @Parameter(name = "kit", defaultValue = "M_E_N_U")String kitName) {
        if (kitName.equals("M_E_N_U")) {
            if (UniverseAPI.getServerName().toLowerCase().contains("kits")) {
                new KitAllMenu().openMenu(player);
                return;
            }

            new KitMainMenu().openMenu(player);
            return;
        }

        final Optional<Kit> kit = Suge.getInstance().getKitHandler().findKit(kitName);

        if (!kit.isPresent()) {
            if (UniverseAPI.getServerName().toLowerCase().contains("kits")) {
                new KitAllMenu().openMenu(player);
                return;
            }

            new KitMainMenu().openMenu(player);
            return;
        }

        if (!player.hasPermission("crazyenchantments.gkitz." + kit.get().getName().toLowerCase())) {
            player.sendMessage(ChatColor.RED + "This kit is locked, purchase this kit at https://store.cavepvp.org");
            return;
        }

        kit.get().equip(player);
    }

    @Command(names = {"getdisplayname"}, permission = "op")
    public static void displayName(Player player) {

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack == null) {
            player.sendMessage(ChatColor.RED + "No ITEM!");
            return;
        }

        if (itemStack.getItemMeta() == null) {
            player.sendMessage(ChatColor.RED + "No item meta");
            return;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            player.sendMessage(ChatColor.RED + "No display name for item");
            return;
        }

        player.sendMessage(itemStack.getItemMeta().getDisplayName() + " <-- Display Name");
        player.sendMessage(itemStack.getItemMeta().getDisplayName() + " <-- Display without Color");
    }
}
