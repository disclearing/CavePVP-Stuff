package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClickableKitListener implements Listener {
    private Foxtrot instance;

    public ClickableKitListener(Foxtrot instance) {
        this.instance = instance;
    }

    @Command(names = {"clickablekit give"}, permission = "command.clickablekit")
    public static void execute(CommandSender sender, @Parameter(name = "kitName")String kitName, @Parameter(name = "target") Player target) {

        if (kitName.equalsIgnoreCase("Bunny") || kitName.equalsIgnoreCase("Easter")) {
            kitName = "Spring";
        }

        final ItemStack itemStack = ItemBuilder.of(Material.BOOK).name(ChatColor.GOLD + ChatColor.BOLD.toString() + "Clickable Kit")
                .setLore(Collections.singletonList(ChatColor.translate("&7Right Click to use the &f" + kitName + " &7kit."))).build();

        sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " a " + kitName + " clickable kit.");
        target.getInventory().addItem(itemStack);
    }


    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (!event.getAction().name().contains("RIGHT") || !isSimilar(itemStack)) {
            return;
        }

        event.setCancelled(true);

        final String kitName = ChatColor.stripColor(itemStack.getItemMeta().getLore().get(0).replace("Right Click to use the ", "").replace("kit.", ""));

        if (SpawnTagHandler.isTagged(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not use a clickable kit in Combat!");
            return;
        }

        if (!SugeListener.canUseKit(player, true)) {
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatColor.RED + "You have used a kit book!");

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), "kit apply " + kitName + player.getName());
   }

   public static boolean isSimilar(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() != Material.BOOK || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null || itemStack.getItemMeta().getLore() == null || !itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + ChatColor.BOLD.toString() + "Clickable Kit")) {
            return false;
        }
        return (itemStack.getItemMeta().getLore().get(0) == null || itemStack.getItemMeta().getLore().get(0).contains(ChatColor.GRAY + "Right Click to use the " + ChatColor.WHITE));
   }

}