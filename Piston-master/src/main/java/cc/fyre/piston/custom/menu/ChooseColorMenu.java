package cc.fyre.piston.custom.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class ChooseColorMenu extends Menu {
    private Profile profile;
    private String display;

    public static List<ChatColor> colors = Arrays.asList(ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.RED, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.GOLD, ChatColor.BLUE, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED);
    public static Map<ChatColor, ChatColor> colorMatch = new HashMap<>();

    @Override
    public String getTitle(Player player) {
        return "Choose a Color";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (ChatColor chatColor : colors) {

            final ChatColor opposite = colorMatch.get(chatColor);

            toReturn.put(toReturn.size(),new Button() {

                final String prefix = "&8[" + opposite + "&ki" + chatColor + ChatColor.BOLD + display + opposite + "&ki&8] " + chatColor;

                @Override
                public String getName(Player player) {
                    return chatColor + chatColor.name();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Example Display:");
                    toReturn.add(ChatColor.translate(prefix + player.getName() + "&7: &fHello World!"));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to select this prefix");
                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.INK_SACK;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return ColorUtil.COLOR_MAP.get(chatColor).getDyeData();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    profile.setCustomPrefix(prefix);
                    profile.setChatColor(opposite);
                    profile.save();

                    player.closeInventory();
                    player.sendMessage(ChatColor.translate("&aSuccessfully changed your custom prefix to " + prefix + "&a!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                }

            });
        }

        return toReturn;
    }

    static {
        colorMatch.put(ChatColor.DARK_RED, ChatColor.RED);
        colorMatch.put(ChatColor.AQUA, ChatColor.WHITE);
        colorMatch.put(ChatColor.GREEN, ChatColor.AQUA);
        colorMatch.put(ChatColor.YELLOW, ChatColor.WHITE);
        colorMatch.put(ChatColor.LIGHT_PURPLE, ChatColor.WHITE);
        colorMatch.put(ChatColor.DARK_BLUE, ChatColor.BLUE);
        colorMatch.put(ChatColor.DARK_GREEN, ChatColor.GREEN);
        colorMatch.put(ChatColor.DARK_AQUA, ChatColor.AQUA);
        colorMatch.put(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);
        colorMatch.put(ChatColor.DARK_GRAY, ChatColor.GRAY);
        colorMatch.put(ChatColor.GOLD, ChatColor.YELLOW);
        colorMatch.put(ChatColor.RED, ChatColor.WHITE);
        colorMatch.put(ChatColor.BLUE, ChatColor.AQUA);
    }
}
