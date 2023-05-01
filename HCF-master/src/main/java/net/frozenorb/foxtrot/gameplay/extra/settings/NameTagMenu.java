package net.frozenorb.foxtrot.gameplay.extra.settings;

import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameTagMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return "Name Tag Colors";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
        }

        final ChatColor teamColor = Foxtrot.getInstance().getTeamColorMap().getChatColor(player.getUniqueId());
        final ChatColor enemyColor = Foxtrot.getInstance().getEnemyColorMap().getChatColor(player.getUniqueId());
        final ChatColor archerTagColor = Foxtrot.getInstance().getArcherTagColorMap().getChatColor(player.getUniqueId());
        final ChatColor focusColor = Foxtrot.getInstance().getFocusColorMap().getChatColor(player.getUniqueId());
        final ChatColor teamFocusColor = Foxtrot.getInstance().getTeamFocusColorMap().getChatColor(player.getUniqueId());

        toReturn.put(11, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Team Name Tag Color";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Modify your teammates");
                toReturn.add(ChatColor.GRAY + "name tag color for just you");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Color: &c" + teamColor + WordUtils.capitalizeFully(teamColor.name().replace("_", " "))));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to modify the name tag color");
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return ColorUtil.COLOR_MAP.get(teamColor).getDyeData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NameTagColorMenu(NameTagType.TEAM).openMenu(player);
            }
        });
        toReturn.put(12, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Archer Tag Name Tag Color";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Modify your archer tag");
                toReturn.add(ChatColor.GRAY + "name tag color for just you");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Color: &c" + archerTagColor + WordUtils.capitalizeFully(archerTagColor.name().replace("_", " "))));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to modify the name tag color");
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return ColorUtil.COLOR_MAP.get(archerTagColor).getDyeData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NameTagColorMenu(NameTagType.ARCHER_TAGS).openMenu(player);
            }
        });
        toReturn.put(13, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Enemy Name Tag Color";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Modify your enemies");
                toReturn.add(ChatColor.GRAY + "name tag color for just you");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Color: &c" + enemyColor + WordUtils.capitalizeFully(enemyColor.name().replace("_", " "))));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to modify this name tag color");
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return ColorUtil.COLOR_MAP.get(enemyColor).getDyeData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NameTagColorMenu(NameTagType.ENEMY).openMenu(player);
            }
        });
        toReturn.put(14, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Team Focus Name Tag Color";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Modify your team focus");
                toReturn.add(ChatColor.GRAY + "name tag color for just you");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Color: &c" + teamFocusColor + WordUtils.capitalizeFully(teamFocusColor.name().replace("_", " "))));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to modify the name tag color");
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return ColorUtil.COLOR_MAP.get(teamFocusColor).getDyeData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NameTagColorMenu(NameTagType.TEAM_FOCUS).openMenu(player);
            }
        });
        toReturn.put(15, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Focus Name Tag Color";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();
                toReturn.add(ChatColor.GRAY + "Modify your focused player");
                toReturn.add(ChatColor.GRAY + "name tag color for just you");
                toReturn.add("");
                toReturn.add(ChatColor.translate("&4&l┃ &fCurrent Color: &c" + focusColor + WordUtils.capitalizeFully(focusColor.name().replace("_", " "))));
                toReturn.add("");
                toReturn.add(ChatColor.GREEN + "Click to modify the name tag color");
                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.INK_SACK;
            }

            @Override
            public byte getDamageValue(Player player) {
                return ColorUtil.COLOR_MAP.get(focusColor).getDyeData();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new NameTagColorMenu(NameTagType.FOCUS).openMenu(player);
            }
        });

        return toReturn;
    }
}
