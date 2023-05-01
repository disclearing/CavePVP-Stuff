package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnerShopMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.*;

public class SellAllListener implements Listener {
    public static Map<Material, Double> sellableItems = new HashMap<>();

    public SellAllListener() {
        sellableItems.put(Material.EMERALD_BLOCK, 50.0);
        sellableItems.put(Material.DIAMOND_BLOCK, 50.0);
        sellableItems.put(Material.GOLD_BLOCK, 31.25);
        sellableItems.put(Material.IRON_BLOCK, 31.25);
        sellableItems.put(Material.REDSTONE_BLOCK, 15.625);
        sellableItems.put(Material.LAPIS_BLOCK, 15.625);
    }

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            return;
        }

        if (!(block.getState() instanceof Sign)) {
            return;
        }

        final Sign sign = (Sign) block.getState();


        if (sign.getLine(1) != null && sign.getLine(1).equalsIgnoreCase(ChatColor.DARK_RED + "[Spawners]")) {
            new SpawnerShopMenu().openMenu(player);
            return;
        }

        if (sign.getLine(0) == null || !sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "- Sell -") || sign.getLine(2) == null || !sign.getLine(2).equalsIgnoreCase("Valuables")) {
            return;
        }

        final List<ItemStack> soldItems = new ArrayList<>();

        int itemsSold = 0;
        double amountPaid = 0;

        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType() == Material.AIR || content.getAmount() <= 0) {
                continue;
            }

            if (content.getItemMeta() != null && content.getItemMeta().hasLore()) {
                continue;
            }

            if (!this.sellableItems.containsKey(content.getType())) {
                continue;
            }

            final double cost = this.sellableItems.get(content.getType());

            soldItems.add(content);

            itemsSold += content.getAmount();
            amountPaid += content.getAmount()*cost;
        }

        if (soldItems.size() == 0) {
            player.sendMessage(ChatColor.RED + "You do not have any valuables to sell on you!");

            Foxtrot.getInstance().getServerHandler().showSignPacket(player, sign,
                    "§cYou do not",
                    "§chave any",
                    sign.getLine(2),
                    "§con you!"
            );
            return;
        }

        soldItems.forEach(it -> player.getInventory().remove(it));

        Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), Math.floor(amountPaid));

        player.sendMessage(ChatColor.translate("&6You sold &f" + itemsSold + " blocks &6for &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(Math.floor(amountPaid))));

        Foxtrot.getInstance().getServerHandler().showSignPacket(player, sign,
                "§aSOLD§r " + itemsSold,
                "for §a$" + NumberFormat.getNumberInstance(Locale.US).format(Math.floor(amountPaid)),
                "New Balance:",
                "§a$" + NumberFormat.getNumberInstance(Locale.US).format((int) Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()))
        );
    }

}
