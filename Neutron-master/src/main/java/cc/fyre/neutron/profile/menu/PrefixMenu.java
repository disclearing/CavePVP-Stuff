package cc.fyre.neutron.profile.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PrefixMenu extends PaginatedMenu {

    @Getter private Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Select a prefix.";
    }

    @Override
    public Map<Integer,Button> getGlobalButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        toReturn.put(4,new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Click to reset your prefix.";
            }

            @Override
            public List<String> getDescription(Player player) {
                return new ArrayList<>();
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.BED;
            }

            @Override
            public void clicked(Player player,int slot,ClickType clickType) {

                if (profile.getActivePrefix() != null) {
                    profile.setActivePrefix(null);
                    profile.save();
                }

                player.sendMessage(ChatColor.GOLD + "You have reset your prefix.");
                player.closeInventory();
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Prefix prefix : Neutron.getInstance().getPrefixHandler().getSortedValueCache()) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return prefix.getDisplay();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    if (player.hasPermission("prefix." + prefix.getName().toLowerCase()) || player.hasPermission("prefix.*")) {
                        toReturn.add(ChatColor.GREEN + "Click to select this prefix.");
                    } else {
                        toReturn.add(ChatColor.RED + "You do not have access to this prefix.");
                    }

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {

                    if (player.hasPermission("prefix." + prefix.getName().toLowerCase()) || player.hasPermission("prefix.*")) {
                        return Material.WOOL;
                    }

                    return Material.BEDROCK;
                }

                @Override
                public byte getDamageValue(Player player) {

                    if (player.hasPermission("prefix." + prefix.getName().toLowerCase()) || player.hasPermission("prefix.*")) {
                        return 13;
                    }

                    return 0;
                }

                @Override
                public void clicked(Player player,int slot,ClickType clickType) {

                    if (player.hasPermission("prefix." + prefix.getName().toLowerCase()) || player.hasPermission("prefix.*")) {

                        if (profile.getActivePrefix() == null || !profile.getActivePrefix().getUuid().equals(prefix.getUuid())) {
                            profile.setActivePrefix(prefix);
                            profile.save();
                        }

                        player.sendMessage(ChatColor.GOLD + "Changed your prefix to: " + ChatColor.WHITE + prefix.getDisplay());
                        player.closeInventory();
                        return;
                    }

                    player.closeInventory();
                    player.sendMessage(Proton.getInstance().getCommandHandler().getCommandConfiguration().getNoPermissionMessage());
                }
            });

        }

        return toReturn;
    }

}
