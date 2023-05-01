package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MultiplePollsMenu extends PaginatedMenu {

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Poll poll : Foxtrot.getInstance().getPollHandler().getPolls()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(Material.EMERALD).name(ChatColor.GREEN + ChatColor.BOLD.toString() + poll.getQuestion()).setLore(Arrays.asList("", ChatColor.GRAY + "Click to vote on this poll.")).build();
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
                    player.closeInventory();

                    final Optional<Poll> optionalPoll = Foxtrot.getInstance().getPollHandler().findByQuestion(poll.getQuestion());

                    optionalPoll.ifPresent(it -> new VoteMenu(poll).openMenu(player));
                }
            });
        }

        return toReturn;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.DARK_GRAY + "Which Poll";
    }
}
