package cc.fyre.neutron.security.menu;

import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.universe.UniverseAPI;
import cc.fyre.universe.server.Server;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class ServersFilterMenu extends Menu {
    private UUID target;
    private UUID victim;
    private boolean urgent;
    private AlertType alertType;

    @Override
    public String getTitle(Player player) {
        return "Servers";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Server server : UniverseAPI.getServers()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.WHITE + server.getName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&6&l┃ &fPort: &e" + server.getPort()));
                    toReturn.add(ChatColor.translate("&6&l┃ &fStatus: &e" + server.getStatus().getDisplayName()));
                    toReturn.add(ChatColor.translate("&6&l┃ &fServer Group: &e" + server.getGroup().getName()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to select this server.");
                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.BEACON;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    new AlertsMenu(target, victim, server.getName(), alertType, urgent).openMenu(player);
                }
            });
        }

        return toReturn;
    }
}
