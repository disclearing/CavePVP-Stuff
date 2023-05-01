package cc.fyre.neutron.profile.menu.grants;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.server.fetch.ServerGroup;
import lombok.AllArgsConstructor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ConfirmGrantMenu extends Menu {
    private Profile target;
    private Rank rank;
    private String reason;
    private DurationWrapper durationWrapper;

    @Override
    public String getTitle(Player player) {
        return "Confirm Grant";
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

        long duration = durationWrapper.getDuration();

        toReturn.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Confirm Grant";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> description = new ArrayList<>();

                description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
                description.add(ChatColor.GREEN + "Click to add the " + ChatColor.WHITE + rank.getFancyName() + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");
                description.add("");
                description.add(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + reason);
                description.add(ChatColor.YELLOW + "Duration: " + ChatColor.WHITE + (!durationWrapper.isPermanent() ? TimeUtils.formatIntoDetailedString((int) (duration/1000)) : "Permanent"));
                description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
                return description;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.DIAMOND_SWORD;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                player.chat("/ogrant " + target.getName() + " " + rank.getName() + " " + durationWrapper.getSource() + " " + reason);
            }
        });

        return toReturn;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
