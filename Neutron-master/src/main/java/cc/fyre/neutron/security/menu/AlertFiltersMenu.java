package cc.fyre.neutron.security.menu;

import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class AlertFiltersMenu extends Menu {
    private UUID target;
    private UUID victim;
    private String server;
    private boolean urgent;

    @Override
    public String getTitle(Player player) {
        return "Filters";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (AlertType value : AlertType.values()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return value.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    new AlertsMenu(target, victim, server, value, urgent).openMenu(player);
                }
            });
        }

        toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "None";
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.TNT;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new AlertsMenu(target, victim, server, null, urgent).openMenu(player);
            }
        });

        return toReturn;
    }
}
