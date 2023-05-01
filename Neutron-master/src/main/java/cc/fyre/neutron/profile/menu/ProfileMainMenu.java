package cc.fyre.neutron.profile.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
public class ProfileMainMenu extends Menu {
    private String target;

    @Override
    public String getTitle(Player player) {
        return target;
    }

    @Override
    public int size(Player player) {
        return 27;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Punishments";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to view " + target + "'s punishments.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.IRON_FENCE;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/c " + target);
            }
        });

        toReturn.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Grants";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to view " + target + "'s grants.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BEACON;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/grants " + target);
            }
        });

        return toReturn;
    }
}
