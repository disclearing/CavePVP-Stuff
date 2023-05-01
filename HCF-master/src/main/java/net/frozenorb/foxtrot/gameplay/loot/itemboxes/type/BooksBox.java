package net.frozenorb.foxtrot.gameplay.loot.itemboxes.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBox;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.EnchantHandler;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.Tier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class BooksBox extends ItemBox {

    @Override
    public String getId() {
        return "BooksBox";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Books Box";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Get a random custom enchant book!");
        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Right Click to redeem this box");

        return toReturn;
    }

    @Override
    public Material getMaterial() {
        return Material.CHEST;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        boolean activate = this.redeem(event.getPlayer(), event);

        if (!activate) {
            return;
        }

        final EnchantHandler enchantHandler = Suge.getInstance().getEnchantHandler();
        final List<CustomEnchant> customEnchants = enchantHandler.getCustomEnchants().stream().filter(it -> it.getLevel() != Tier.CAVE).collect(Collectors.toList());
        final CustomEnchant customEnchant = customEnchants.get(ThreadLocalRandom.current().nextInt(0, customEnchants.size()));

        Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "ce givebook " + player.getName() + " " + customEnchant.getName() + " " + customEnchant.getAmplifier());
    }
}
