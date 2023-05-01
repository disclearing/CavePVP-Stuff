package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PollResultsMenu extends PaginatedMenu {

    private Poll poll;

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (String option : poll.getOptions()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(Material.PAPER).name(option + " " + ChatColor.GRAY + "[" + poll.getVotes().values().stream().filter(it -> it.equalsIgnoreCase(option)).count() + "]").build();
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
                    new PollEditMenu(poll);
                }
            });
        }

        return toReturn;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.DARK_GRAY + "Poll Result";
    }
}
