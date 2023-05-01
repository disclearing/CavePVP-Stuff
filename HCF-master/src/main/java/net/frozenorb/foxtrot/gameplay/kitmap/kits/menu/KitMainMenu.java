package net.frozenorb.foxtrot.gameplay.kitmap.kits.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class KitMainMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Select a Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (DefaultKit defaultKit : Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKits()) {
            toReturn.put(10 + toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.translate(defaultKit.getDisplayName() + " Kit");
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to apply this kit!");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return defaultKit.getIcon().getType();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.chat("/" + defaultKit.getName());
                }
            });
        }
        toReturn.put(10 + toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "God Kits";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList("", ChatColor.GREEN + "Click to view all God Kits!");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_BLOCK;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/gkit");
            }
        });

        return toReturn;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
