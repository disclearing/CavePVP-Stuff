package net.frozenorb.foxtrot.server.deaths.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class DeathMainMenu extends Menu {
    private OfflinePlayer target;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Past Deaths";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to view all of " + target.getName() + "'s past kills");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.REDSTONE_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new DeathsMenu(target, new SimpleDateFormat("M dd yyyy h:mm a")).openMenu(player);
            }
        });

        toReturn.put(6, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Past Kills";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to view all of " + target.getName() + "'s past kills");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.EMERALD_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new KillsMenu(target, new SimpleDateFormat("M dd yyyy h:mm a")).openMenu(player);
            }
        });

        return toReturn;
    }

    @Override
    public String getTitle(Player player) {
        return "Select an option";
    }
}
