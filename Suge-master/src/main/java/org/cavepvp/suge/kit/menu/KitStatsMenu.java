package org.cavepvp.suge.kit.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitStatsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Kit Stats";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Kit kit : Suge.getInstance().getKitHandler().getKits().values()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return CC.translate(kit.getDisplayName());
                }

                @Override
                public List<String> getDescription(Player player) {
                    return Collections.singletonList(ChatColor.GOLD + "Uses: " + ChatColor.WHITE + Suge.getInstance().getKitHandler().getKitUses().getOrDefault(kit, 0));
                }

                @Override
                public Material getMaterial(Player player) {
                    return kit.getMaterial();
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) kit.getDamage();
                }

                @Override
                public int getAmount(Player player) {
                    return Suge.getInstance().getKitHandler().getKitUses().getOrDefault(kit, 1);
                }
            });
        }

        return toReturn;
    }
}
