package net.frozenorb.foxtrot.gameplay.extra.settings;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class SettingsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 45; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        for (SettingType value : SettingType.values()) {
            toReturn.put(value.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.BOLD + value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> lore = new ArrayList<>(value.getDescription());
                    if (value.equals(SettingType.DTR_DISPLAY)) {
                        lore.add("");
                        lore.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Explanation");
                        lore.add(ChatColor.GRAY + "Concept is the same, just how");
                        lore.add(ChatColor.GRAY + "its shown is different. If you're");
                        lore.add(ChatColor.GRAY + "3 DTR it will show as 3 green hearts.");
                        lore.add("");
                        lore.add(ChatColor.translate("&4&l┃ &fDTR Example: &a2.02/3.03 DTR"));
                        lore.add(ChatColor.translate("&4&l┃ &fDTR in Hearts: &a&l❤❤❤&7&l❤"));
                        lore.add("");
                        lore.add(ChatColor.translate("&a❤: &cCurrent DTR"));
                        lore.add(ChatColor.translate("&7❤: &cMissing DTR"));
                        lore.add(ChatColor.translate("&4❤: &cRaidable"));
                        lore.add(ChatColor.translate("&6❤: &cRegenerating DTR"));
                        lore.add(ChatColor.translate("&e❤: &c1 DTR"));
                    }
                    lore.add("");
                    lore.add(ChatColor.translate("&4&l┃ &fStatus: " + (value.isEnabled(player) ? "&aEnabled" : "&cDisabled")));
                    lore.add("");

                    if (!value.isEnabled(player)) {
                        lore.add(ChatColor.GREEN + "Click to enable " + ChatColor.stripColor(value.getDisplayName()));
                    } else {
                        lore.add(ChatColor.GREEN + "Click to disable " + ChatColor.stripColor(value.getDisplayName()));
                    }
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return value.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    value.toggle(player);
                }
            });
        }

        return toReturn;
    }
}
