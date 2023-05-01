package cc.fyre.neutron.prefix.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixListMenu extends Menu {

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Prefixes";
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Prefix prefix : Neutron.getInstance().getPrefixHandler().getSortedValueCache()) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return prefix.getName();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.GOLD + "Weight: " + ChatColor.WHITE + prefix.getWeight());
                    toReturn.add(ChatColor.GOLD + "Display: " + prefix.getDisplay());
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.WOOL;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return 0;
                }

            });

        }

        return toReturn;
    }
}
