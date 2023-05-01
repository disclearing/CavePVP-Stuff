package cc.fyre.piston.custom.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ChooseRankMenu extends Menu {
    private Profile profile;

    @Override
    public String getTitle(Player player) {
        return "Choose a Rank";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Rank rank : Neutron.getInstance().getRankHandler().getSortedValueCache().stream().filter(it -> it.hasMetaData("DONATOR")).collect(Collectors.toList())) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return rank.getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&lExample:"));
                    toReturn.add(ChatColor.translate(rank.getPrefix() + " " + player.getName() + "&7: &fHello World!"));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to select the " + rank.getName() + " prefix!");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.INK_SACK;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return ColorUtil.COLOR_MAP.get(rank.getColor()).getDyeData();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    profile.setCustomPrefix(rank.getPrefix() + " ");
                    profile.save();

                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                    player.sendMessage(ChatColor.GREEN + "Selected the " + rank.getFancyName() + ChatColor.GREEN + " prefix!");

                    player.closeInventory();
                    new CustomRankMainMenu(profile).openMenu(player);
                }
            });
        }

        return toReturn;
    }
}
