package cc.fyre.neutron.command.profile.holiday.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.command.profile.holiday.type.HolidayType;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidayMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Choose a Holiday";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        for (int i = 0; i < 36; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
        }

        for (HolidayType value : HolidayType.values()) {
            toReturn.put(value.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.RED + "Chat Preview:");
                    lore.add(ChatColor.translateAlternateColorCodes('&',value.getPrefix() + " " + value.getDisplayColor() + player.getName() + "&7: " + value.getChatColor() + "Hello World!"));
                    lore.add("");

                    if (profile.getHolidayType() != value) {
                        lore.add(ChatColor.GREEN + "Click here to select the " + ChatColor.stripColor(value.getDisplayName()) + ".");
                    } else {
                        lore.add(ChatColor.RED + "Click to deactivate the " + ChatColor.stripColor(value.getDisplayName()) + ".");
                    }
                    return lore;
                }

                @Override
                public boolean isGlow() {
                    return profile.getHolidayType() != null && profile.getHolidayType() == value;
                }

                @Override
                public Material getMaterial(Player player) {
                    return value.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    if (profile == null) {
                        player.sendMessage(ChatColor.RED + "Failed to load your profile! Contact an administrator immediately!");
                        return;
                    }

                    if (!player.hasPermission("command.holidayprefix")) {
                        player.sendMessage(ChatColor.RED + "No permission.");
                        return;
                    }

                    if (profile.getHolidayType() == value) {
                        profile.setHolidayType(null);
                        player.sendMessage(ChatColor.RED + "Deactivated your holiday type.");
                        return;
                    }

                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    player.sendMessage(ChatColor.GREEN + "Changed your holiday style to " + value.getDisplayName() + ChatColor.GREEN + "!");
                    profile.setChatColor(value.getChatColor());
                    profile.setHolidayType(value);
                    profile.save();
                }
            });
        }

        return toReturn;
    }
}
