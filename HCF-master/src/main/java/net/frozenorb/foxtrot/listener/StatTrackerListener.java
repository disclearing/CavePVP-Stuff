package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.deathmessage.event.PlayerKilledEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatTrackerListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(PlayerKilledEvent event) {

        final Player victim = event.getVictim();
        final Player killer = event.getKiller();

        final ItemStack itemStack = killer.getItemInHand().clone();

        if (itemStack == null || itemStack.getType() != Material.DIAMOND_SWORD || itemStack.getItemMeta() == null) {
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = new ArrayList<>();

        if (itemMeta.getLore() != null) {
            lore = itemMeta.getLore();
        }

        final List<String> kills = lore.stream().filter(it -> it.contains(ChatColor.YELLOW + "killed " + ChatColor.WHITE)).collect(Collectors.toList());

        final Optional<String> killCountLore = lore.stream().filter(it -> it.startsWith(ChatColor.GOLD + ChatColor.BOLD.toString() + "Kills: ")).findFirst();

        if (!killCountLore.isPresent()) {
            lore.add(ChatColor.translate("&6&lKills: &f1"));
        } else {
            lore.set(lore.indexOf(killCountLore.get()), ChatColor.translate("&6&lKills: &f" + (Integer.parseInt(ChatColor.stripColor(killCountLore.get()).replace("Kills: ", ""))+1)));
        }

        kills.forEach(lore::remove);

        kills.add(0, ChatColor.translate("&f" + killer.getDisplayName() + " &ekilled &f" + victim.getDisplayName()));

        if (kills.size() > 3) {
            kills.remove(kills.size()-1);
        }

        lore.addAll(kills);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        killer.setItemInHand(itemStack);
    }

}
