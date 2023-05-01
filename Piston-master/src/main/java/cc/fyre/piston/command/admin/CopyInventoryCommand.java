package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.command.param.defaults.offlineplayer.OfflinePlayerWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CopyInventoryCommand {

    @Command(
            names = {"copyinv","copyinventory", "cpfrom"},
            permission = "command.cpfrom"
    )
    public static void execute(Player player,@Parameter(name = "player")OfflinePlayerWrapper offlinePlayerWrapper) {

        offlinePlayerWrapper.loadAsync(target -> {

            if (target == null) {
                player.sendMessage(ChatColor.RED + "No online or offline player with the name " + offlinePlayerWrapper.getName() + " found.");
                return;
            }

            player.getInventory().setArmorContents(null);
            player.getInventory().clear();


            final ItemStack[] armor = target.getInventory().getArmorContents();
            final ItemStack[] inventory = target.getInventory().getContents();

            for (ItemStack itemStack : inventory) {

                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                player.getInventory().addItem(itemStack);
            }

            player.getInventory().setArmorContents(armor);
            player.updateInventory();
            player.sendMessage(ChatColor.GOLD + "You have copied " + target.getDisplayName() + ChatColor.GOLD + "'s inventory.");

        });

    }

    @Command(
            names = {"copyto","copyinventoryto", "cpto"},
            permission = "command.cpto"
    )
    public static void execute(CommandSender sender,@Parameter(name = "from")OfflinePlayerWrapper offlinePlayerWrapper,@Parameter(name = "to")Player to) {

        offlinePlayerWrapper.loadAsync(from -> {

            if (from == null) {
                sender.sendMessage(ChatColor.RED + "No online or offline player with the name " + offlinePlayerWrapper.getName() + " found.");
                return;
            }

            to.getInventory().setArmorContents(null);
            to.getInventory().clear();

            final Player fromPlayer = from.getPlayer();

            final ItemStack[] armor = fromPlayer.getInventory().getArmorContents();
            final ItemStack[] inventory = fromPlayer.getInventory().getContents();

            for (ItemStack itemStack : inventory) {

                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    continue;
                }

                to.getInventory().addItem(itemStack);
            }

            to.getInventory().setArmorContents(armor);
            to.updateInventory();

            sender.sendMessage(ChatColor.GOLD + "You have copied " + (sender instanceof Player && ((Player)sender).getUniqueId().equals(from.getUniqueId()) ? "your":fromPlayer.getDisplayName() + ChatColor.GOLD + "'s") + " inventory to " + ChatColor.WHITE + to.getDisplayName() + ChatColor.GOLD + ".");


        });

     }

}
