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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ClickableKitBox extends ItemBox {

    @Override
    public String getId() {
        return "ClickableKitBox";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Clickable Kit Box";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Get a random clickable kit!");
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

        final List<String> clickableKits = Suge.getInstance().getKitHandler().getKits().keySet().stream().filter(it -> !it.equalsIgnoreCase("Starter") && !it.equalsIgnoreCase("Weekly") && !it.equalsIgnoreCase("NewSeasonal") && !it.equalsIgnoreCase("Builder")).collect(Collectors.toList());
        final String clickableKit = clickableKits.get(ThreadLocalRandom.current().nextInt(0, clickableKits.size()));

        Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "clickablekit give " + clickableKit + " " + player.getName());
    }
}
