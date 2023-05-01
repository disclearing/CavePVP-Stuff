package cc.fyre.neutron.prevention.gui;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prevention.impl.Prevention;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PreventionGUI extends PaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.GREEN + "Unresolved Alerts";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> toReturn = Maps.newHashMap();
        for(Prevention prevention : Neutron.getInstance().getPreventionHandler().getPreventionList()) {
            String name = Proton.getInstance().getUuidCache().name(prevention.getUuid());
            if(!prevention.isResolved()) {
                toReturn.put(toReturn.size(), new Button() {
                    @Override
                    public String getName(Player var1) {
                        return ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(prevention.getTime()));
                    }

                    @Override
                    public List<String> getDescription(Player var1) {
                        List<String> toReturn = new ArrayList<>();
                        toReturn.add(ChatColor.YELLOW + "User: " + name);
                        toReturn.add(ChatColor.YELLOW + "Command: " + prevention.getCommand());
                        toReturn.add(ChatColor.YELLOW + "Time: " + TimeUtils.formatIntoCalendarString(new Date(prevention.getTime())));
                        return toReturn;
                    }

                    @Override
                    public Material getMaterial(Player var1) {
                        return Material.PAPER;
                    }
                    @Override
                    public void clicked(Player player, int slot, ClickType clickType) {
                       new PreventionGUIYON(prevention).openMenu(player);
                    }
                });
            }
        }
        return toReturn;
    }

}
