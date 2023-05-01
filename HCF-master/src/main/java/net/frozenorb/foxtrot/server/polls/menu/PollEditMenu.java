package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PollEditMenu extends Menu {

    private Poll poll;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.PAPER).name(ChatColor.WHITE + "Results & Options").setLore(Arrays.asList("", ChatColor.GRAY + "Click to view all options and its results.")).build();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }
        });

        toReturn.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return ItemBuilder.of(Material.REDSTONE_BLOCK).name(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "End Poll").setLore(Arrays.asList("", ChatColor.GRAY + "Click to end the poll")).build();
            }

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
                poll.setActive(false);
                player.sendMessage(ChatColor.RED + "Successfully ended the poll.");
            }
        });

        return toReturn;
    }

    @Override
    public String getTitle(Player player) {
        return "Editing Poll";
    }

    @Override
    public int size(Player player) {
        return 27;
    }
}
