package net.frozenorb.foxtrot.gameplay.clickitem.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.BossHandler;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItem;
import net.frozenorb.foxtrot.server.SpawnerType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RandomSpawner extends ClickItem {

    @Override
    public String getId() {
        return "random-spawner";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Random Spawner";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN + "Right Click to receive a random spawner!");

        return toReturn;
    }

    @Override
    public Material getMaterial() {
        return Material.MOB_SPAWNER;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        boolean activate = this.redeem(event.getPlayer(), event);

        if (!activate) {
            return;
        }

        final List<SpawnerType> spawners = Arrays.stream(SpawnerType.values()).collect(Collectors.toList());
        final SpawnerType spawnerType = spawners.get(ThreadLocalRandom.current().nextInt(spawners.size()));

        ItemStack drop = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta meta = drop.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + StringUtils.capitaliseAllWords(spawnerType.getDisplayName().toLowerCase().replaceAll("_", " ")) + " Spawner");
        drop.setItemMeta(meta);

        player.getInventory().addItem(drop.clone());
        player.sendMessage(ChatColor.translate("&aYou have received a &f" + spawnerType.getDisplayName() + " Spawner&a!"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
    }
}
